package com.conan.router.anno.bean;


import com.conan.router.anno.annotation.Inject;

import javax.lang.model.element.Element;

public class InjectParameter {

    public InjectParameterType type;//@Inject注入Field的类型
    public String name;//@Inject注入对象的名称
    public String injectName;//@Inject(name)中的name

    public static InjectParameter fromElement(Element element) {
        InjectParameter parameter = new InjectParameter();
        Inject inject = element.getAnnotation(Inject.class);
        parameter.name = element.getSimpleName().toString();
        parameter.injectName = inject.name();
        parameter.type = InjectParameterType.parseType(element);
        return parameter;
    }

    public static InjectParameter fromField(String name, int type,String injectName) {
        InjectParameter parameter = new InjectParameter();
        parameter.name = name;
        parameter.type = InjectParameterType.parseType(type);
        parameter.injectName = injectName;
        return parameter;
    }

    @Override
    public String toString() {
        return "InjectParameter{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", injectName='" + injectName + '\'' +
                '}';
    }
}
