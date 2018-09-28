package com.conan.router.compiler.processor;

import com.conan.router.anno.RouterConatants;
import com.conan.router.anno.annotation.Inject;
import com.conan.router.anno.annotation.Route;
import com.conan.router.anno.bean.InjectParameter;
import com.conan.router.anno.bean.RouteTarget;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {
    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    private List<RouteTarget> mRoutes;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Route.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //初始化我们需要的基础工具
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mRoutes = new ArrayList<>();
        System.out.println("create RouteProcessor init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE,"process:"+mRoutes.size());
        //@Route注解的Element
        Set<? extends Element> routeElementSet = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (routeElementSet != null && routeElementSet.size() > 0) {
            for (Element routeElement : routeElementSet) {
                mMessager.printMessage(Diagnostic.Kind.NOTE,routeElement.getSimpleName());
                //@Route只能注解在Class上
                if (routeElement.getKind() != ElementKind.CLASS) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Route Annotation Can Only Annotate At Class Object");
                    return true;
                }
                //Element对应的信息保存在TypeMirror中
                TypeMirror routeMirror = routeElement.asType();
                if (!mTypeUtils.isSubtype(routeMirror, mElementUtils.getTypeElement("android.app.Activity").asType())) {
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "Route Annotation Can Only Annotate At Activity Class");
                    return true;
                }
                //获取@Route的注解内容
                Route annotation = routeElement.getAnnotation(Route.class);
                RouteTarget routeTarget = new RouteTarget();
                routeTarget.authority = annotation.authority();
                if(routeTarget.authority.isEmpty()){
                    routeTarget.authority = RouterConatants.DEFAULT;
                }
                routeTarget.path = annotation.path();
                routeTarget.desc = annotation.desc();
                if(routeTarget.path.isEmpty()){
                    mMessager.printMessage(Diagnostic.Kind.ERROR, " @Route(path) can not be empty.");
                    return true;
                }
                if(hasConflictRouterTarget(routeTarget)){
                    mMessager.printMessage(Diagnostic.Kind.ERROR, "authority["+routeTarget.authority+"] or path["+routeTarget.path+"] of @Route must be unique.");
                    return true;
                }
                List<InjectParameter> params = new ArrayList<>();
                List<? extends Element> childElementList = routeElement.getEnclosedElements();
                for (Element childElement : childElementList) {
                    //@Route注解的Class，Class中使用@Inject注解的Field表示Route传给Activity的参数
                    if (childElement.getAnnotation(Inject.class) != null) {
                        if (childElement.getKind() == ElementKind.FIELD) {
                            InjectParameter inject = InjectParameter.fromElement(childElement);
                            if(inject.type == null){
                                mMessager.printMessage(Diagnostic.Kind.ERROR, "@Inject field must be[int,Integer,long,Long,short,Short,char,Character,float,Float,byte,Byte,bool,Boolean,double,Double,String].");
                                return true;
                            }
                            params.add(inject);
                        } else {
                            mMessager.printMessage(Diagnostic.Kind.ERROR, "Inject Annotation Can Only Annotate At Field");
                            return true;
                        }
                    }
                }
                routeTarget.params = params;
                routeTarget.targetActivityElement = (TypeElement) routeElement;
                mMessager.printMessage(Diagnostic.Kind.NOTE, routeTarget.toString());
                mRoutes.add(routeTarget);
            }
            generateRouteClass();
            generateInjectHelper();
            return true;
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE,"process false");
        return false;
    }

    /**
     * 根据路由的authority和路由信息生成xxxRouterParserImpl
     */
    private void generateRouteClass() {
        Map<String, List<RouteTarget>> classificationRouterTarget = new HashMap<>();
        //根据authority对@Route进行分类
        for (RouteTarget target : mRoutes) {
            String authority = target.authority.isEmpty() ? RouterConatants.DEFAULT : target.authority;
            String simpleClassName = authority + RouterConatants.ROUTER_PARSER_IMPL;
            if (classificationRouterTarget.get(simpleClassName) == null) {
                classificationRouterTarget.put(simpleClassName, new ArrayList<>());
            }
            classificationRouterTarget.get(simpleClassName).add(target);
        }
        for (Map.Entry<String, List<RouteTarget>> entry : classificationRouterTarget.entrySet()) {
            generateClassificationRouterClass(entry.getKey(), entry.getValue());
        }
    }

    private void generateClassificationRouterClass(String simpleClassName, List<RouteTarget> targets) {
        String pckName = RouterConatants.GENERATION_PACKAGE_NAME;
        TypeElement targetElement = mElementUtils.getTypeElement(pckName+"."+simpleClassName);
        if(targetElement != null){
            mMessager.printMessage(Diagnostic.Kind.ERROR, "multi-modules can not use same authority:"+simpleClassName);
            return;
        }
        //生成的GenRouter继承com.conan.router.library.BaseRouterParser
        TypeElement superElement = mElementUtils.getTypeElement(RouterConatants.BASE_ROUTER_PARSER_FULL_NAME);
        if (superElement == null) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "Can not found "+RouterConatants.BASE_ROUTER_PARSER_FULL_NAME);
            return;
        }
        ClassName superClass = ClassName.get(superElement);
        TypeName returnType = TypeName.VOID;
        MethodSpec.Builder overInitMethodBuilder = MethodSpec.methodBuilder(RouterConatants.INIT_ROUTER)
                .returns(returnType)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        for(int i = 0;i<targets.size();i++) {
            RouteTarget target = targets.get(i);
            if(i == 0) {
                overInitMethodBuilder.addStatement("super." + RouterConatants.INIT_ROUTER + "()");
            }
            overInitMethodBuilder.addStatement("routers.put($S,$T.class)", target.path, ClassName.get(target.targetActivityElement));
            List<InjectParameter> params = target.params;
            if (params != null && params.size() > 0) {
                for (InjectParameter inject : params) {
                    overInitMethodBuilder.addStatement("addParamsTypes($T.class,$S,$L,$S)",
                            ClassName.get(target.targetActivityElement), inject.name, inject.type.ordinal(), inject.injectName);
                }
            }
        }
        MethodSpec overrideIntiMethod = overInitMethodBuilder.build();
        try {
            TypeSpec typeSpec = TypeSpec.classBuilder(simpleClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(overrideIntiMethod)
                    .superclass(superClass).build();
            JavaFile.builder(pckName, typeSpec).build().writeTo(mFiler);
        } catch (Exception e) {

        }
    }

    /**
     * 生成Inject帮助类
     */
    private void generateInjectHelper(){
        if(mRoutes == null || mRoutes.size() <= 0 ){
            return;
        }
        ParameterizedTypeName listType = ParameterizedTypeName.get(List.class,InjectParameter.class);

        ParameterizedTypeName mapType =  ParameterizedTypeName.get(ClassName.get(Map.class),TypeName.get(String.class),listType);
        //成员变量Map<String,List<InjectParameter>> map
        FieldSpec.Builder mapField = FieldSpec.builder(mapType,"map").addModifiers(Modifier.PRIVATE).initializer(CodeBlock.of("new $T<>();",HashMap.class));
        //函数fillInjectParams(String name,int type),用于向map填充内容
        MethodSpec.Builder fillInjectBuild = MethodSpec.methodBuilder("fillInjectParams")
                .returns(TypeName.VOID).addModifiers(Modifier.PRIVATE)
                .addParameter(String.class,"targetClassName")
                .addParameter(String.class,"name")
                .addParameter(Integer.class,"type")
                .addParameter(String.class,"injectName");
        fillInjectBuild.addStatement("if(map.get(targetClassName)==null){\n"+"map.put(targetClassName,new $T<$T>());\n}\n"+"map.get(targetClassName).add(InjectParameter.fromField(name,type,injectName))"
                ,ArrayList.class,InjectParameter.class);
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        //构造函数，在创建对象时调用fillInjectParams初始化map的所有内容
        for(RouteTarget target : mRoutes){
            if(target.params != null && target.params.size() > 0){
                for(InjectParameter parameter : target.params){
                    constructorBuilder.addStatement("fillInjectParams($S,$S,$L,$S)",ClassName.get(target.targetActivityElement),parameter.name,parameter.type.ordinal(),parameter.injectName);
                }
            }
        }
        //函数getInjectParams(String targetClassName)，提供给InjectService使用
        MethodSpec.Builder getInjectParamsBuilder = MethodSpec.methodBuilder(RouterConatants.GET_INJECT_PARAMS).addModifiers(Modifier.PUBLIC);
        getInjectParamsBuilder.returns(listType);
        getInjectParamsBuilder.addParameter(ParameterSpec.builder(String.class,"targetClassName").build());
        getInjectParamsBuilder.addStatement("return map.get(targetClassName)");

        //生成com.conan.router.gen.InjectHelperX.java
        int nextInjectHelperClass = 1;
        while(true){
            TypeElement element = mElementUtils.getTypeElement(RouterConatants.GENERATION_PACKAGE_NAME+"."+RouterConatants.INJECT_HELPER_CLASS_NAME+nextInjectHelperClass);
            if(element != null){
                nextInjectHelperClass ++ ;
            }else{
                break;
            }
        }
        try {
            TypeSpec typeSpec = TypeSpec.classBuilder(RouterConatants.INJECT_HELPER_CLASS_NAME+nextInjectHelperClass)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(mapField.build())
                    .addMethod(constructorBuilder.build())
                    .addMethod(fillInjectBuild.build())
                    .addMethod(getInjectParamsBuilder.build())
                    .build();
            JavaFile.builder(RouterConatants.GENERATION_PACKAGE_NAME, typeSpec).build().writeTo(mFiler);
        } catch (Exception e) {
            mMessager.printMessage(Diagnostic.Kind.NOTE,e.getMessage());
        }
    }


    private boolean hasConflictRouterTarget(RouteTarget routeTarget){
        for(RouteTarget target : mRoutes){
            if(target.authority.equals(routeTarget.authority) && target.path.equals(routeTarget.path)){
                return true;
            }
        }
        return false;
    }
}
