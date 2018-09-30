package com.conan.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.conan.router.anno.annotation.Inject;
import com.conan.router.anno.annotation.Route;
import com.conan.router.library.InjectService;
import com.conan.router.library.Router;

@Route(authority = "app",path = "MainActivity2",desc = "MainActivity2")
public class MainActivity2 extends AppCompatActivity {

    @Inject(name = "")
    String name;

    @Inject(name = "age")
    String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        InjectService.inject(this);
        TextView tv = findViewById(com.conan.router.samples.R.id.text);
        tv.setText(name+":"+age);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.getInstance().openScheme(MainActivity2.this,"router://app/MainActivity3?name1=MainActivity3&age1=1800",null,-1);
            }
        });
    }
}
