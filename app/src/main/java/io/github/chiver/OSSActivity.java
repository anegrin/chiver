package io.github.chiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.github.chiver.adapter.LicenseAdapter;

public class OSSActivity extends BaseActivity {

    private final Map<String, List<String>> linksToItems = new HashMap<>();

    public OSSActivity() {
        super(false);
    }

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(R.string.oss_licenses);
        ListView lvLicenses = findViewById(R.id.lv_licenses);

        final List<String> links = new LinkedList<>();
        final List<String> items = new LinkedList<>();

        try (InputStream is = getResources().openRawResource(R.raw.third_party_license_metadata);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)) {
            reader.lines().filter(this::isNotBlank).map(s -> s.split(" ")[1]).forEach(items::add);
        } catch (IOException ioe) {
            onLoadingError();
        }

        try (InputStream is = getResources().openRawResource(R.raw.third_party_licenses);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)) {
            reader.lines().filter(this::isNotBlank).forEach(links::add);
        } catch (IOException ioe) {
            onLoadingError();
        }

        if (items.size() != links.size()) {
            onLoadingError();
        }

        for (Iterator<String> itemsIterator = items.iterator(), linksIterator = links.iterator(); itemsIterator.hasNext() && linksIterator.hasNext(); ) {
            String item = itemsIterator.next();
            String link = linksIterator.next();

            List<String> itemsList = linksToItems.computeIfAbsent(link, k -> new LinkedList<>());
            itemsList.add(item);
        }

        LicenseAdapter adapter = new LicenseAdapter(this, linksToItems);

        lvLicenses.setAdapter(adapter);

        lvLicenses.setOnItemClickListener(this::onItemClick);
    }

    private void onLoadingError() {
        Toast.makeText(this, R.string.loadingError, Toast.LENGTH_SHORT).show();
        linksToItems.clear();
    }

    @SuppressWarnings("unused")
    private void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String link = adapterView.getAdapter().getItem(position).toString();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }


    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Override
    protected void _onCreateOptionsMenu(Menu menu) {
        menu.findItem(R.id.mi_refresh).setVisible(false);
        menu.findItem(R.id.mi_share).setVisible(false);
        menu.findItem(R.id.mi_browse).setVisible(false);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_oss;
    }
}
