package rw.itdevs.brarudi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import static rw.itdevs.brarudi.utils.LocaleManager.LANGUAGE_FRENCH;
import static rw.itdevs.brarudi.utils.LocaleManager.LANGUAGE_KIRUNDI;

public class LanguageActivity extends BaseActivity {

SharedPreferences preferences;
    CardView rundi,france;
            //,english,tz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.transparent));
        }

        setContentView(R.layout.language);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences=getSharedPreferences("brarudi",0);

        rundi=findViewById(R.id.rundi);
        france=findViewById(R.id.france);
//        english=findViewById(R.id.english);
//        tz=findViewById(R.id.tz);

       if(preferences.contains("logged")){

            Intent i = new Intent(LanguageActivity.this, Main2Activity.class);
            startActivity(i);
            finish();

        }

        rundi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewLocale(LANGUAGE_KIRUNDI, true);

            }
        });
        france.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewLocale(LANGUAGE_FRENCH, true);

            }
        });

//        english.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setNewLocale(LANGUAGE_ENGLISH, true);
//
//            }
//        });
//
//        tz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setNewLocale(LANGUAGE_SWAHILI, true);
//
//            }
//        });
    }

    private boolean setNewLocale(String language, boolean restartProcess) {

        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("logged",true);
        editor.apply();

        Application.localeManager.setNewLocale(this, language);
        Intent i = new Intent(this,LanguageActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            startActivity(new Intent(LanguageActivity.this, Main2Activity.class));
            finish();

        return true;
    }
}
