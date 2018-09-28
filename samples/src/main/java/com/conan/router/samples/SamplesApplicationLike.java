package com.conan.router.samples;

import com.conan.router.library.Router;

public class SamplesApplicationLike {

    private static SamplesApplicationLike mInstance = new SamplesApplicationLike();

    private SamplesApplicationLike(){}

    public static SamplesApplicationLike getInstance(){
        return mInstance;
    }

    public void onCreate(){
        Router.getInstance().addBaseRouterImpl("com.conan.router.samples.SampleRouter");
    }
}
