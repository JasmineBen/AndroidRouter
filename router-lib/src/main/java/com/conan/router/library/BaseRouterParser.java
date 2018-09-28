package com.conan.router.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.conan.router.anno.bean.InjectParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRouterParser {

    //path对应的Activity
    public Map<String,Class> routers = new HashMap<>();
    public Map<Class,List<InjectParameter>> paramsTypes = new HashMap<>();
    private boolean bInited = false;

    public void initRouters(){
        bInited = true;
    }

    protected void addParamsTypes(Class routeClass,String name,int type,String injectName){
        if(paramsTypes.get(routeClass) == null){
            paramsTypes.put(routeClass,new ArrayList<InjectParameter>());
        }
        paramsTypes.get(routeClass).add(InjectParameter.fromField(name,type,injectName));
    }

    public boolean openScheme(Context context,String url,Bundle data){
        if(!TextUtils.isEmpty(url) && context != null) {
            if(!bInited) {
               initRouters();
            }
            for(Map.Entry<String,Class> entry : routers.entrySet()){
                Log.i("zpy","BaseRouterParser openScheme:"+entry.getKey()+";"+entry.getValue());
            }
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            if(path.startsWith("/")){
                path = path.substring(1);
            }
            Class targetActivity = routers.get(path);
            Log.i("zpy","BaseRouterParser openScheme target:"+targetActivity);
            if(targetActivity != null){
                List<InjectParameter> injectParams = paramsTypes.get(targetActivity);
                Intent intent = new Intent(context,targetActivity);
                intent.putExtras(getUriParams(injectParams,uri,data));
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    private Bundle getUriParams( List<InjectParameter> injectParams,Uri uri,Bundle data){
        if(data == null){
            data = new Bundle();
        }
        if(injectParams != null) {
            for (InjectParameter param : injectParams) {
                String paramName = TextUtils.isEmpty(param.injectName) ? param.name : param.injectName;
                if (uri.getQueryParameter(paramName) == null) {
                    throw new RuntimeException("Cant not find parameter " + param.name + " in uri:" + uri.toString());
                }
                try {
                    String uriParamData = uri.getQueryParameter(paramName);
                    switch (param.type) {
                        case DOUBLE:
                            data.putDouble(paramName, Double.parseDouble(uriParamData));
                            break;
                        case INT:
                            data.putInt(paramName, Integer.parseInt(uriParamData));
                            break;
                        case BYTE:
                            data.putByte(paramName, Byte.parseByte(uriParamData));
                            break;
                        case CHAR:
                            data.putChar(paramName, uriParamData.charAt(0));
                            break;
                        case LONG:
                            data.putLong(paramName, Long.parseLong(uriParamData));
                            break;
                        case FLOAT:
                            data.putFloat(paramName, Float.parseFloat(uriParamData));
                            break;
                        case SHORT:
                            data.putShort(paramName, Short.parseShort(uriParamData));
                            break;
                        case BOOLEAN:
                            data.putBoolean(paramName, Boolean.valueOf(uriParamData));
                            break;
                        case STRING:
                            data.putString(paramName, uriParamData);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Can not cast ");
                }
            }
        }
        return data;
    }
}
