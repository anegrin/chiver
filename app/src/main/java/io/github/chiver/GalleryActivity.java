package io.github.chiver;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import io.github.chiver.adapter.GalleryItemAdapter;
import io.github.chiver.model.Gallery;
import io.github.chiver.model.GalleryItem;

public class GalleryActivity extends BaseActivity {

    public static final String GALLERY_KEY = "gallery";

    private Gallery gallery;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;
    private WebView wvParser;

    public GalleryActivity() {
        super(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.parsing));

        gallery = getIntent().getParcelableExtra(GALLERY_KEY);
        RecyclerView recyclerView = findViewById(R.id.rv_gallery_items);
        recyclerView.setHasFixedSize(true);

        GalleryItemAdapter adapter = new GalleryItemAdapter(this, getChiver().getSimpleDiskCache());
        recyclerView.setAdapter(adapter);

        wvParser = findViewById(R.id.wv_parser);
        wvParser.getSettings().setJavaScriptEnabled(true);
        wvParser.getSettings().setLoadsImagesAutomatically(false);
        wvParser.getSettings().setBlockNetworkImage(true);
        wvParser.addJavascriptInterface(new SearchWebAppInterface(this, adapter), "wai");

        fetch(wvParser, gallery.link);

    }

    @Override
    protected void _onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.mi_refresh).setVisible(false);
        menu.findItem(R.id.mi_share).setVisible(false);
        menu.findItem(R.id.mi_browse).setVisible(true);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gallery;
    }

    @Override
    protected boolean onBrowse(MenuItem item) {
        if (gallery != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gallery.link)));
        }

        return super.onShare(item);
    }

    private void fetch(WebView wvParser, String url) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(url, response -> {
            String parser = loadResource("parser.html");
            String html = parser.replace("$placeholder$", response);
            wvParser.loadDataWithBaseURL(Constants.TC_BASE_URL, html, "text/html", "UTF-8", null);
        }, error -> {
            Toast.makeText(GalleryActivity.this, R.string.loadingError, Toast.LENGTH_SHORT).show();
        });
        getRequestQueue().add(stringRequest);
    }

    private String loadResource(String res) throws RuntimeException {
        try {
            return IOUtils.toString(getResources().getAssets().open(res), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized RequestQueue getRequestQueue() {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        return this.requestQueue;

    }

    private static class SearchWebAppInterface {
        private GalleryActivity galleryActivity;
        private GalleryItemAdapter galleryItemAdapter;

        public SearchWebAppInterface(GalleryActivity galleryActivity, GalleryItemAdapter galleryItemAdapter) {
            this.galleryActivity = galleryActivity;
            this.galleryItemAdapter = galleryItemAdapter;
        }

        @JavascriptInterface
        public void addGalleryItem(String url) {
            if (url != null) {
                galleryItemAdapter.addGalleryItem(new GalleryItem(url));
            }

        }

        @JavascriptInterface
        public void notifyDataSetChanged() {
            galleryActivity.runOnUiThread(() -> {
                ((ViewGroup) galleryActivity.wvParser.getParent()).removeView(galleryActivity.wvParser);
                galleryItemAdapter.notifyDataSetChanged();
                galleryActivity.progressDialog.dismiss();
            });
        }
    }
}
