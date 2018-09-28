package com.conan.router.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.conan.router.anno.annotation.Inject;
import com.conan.router.anno.annotation.Route;
import com.conan.router.library.InjectService;
import com.conan.router.library.Router;

@Route(authority = "sample",path = "SampleActivity",desc = "sampleDesc")
public class SampleActivity extends AppCompatActivity{

    @Inject(name = "sampleName")
    String name;

    @Inject(name = "sampleAge")
    int age;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);
        InjectService.inject(this);
        TextView tv = findViewById(R.id.text);
        tv.setText(name+":"+age);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.getInstance().openScheme(SampleActivity.this,"router://app/MainActivity2?name=MainActivity2&age=180",null);

            }
        });
    }
}
