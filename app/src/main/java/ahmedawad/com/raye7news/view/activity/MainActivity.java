package ahmedawad.com.raye7news.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.Comparator;
import ahmedawad.com.raye7news.R;
import ahmedawad.com.raye7news.model.NewsObject;
import ahmedawad.com.raye7news.presenter.ApiClient;
import ahmedawad.com.raye7news.presenter.ApiInterface;
import ahmedawad.com.raye7news.presenter.CheckNetwork;
import ahmedawad.com.raye7news.view.adapter.NewsListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    RecyclerView rvNewsList;
    ProgressDialog progressDialog;
    Dialog dialog;

    NewsListAdapter newsListAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add action bar icon
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.rayelogo75);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        // init recycler view
        rvNewsList = findViewById(R.id.rvNewsList);
        rvNewsList.setLayoutManager(new LinearLayoutManager(this));
        rvNewsList.setHasFixedSize(true);

        // get screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;

        // init dialog alert
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);

        // init progress dialog
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Fetching latest news data...");
        progressDialog.setCancelable(false);

        // check network connection
        if (CheckNetwork.getInstance().isNetworkConnected(getApplicationContext())) {
            // init dialog widget
            TextView tvDialog=dialog.findViewById(R.id.tvDialog);
            Button btnYes=dialog.findViewById(R.id.btnYes);
            Button btnNo=dialog.findViewById(R.id.btnNo);

            tvDialog.setText("No internet connection,Make sure Wi-Fi or cellular data is turned on, then try again.");
            btnYes.setText("Try again");
            btnNo.setText("Exit");

            // yes btn
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    recreate();
                }
            });

            // no btn
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        }else {
            // show progress dialog
            progressDialog.show();

            // init retrofit call
            ApiInterface apiInterface= ApiClient.getInstance().create(ApiInterface.class);

            Call<NewsObject> newsObjectCall=apiInterface.getAllNewsData("google","usa-today","en","42105f4ec06f48bfb603590c79481066");
            newsObjectCall.enqueue(new Callback<NewsObject>() {
                @Override
                public void onResponse(@NonNull Call<NewsObject> call, @NonNull Response<NewsObject> response) {
                    if(response.body()!=null){
                        NewsObject responseNews=response.body();

                        // sort news list by date
                        if (responseNews != null) {
                            Collections.sort(responseNews.getArticles(), new Comparator<NewsObject.ArticlesBean>() {
                                @Override
                                public int compare(NewsObject.ArticlesBean articlesBean, NewsObject.ArticlesBean t1) {
                                    if(articlesBean.getPublishedAt()==null || t1.getPublishedAt()==null){
                                        return 0;
                                    }else {
                                        return articlesBean.getPublishedAt().compareTo(t1.getPublishedAt());
                                    }
                                }
                            });

                            Collections.reverse(responseNews.getArticles());

                            // init adapter
                            newsListAdapter=new NewsListAdapter(MainActivity.this,responseNews.getArticles(),width);
                            rvNewsList.setAdapter(newsListAdapter);

                            progressDialog.dismiss();
                        }

                    }

                }

                @Override
                public void onFailure(@NonNull Call<NewsObject> call, @NonNull Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    recreate();
                }
            });
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
        if(item.getItemId()==R.id.favorite) {
            Intent intent1 = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent1);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}