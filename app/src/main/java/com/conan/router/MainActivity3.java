package com.conan.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.conan.router.anno.annotation.Inject;
import com.conan.router.anno.annotation.Route;
import com.conan.router.library.InjectService;
import com.conan.router.library.Router;

@Route(authority = "app",path = "MainActivity3",desc = "MainActivity3")
public class MainActivity3 extends AppCompatActivity {

    @Inject(name = "name1")
    String name;

    @Inject(name = "age1")
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        InjectService.inject(this);
        TextView tv = findViewById(com.conan.router.samples.R.id.text);
        tv.setText(name+":"+age);
    }
}
