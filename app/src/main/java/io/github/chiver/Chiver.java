package io.github.chiver;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import cz.fhucho.android.util.SimpleDiskCache;

@SuppressWarnings("WeakerAccess")
public class Chiver extends Application {

    private static final String PREF_AUTOPLAY = "autoplay";

    private SimpleDiskCache simpleDiskCache;
    private RequestQueue requestQueue;
    private String script;
    private String parser;
    private SharedPreferences preferences;

    public Chiver() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            simpleDiskCache = SimpleDiskCache.open(getExternalCacheDir(), 1, 64 * 1024 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringRequest stringRequest = new StringRequest(Constants.REMOTE_SCRIPT_URL, response -> script = response, error -> Log.i(Constants.TAG, "Can't load remote script, using local asset"));
        getRequestQueue().add(stringRequest);
        preferences = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
    }

    public SimpleDiskCache getSimpleDiskCache() {
        return simpleDiskCache;
    }

    public String getScript() {
        return script = script != null ? script : loadResource("script.js");
    }

    public String getParser() {
        return parser = parser != null ? parser : loadResource("parser.html");
    }

    private String loadResource(String res) throws RuntimeException {
        try {
            return IOUtils.toString(getResources().getAssets().open(res), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized RequestQueue getRequestQueue() {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        return this.requestQueue;

    }

    public void setAutoplayEnabled(boolean enabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_AUTOPLAY, enabled);
        editor.apply();
    }

    public boolean isAutoplayEnabled() {
        return preferences.getBoolean(PREF_AUTOPLAY, false);
    }
}
