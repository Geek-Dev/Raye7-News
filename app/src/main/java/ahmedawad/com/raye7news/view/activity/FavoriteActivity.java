package ahmedawad.com.raye7news.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import ahmedawad.com.raye7news.R;
import ahmedawad.com.raye7news.model.FavoriteObject;
import ahmedawad.com.raye7news.view.adapter.FavoriteListAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView rvFavoriteList;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // add action bar icon
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Raye7 Favorite");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.rayelogo75);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        // get screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;

        // init dialog alert
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);

        // init recycler view
        rvFavoriteList=findViewById(R.id.rvFavoriteList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rvFavoriteList.setLayoutManager(linearLayoutManager);
        rvFavoriteList.setHasFixedSize(true);

        // init realm
        Realm realm=Realm.getDefaultInstance();
        // get favorite news from realm
        RealmResults<FavoriteObject> realmResults=realm.where(FavoriteObject.class).findAll().sort("newsDate", Sort.ASCENDING);

        // check if realm data less than 0
        if(realmResults.size()<=0){
            showDialog();
        }

        // init adapter
        FavoriteListAdapter favoriteListAdapter=new FavoriteListAdapter(this,realmResults,width);

        rvFavoriteList.setAdapter(favoriteListAdapter);


    }

    // show alert dialog
    @SuppressLint("SetTextI18n")
    public void showDialog(){
        TextView tvDialog=dialog.findViewById(R.id.tvDialog);
        Button btnYes=dialog.findViewById(R.id.btnYes);
        Button btnNo=dialog.findViewById(R.id.btnNo);

        tvDialog.setText("No favorite news yet, return to the news?");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FavoriteActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
            Intent intent1 = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(FavoriteActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
