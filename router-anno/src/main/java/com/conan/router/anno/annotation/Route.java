package com.conan.router.anno.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Route {

    /**
     * ymm-crm://view/webview?url=http://www.baidu.com&title=百度&ats=1
     * [scheme:][//authority]/[path][?query][#fragment]
     */

    String authority();//[authority]

    String path();//[path]

    String desc();//描述
}
