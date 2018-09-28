package com.conan.router.library;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.conan.router.anno.RouterConatants;
import com.conan.router.anno.bean.InjectParameter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class InjectService {

    public static void inject(Activity activity) {
        try {
            int index = 0;
            while(true) {
                index++;
                Class injectHelperClass = Class.forName(RouterConatants.GENERATION_PACKAGE_NAME + "." + RouterConatants.INJECT_HELPER_CLASS_NAME+index);
                Method getInjectParamsMethod = injectHelperClass.getDeclaredMethod(RouterConatants.GET_INJECT_PARAMS, String.class);
                getInjectParamsMethod.setAccessible(true);
                List<InjectParameter> injectParams = (List<InjectParameter>) getInjectParamsMethod.invoke(injectHelperClass.newInstance(), activity.getClass().getCanonicalName());
                if (injectParams != null && injectParams.size() > 0) {
                    for (InjectParameter inject : injectParams) {
                        Log.i("zpy", inject.toString());
                        Field field = activity.getClass().getDeclaredField(inject.name);
                        field.setAccessible(true);
                        Bundle data = activity.getIntent().getExtras();
                        String paramName = TextUtils.isEmpty(inject.injectName) ? inject.name : inject.injectName;
                        Class paramTypeClass = field.getType().getClass();
                        switch (inject.type) {
                            case STRING:
                                field.set(activity, data.getString(paramName));
                                break;
                            case BOOLEAN:
                                if(Boolean.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getBoolean(paramName));
                                }else {
                                    field.setBoolean(activity, data.getBoolean(paramName));
                                }
                                break;
                            case SHORT:
                                if(Short.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getShort(paramName));
                                }else {
                                    field.setShort(activity, data.getShort(paramName));
                                }
                                break;
                            case FLOAT:
                                if(Float.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getFloat(paramName));
                                }else {
                                    field.setFloat(activity, data.getFloat(paramName));
                                }
                                break;
                            case LONG:
                                if(Long.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getLong(paramName));
                                }else {
                                    field.setLong(activity, data.getLong(paramName));
                                }
                                break;
                            case CHAR:
                                if(Character.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getChar(paramName));
                                }else {
                                    field.setChar(activity, data.getChar(paramName));
                                }
                                break;
                            case BYTE:
                                if(Byte.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getByte(paramName));
                                }else {
                                    field.setByte(activity, data.getByte(paramName));
                                }
                                break;
                            case INT:
                                if(Integer.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getInt(paramName));
                                }else {
                                    field.setInt(activity, data.getInt(paramName));
                                }
                                break;
                            case DOUBLE:
                                if(Double.class.getClass().equals(paramTypeClass)){
                                    field.set(activity, data.getDouble(paramName));
                                }else {
                                    field.setDouble(activity, data.getDouble(paramName));
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("zpy","InjectService "+e.getMessage());
        }
    }

}
