package com.conan.router;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.conan.router.library.Router;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router.getInstance().openScheme(MainActivity.this,"router://sample/SampleActivity?sampleName=sample&sampleAge=18",null,-1);
            }
        });
    }
}
