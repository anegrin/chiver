package io.github.chiver;

import android.app.Application;

import java.io.IOException;

import cz.fhucho.android.util.SimpleDiskCache;

@SuppressWarnings("WeakerAccess")
public class Chiver extends Application {

    private SimpleDiskCache simpleDiskCache;

    public Chiver() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            simpleDiskCache = SimpleDiskCache.open(getExternalCacheDir(), 1, 32 * 1024 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public SimpleDiskCache getSimpleDiskCache() {
        return simpleDiskCache;
    }
}
