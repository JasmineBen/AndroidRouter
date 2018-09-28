package com.conan.router.anno.bean;

import java.util.List;

import javax.lang.model.element.TypeElement;

public class RouteTarget {
    public String authority;
    public String path;
    public String desc;
    public TypeElement targetActivityElement;
    public List<InjectParameter> params;

    @Override
    public String toString() {
        return "RouteTarget{" +
                "authority='" + authority + '\'' +
                ", path='" + path + '\'' +
                ", desc='" + desc + '\'' +
                ", targetActivityElement=" + targetActivityElement +
                ", params=" + params +
                '}';
    }
}
