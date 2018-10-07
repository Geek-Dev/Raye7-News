package ahmedawad.com.raye7news.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ahmedawad.com.raye7news.R;

public class WebActivity extends AppCompatActivity {

    String activityOrder=null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // add action bar icon
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Raye7 Browser");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.rayelogo75);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        // init webview settings
        WebView webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);

        // get url from another activity
        Intent intent = getIntent();
        if(intent.getStringExtra("url")!=null){
            webView.loadUrl(intent.getStringExtra("url"));
        }

        // check send activity
        if(intent.getStringExtra("activity")!=null){
            activityOrder=intent.getStringExtra("activity");
        }

    }

    // on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(activityOrder.equals("0")){
            Intent intent1 = new Intent(WebActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }else if(activityOrder.equals("1")){
            Intent intent1 = new Intent(WebActivity.this, FavoriteActivity.class);
            startActivity(intent1);
            finish();
        }else {
            Intent intent1 = new Intent(WebActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }
    }

    // create menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // select menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.newsHome) {
            Intent intent1 = new Intent(WebActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }else if(item.getItemId()==R.id.favorite) {
            Intent intent1 = new Intent(WebActivity.this, FavoriteActivity.class);
            startActivity(intent1);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
