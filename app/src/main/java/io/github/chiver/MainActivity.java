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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.github.chiver.adapter.GalleryAdapter;
import io.github.chiver.util.SitemapSAXParser;

public class MainActivity extends BaseActivity {

    private GalleryAdapter adapter;
    private RequestQueue requestQueue;
    private int daysCounter = 0;
    private ProgressDialog progressDialog;


    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

        RecyclerView recyclerView = findViewById(R.id.rv_galleries);
        recyclerView.setHasFixedSize(true);
        adapter = new GalleryAdapter(this, getChiver().getSimpleDiskCache());
        recyclerView.setAdapter(adapter);
        loadGalleries();

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
                        loadGalleries();
                    }
                }

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

    private void loadGalleries() {

        progressDialog.show();

        Calendar utc = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.add(Calendar.DAY_OF_YEAR, daysCounter);

        int year = utc.get(Calendar.YEAR);
        int month = utc.get(Calendar.MONTH) + 1;//0 based
        int day = utc.get(Calendar.DAY_OF_MONTH);

        StringRequest stringRequest = new StringRequest(String.format(Locale.getDefault(), Constants.TC_SITEMAP_PATTERN, year, month, day, String.valueOf(Math.random())), response -> {
            parseAndNotifyProgress(response, progressDialog);
        }, error -> {

            if (error.networkResponse != null && error.networkResponse.statusCode == 404) {

                //sometimes we get 404 but even if we get proper response
                if (isValidResponse(error.networkResponse.data)) {
                    String response = new String(error.networkResponse.data);
                    parseAndNotifyProgress(response, progressDialog);
                } else {
                    daysCounter--;
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, R.string.backInTime, Toast.LENGTH_SHORT).show();
                    loadGalleries();
                }
                return;
            }

            Log.e("Volley", error.toString());
            progressDialog.dismiss();
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

    private void parseAndNotifyProgress(String response, ProgressDialog progressDialog) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SitemapSAXParser handler = new SitemapSAXParser(adapter::addItem);
            saxParser.parse(new InputSource(new StringReader(response)), handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.e("Volley", e.getMessage(), e);
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, R.string.loadingError, Toast.LENGTH_SHORT).show();
        }

        daysCounter--;

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    private synchronized RequestQueue getRequestQueue() {
        if (this.requestQueue == null) {
            this.requestQueue = Volley.newRequestQueue(this);
        }

        return this.requestQueue;

    }

    @Override
    protected boolean onRefresh(MenuItem item) {
        adapter.clearItems();
        resetDaysCounter();
        adapter.notifyDataSetChanged();
        loadGalleries();
        return super.onRefresh(item);
    }

    private void resetDaysCounter() {
        daysCounter = 0;
    }
}
