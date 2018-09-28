package com.conan.router.anno.bean;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public enum InjectParameterType {
    BYTE,
    SHORT,
    CHAR,
    BOOLEAN,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING;

    public static InjectParameterType parseType(Element fieldElement){
        TypeMirror mirror = fieldElement.asType();
        TypeKind fieldType = mirror.getKind();
        if(fieldType.isPrimitive()){//基本数据类型
            if(fieldType == TypeKind.BYTE){
                return BYTE;
            }
            if(fieldType == TypeKind.SHORT){
                return SHORT;
            }
            if(fieldType == TypeKind.CHAR){
                return CHAR;
            }
            if(fieldType == TypeKind.BOOLEAN){
                return BOOLEAN;
            }
            if(fieldType == TypeKind.INT){
                return INT;
            }
            if(fieldType == TypeKind.LONG){
                return LONG;
            }
            if(fieldType == TypeKind.FLOAT){
                return FLOAT;
            }
            if(fieldType == TypeKind.DOUBLE){
                return DOUBLE;
            }
        }else{//包装类型
            String filedTypeName = mirror.toString();
            if(Byte.class.getCanonicalName().equals(filedTypeName)){
                return BYTE;
            }
            if(Short.class.getCanonicalName().equals(filedTypeName)){
                return SHORT;
            }
            if(Character.class.getCanonicalName().equals(filedTypeName)){
                return CHAR;
            }
            if(Boolean.class.getCanonicalName().equals(filedTypeName)){
                return BOOLEAN;
            }
            if(Integer.class.getCanonicalName().equals(filedTypeName)){
                return INT;
            }
            if(Long.class.getCanonicalName().equals(filedTypeName)){
                return LONG;
            }
            if(Float.class.getCanonicalName().equals(filedTypeName)){
                return FLOAT;
            }
            if(Double.class.getCanonicalName().equals(filedTypeName)){
                return DOUBLE;
            }
            if(String.class.getCanonicalName().equals(filedTypeName)){
                return STRING;
            }

        }
        return null;
    }

    public static InjectParameterType parseType(int type){

        if(BYTE.ordinal() == type){
            return BYTE;
        }
        if(SHORT.ordinal() == type){
            return SHORT;
        }
        if(CHAR.ordinal() == type){
            return CHAR;
        }
        if(BOOLEAN.ordinal() == type){
            return BOOLEAN;
        }
        if(INT.ordinal() == type){
            return INT;
        }
        if(LONG.ordinal() == type){
            return LONG;
        }
        if(FLOAT.ordinal() == type){
            return FLOAT;
        }
        if(DOUBLE.ordinal() == type){
            return DOUBLE;
        }
        if(STRING.ordinal() == type){
            return STRING;
        }
        return null;
    }

}
