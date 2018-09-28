package com.conan.router;

import android.app.Application;

import com.conan.router.library.Router;
import com.conan.router.samples.SamplesApplicationLike;

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Router.getInstance().addBaseRouterImpl("com.conan.router.AppRouter");
        SamplesApplicationLike.getInstance().onCreate();
    }
}
