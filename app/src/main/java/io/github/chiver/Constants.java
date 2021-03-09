package io.github.chiver;

public class Constants {
    private static final String TC = "thechive";
    static final String TC_BASE_URL = "https://" + TC + ".com/";
    static final String TC_MAIN_FEED = TC_BASE_URL + "feed/";
    static final String TC_FEED_PATTERN = TC_BASE_URL + "feed/?paged=%s";
    static final String REMOTE_SCRIPT_URL = "https://raw.githubusercontent.com/anegrin/chiver/next/app/src/main/assets/script.js";
    public static final String TAG = Chiver.class.getSimpleName();

    private Constants() {
    }
}
