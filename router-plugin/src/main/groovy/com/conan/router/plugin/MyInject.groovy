package com.conan.router.plugin

import javassist.*
import org.gradle.api.Project

public class MyInject {

    static def mClassPathList = new ArrayList<JarClassPath>()

    public static void removeClassPath(Project project) {
        if (mClassPathList != null) {
            def pool = ClassPool.getDefault()
            mClassPathList.each {
                try {
                    pool.removeClassPath(it)
                } catch (Exception e) {
                    project.logger.error(e.getMessage())
                }
            }
            mClassPathList.clear()
        }
    }

    public static void injectDir(String path,Project project, List<String> routerParserImplList) {
        String routerImpl = project.router.routerImpl
        if (routerImpl.isEmpty()) {
            return
        }
        File dir = new File(path)
        if (!dir.isDirectory()) {
            return
        }
        if (!routerParserImplList.isEmpty()) {
            generateBaseRouterImplClass(path,project,routerParserImplList)
        }
    }

    private static void generateBaseRouterImplClass(String path,Project project,List<String> routerParserImplList) {
        ClassPool pool = ClassPool.getDefault()
        pool.appendClassPath(path)
//        //project.android.bootClasspath 加入android.jar，否则找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        println(project.router.routerImpl)

        CtClass cc =  pool.makeClass(project.router.routerImpl)
        pool.appendClassPath(new ClassClassPath(this.getClass()))
        CtField cf =  CtField.make('public java.util.Map mRouterParserClass = new java.util.HashMap();', cc)
        cc.addField(cf)
        CtMethod cm = CtNewMethod.make('public void registerRouters(){}',cc)
        cc.addMethod(cm)
        StringBuffer sb = new StringBuffer()
        for (String routerParserImplClassPath : routerParserImplList) {
            int index = routerParserImplClassPath.lastIndexOf('\\')
            String simpleNameWithClass = routerParserImplClassPath.substring(index + 1)
            String simpleNameWithoutClass = simpleNameWithClass.replace('.class', '')
            String authority = "\"" + simpleNameWithoutClass.replace('RouterParserImpl', '') + "\""
            String routerParserImplClassName = "\"" + 'com.conan.router.gen.' + simpleNameWithoutClass + "\""
            String insertStatement = 'mRouterParserClass.put(' + authority + ',' + routerParserImplClassName + ');'
            sb.append(insertStatement).append('\n')
        }
        println(sb.toString())
        cm.insertBefore(sb.toString())

        CtMethod getRoutersMethod = CtNewMethod.make('public java.util.Map getRouters(){return mRouterParserClass;}',cc)
        cc.addMethod(getRoutersMethod)
        cc.writeFile(path)
        cc.detach()
    }

    public static List<String> collectRouterParserImpl(String path){
        def routerParserImpl = new ArrayList<String>()
        File dir = new File(path)
        if (!dir.isDirectory()) {
            return routerParserImpl
        }
        dir.eachFileRecurse {
            File file ->
                String filePath = file.absolutePath
                if(filePath.contains('com\\conan\\router\\gen') && filePath.endsWith('RouterParserImpl.class')){
                    println("collectRouterParserImpl :"+filePath)
                    routerParserImpl.add(filePath)
                }
        }
        return routerParserImpl
    }

    public static void injectJar(String path) {
        println('injectJar:'+path)
        ClassPool pool = ClassPool.getDefault()
        def classPath = new JarClassPath(path)
        mClassPathList.add(classPath)
        pool.appendClassPath(classPath)
    }

}