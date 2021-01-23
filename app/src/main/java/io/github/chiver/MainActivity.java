package io.github.chiver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.github.chiver.adapter.GalleryAdapter;
import io.github.chiver.util.FeedSAXParser;

public class MainActivity extends BaseActivity {

    private GalleryAdapter adapter;
    private RequestQueue requestQueue;
    private int page = 1;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

        RecyclerView recyclerView = findViewById(R.id.rv_galleries);
        recyclerView.setHasFixedSize(true);
        adapter = new GalleryAdapter(this, getChiver().getSimpleDiskCache());
        recyclerView.setAdapter(adapter);
        loadGalleries(false);

        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (progressDialog.isShowing()) {
                    return;
                }
                if (layoutManager != null) {
                    boolean canScroll = layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1;
                    if (!canScroll) {
                        loadGalleries(false);
                    }
                }

            }
        });

        swipeRefreshLayout = findViewById(R.id.srl_gallery_items);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    @Override
    protected void _onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.mi_refresh).setVisible(true);
        menu.findItem(R.id.mi_share).setVisible(false);
        menu.findItem(R.id.mi_browse).setVisible(false);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    private void loadGalleries(boolean refresh) {

        onStartLoading(refresh);

        String url = page == 1 ? Constants.TC_MAIN_FEED : String.format(Locale.getDefault(), Constants.TC_FEED_PATTERN, page);
        StringRequest stringRequest = new StringRequest(url, response -> {
            parseAndNotifyProgress(response, refresh);
        }, error -> {

            if (error.networkResponse != null && error.networkResponse.statusCode == 404) {

                //sometimes we get 404 but even if we get proper response
                if (isValidResponse(error.networkResponse.data)) {
                    String response = new String(error.networkResponse.data);
                    parseAndNotifyProgress(response, refresh);
                } else {
                    page++;
                    onEndLoading(refresh);
                    Toast.makeText(MainActivity.this, R.string.tryNextPage, Toast.LENGTH_SHORT).show();
                    loadGalleries(false);
                }
                return;
            }

            Log.e("Volley", error.toString());
            onEndLoading(refresh);
            Toast.makeText(MainActivity.this, R.string.loadingError, Toast.LENGTH_SHORT).show();
        });
        getRequestQueue().add(stringRequest);
    }

    private boolean isValidResponse(byte[] data) {
        //does it start with '<?xml' ?
        return data != null
                && data.length > 5
                && data[0] == '<'
                && data[1] == '?'
                && data[2] == 'x'
                && data[3] == 'm'
                && data[4] == 'l';
    }

    private void parseAndNotifyProgress(String response, boolean refresh) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            FeedSAXParser handler = new FeedSAXParser(adapter::addItem);
            saxParser.parse(new InputSource(new StringReader(response)), handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.e("Volley", e.getMessage(), e);
            onEndLoading(refresh);
            Toast.makeText(MainActivity.this, R.string.loadingError, Toast.LENGTH_SHORT).show();
        }

        page++;

        adapter.notifyDataSetChanged();
        onEndLoading(refresh);
    }

    private void onStartLoading(boolean refresh) {
        if (refresh) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            progressDialog.show();
        }
    }

    private void onEndLoading(boolean refresh) {
        if (refresh) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private synchronized RequestQueue getRequestQueue() {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        return this.requestQueue;

    }

    @Override
    protected boolean onRefresh(MenuItem item) {
        doRefresh();
        return super.onRefresh(item);
    }

    private void doRefresh() {
        adapter.clearItems();
        resetDaysCounter();
        loadGalleries(true);
    }

    private void resetDaysCounter() {
        page = 1;
    }
}
