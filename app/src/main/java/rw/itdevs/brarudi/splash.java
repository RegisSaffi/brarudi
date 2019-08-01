package rw.itdevs.brarudi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class splash extends BaseActivity {
    private static int SPLASH_TIME_OUT=4000;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        setContentView(R.layout.splash);

        preferences=getSharedPreferences("brarudi",0);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                if(preferences.contains("logged")) {
                    Intent i = new Intent(splash.this, Main2Activity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(splash.this, LanguageActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        },SPLASH_TIME_OUT);
    }
}
