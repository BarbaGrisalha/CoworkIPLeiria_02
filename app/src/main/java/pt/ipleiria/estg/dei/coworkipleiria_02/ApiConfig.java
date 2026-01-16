package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Context;
import android.content.SharedPreferences;

public class ApiConfig {
    private static final String PREF_NAME = "ApiConfig";
    private static final String KEY_BASE_URL = "base_url";

    public static void setBaseUrl(Context context, String url) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_BASE_URL, url).apply();
    }

    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BASE_URL, "http://10.0.2.2:8080/cowork/api/web/");
    }
}