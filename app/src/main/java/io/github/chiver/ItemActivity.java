package io.github.chiver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.github.chiver.util.CachingImageLoader;

public class ItemActivity extends BaseActivity {

    public static final String URLS_KEY = "urls";
    public static final String POS_KEY = "position";

    private ImageLoader imageLoader;
    private ViewPager vpItem;
    private String[] urls;

    public ItemActivity() {
        super(false);
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        urls = getIntent().getStringArrayExtra(URLS_KEY);
        int pos = getIntent().getIntExtra(POS_KEY, 0);

        vpItem = findViewById(R.id.vp_item);
        vpItem.setAdapter(new ItemAdapter(getSupportFragmentManager(), urls, getImageLoader()));
        vpItem.setCurrentItem(pos);
        vpItem.addOnPageChangeListener(new DisplayPage(findViewById(R.id.tv_pages), urls.length));

    }

    @Override
    protected void _onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.mi_refresh).setVisible(false);
        menu.findItem(R.id.mi_share).setVisible(true);
        menu.findItem(R.id.mi_browse).setVisible(false);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_item;
    }

    private synchronized ImageLoader getImageLoader() {

        if (this.imageLoader == null) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            this.imageLoader = new ImageLoader(requestQueue, new CachingImageLoader(getChiver().getSimpleDiskCache()));
        }

        return this.imageLoader;
    }

    @Override
    protected boolean onShare(MenuItem item) {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(urls[vpItem.getCurrentItem()])
                .startChooser();

        return super.onShare(item);
    }

    private class ItemAdapter extends FragmentStatePagerAdapter {

        private final String[] urls;
        private final ImageLoader imageLoader;

        ItemAdapter(FragmentManager supportFragmentManager, String[] urls, ImageLoader imageLoader) {
            super(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.urls = urls;
            this.imageLoader = imageLoader;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return new ItemFragment(urls[position], imageLoader);
        }

        @Override
        public int getCount() {
            return urls.length;
        }
    }

    private class DisplayPage implements ViewPager.OnPageChangeListener {
        private final int total;
        private final TextView textView;

        DisplayPage(TextView textView, int total) {
            this.textView = textView;
            this.total = total;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            positionText(position);
        }

        @Override
        public void onPageSelected(int position) {
            positionText(position);
        }

        @SuppressLint("SetTextI18n")
        private void positionText(int position) {
            textView.setText((position + 1) + " / " + total);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
