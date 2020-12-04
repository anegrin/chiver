package io.github.chiver;

public class Constants {
    private static final String TC = "thechive";
    private static final String TC_BASE_URL = "https://" + TC + ".com/";
    static final String TC_MAIN_FEED = TC_BASE_URL + "feed/";
    static final String TC_FEED_PATTERN = TC_BASE_URL + "feed/?paged=%s";
    public static final String TAG = Chiver.class.getSimpleName();

    private Constants() {
    }
}
