package rw.itdevs.brarudi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rw.itdevs.brarudi.utils.Utility;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Application.localeManager.setLocale(base));
        Log.d("BASE", "attachBaseContext");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BASE", "onCreate");
        Utility.resetActivityTitle(this);
    }
}
