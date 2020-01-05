package io.github.chiver;

public class Constants {
    private static final String TC = "thechive";
    private static final String TC_BASE_URL = "https://" + TC + ".com/";
    static final String TC_SITEMAP_PATTERN = TC_BASE_URL + "sitemap.xml?yyyy=%d&mm=%d&dd=%d&_rnd=%s";
    public static final String TAG = Chiver.class.getSimpleName();

    private Constants() {
    }
}
