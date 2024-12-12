package br.inf.gr.mediodamemoria;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;

import java.util.Locale;

public class MainApplication extends MultiDexApplication {

    public static LocaleManager localeManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        localeManager = new LocaleManager(this);

        // Use o idioma do dispositivo se não houver preferência salva
        if (localeManager.getLanguage().equals(LocaleManager.LANGUAGE_ENGLISH)) {
            String defaultLanguage = Locale.getDefault().getLanguage();
            localeManager.setNewLocale(this, defaultLanguage);
        } else {
            localeManager.setLocale(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.getLocales().get(0).getLanguage());
    }
}
