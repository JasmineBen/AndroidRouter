package com.conan.router.library;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.conan.router.anno.RouterConatants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Router {

    private Map<String,String> mSuperRouterParserClass = new HashMap<>();
    private Map<String,BaseRouterParser> mRouterParser = new HashMap<>();
    private boolean bInited = false;
    private static final Router mInstance = new Router();

    private List<String> mBaseRouterImpl;

    private Router(){
        super();
        mBaseRouterImpl = new ArrayList<>();
    }

    public static Router getInstance(){
        return mInstance;
    }

    private void init(){
        Log.i("zpy","init");
        for(String baseRouterImpl : mBaseRouterImpl){
            Log.i("zpy","openScheme:"+baseRouterImpl);
            try {
                Class implClass = Class.forName(baseRouterImpl);
                Object instance = implClass.newInstance();
                Method openScheme = implClass.getDeclaredMethod("registerRouters");
                openScheme.setAccessible(true);
                openScheme.invoke(instance);
                Method getRouter = implClass.getDeclaredMethod("getRouters");
                Map routers = (Map)getRouter.invoke(instance);
                fillRouterParserClass(routers);
            }catch (Exception e){
                e.printStackTrace();
                Log.i("zpy","init:"+e.getMessage());
            }
        }
        for(Map.Entry<String,String> entry : mSuperRouterParserClass.entrySet()){
            String routerClassName = entry.getValue();
            try {
                Class routerClass = Class.forName(routerClassName);
                mRouterParser.put(routerClassName,(BaseRouterParser)routerClass.newInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        bInited = true;
    }


    protected void fillRouterParserClass(Map map){
        Log.i("zpy","fillRouterParserClass");
        if(map != null){
            Log.i("zpy","fillRouterParserClass1");
            Set keys = map.keySet();
            Iterator it = keys.iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                mSuperRouterParserClass.put(key,(String)map.get(key));
            }
        }
    }

    public boolean openScheme(Context context, String url, Bundle data) {
        if(!bInited){
            init();
        }
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        String authority = TextUtils.isEmpty(uri.getAuthority()) ? RouterConatants.DEFAULT : uri.getAuthority();
        if(!TextUtils.isEmpty(path)){
            String routerParserClassName = mSuperRouterParserClass.get(authority);
            Log.i("zpy","BaseRouter openScheme:"+mRouterParser.get(routerParserClassName));
            if(routerParserClassName != null && mRouterParser.get(routerParserClassName) != null
                    && mRouterParser.get(routerParserClassName).openScheme(context,url,data)){
                return true;
            }
        }
        return false;
    }

    public void addBaseRouterImpl(String fullClassName){
        if(!mBaseRouterImpl.contains(fullClassName)){
            mBaseRouterImpl.add(fullClassName);
        }
    }

}
