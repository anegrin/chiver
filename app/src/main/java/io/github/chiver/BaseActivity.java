package io.github.chiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

abstract class BaseActivity extends AppCompatActivity {

    private final boolean home;

    BaseActivity() {
        this(true);
    }

    BaseActivity(boolean home) {
        this.home = home;
    }

    Chiver getChiver() {
        return (Chiver) getApplication();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isNotHome());
        getSupportActionBar().setDisplayShowHomeEnabled(isNotHome());
        _onCreate(savedInstanceState);
    }

    protected abstract int getContentView();

    private boolean isNotHome() {
        return !home;
    }

    protected abstract void _onCreate(@SuppressWarnings("unused") Bundle savedInstanceState);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        _onCreateOptionsMenu(menu);
        return true;
    }

    protected abstract void _onCreateOptionsMenu(Menu menu);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mi_refresh:
                return onRefresh(item);
            case R.id.mi_share:
                return onShare(item);
            case R.id.mi_browse:
                return onBrowse(item);
            case R.id.mi_info:
                return onInfo(item);
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("all")
    final boolean onInfo(MenuItem item) {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
        return true;
    }

    boolean onShare(MenuItem item) {
        return true;
    }

    boolean onRefresh(MenuItem item) {
        return true;
    }

    boolean onBrowse(MenuItem item) {
        return true;
    }

}
