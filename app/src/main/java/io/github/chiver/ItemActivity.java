package io.github.chiver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import io.github.chiver.util.CachingImageLoader;

public class ItemActivity extends BaseActivity {

    public static final String URLS_KEY = "urls";
    public static final String POS_KEY = "position";

    private ImageLoader imageLoader;
    private ViewPager vpItem;
    private String[] urls;
    private View ivPrev;
    private View ivNext;

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

        View llPrev = findViewById(R.id.ll_prev);
        llPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = vpItem.getCurrentItem();
                boolean hasPrev = currentItem > 0;
                if (hasPrev) {
                    vpItem.setCurrentItem(currentItem - 1);
                }
            }
        });

        View llNext = findViewById(R.id.ll_next);
        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = vpItem.getCurrentItem();
                boolean hasNext = currentItem < urls.length - 1;
                if (hasNext) {
                    vpItem.setCurrentItem(currentItem + 1);
                }
            }
        });

        ivPrev = findViewById(R.id.iv_prev);
        ivNext = findViewById(R.id.iv_next);

        if (pos == 0) {
            ivPrev.setVisibility(View.INVISIBLE);
        } else if (pos == urls.length - 1) {
            ivNext.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fadeOut(ivPrev);
        fadeOut(ivNext);
    }

    private void fadeOut(View view) {

        if (view.getVisibility() == View.VISIBLE) {

            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            animation.setDuration(1000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.startAnimation(animation);
        }
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
