package com.example.testbrounie.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.testbrounie.R;

public class SplashActivity extends AppCompatActivity {
    //set variables declaration
    private ImageView ivBrounie;
    private static int splashTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);



        initComponents();
        setEffects();
    }
    /**
     * Method to initialize all components of the class
     */
    private void initComponents() {
        ivBrounie = findViewById(R.id.ivBrounie);
    }
    /**
     * Method to set a effect of a specific component and timeout of splash screen
     */
    private void setEffects() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intMenu = new Intent(SplashActivity.this, MenuActivity.class);
                startActivity(intMenu);
                finish();
            }
        }, splashTime);

        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.splasheffect);
        ivBrounie.startAnimation(splashAnim);
    }
}
