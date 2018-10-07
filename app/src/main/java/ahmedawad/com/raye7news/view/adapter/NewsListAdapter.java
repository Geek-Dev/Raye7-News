package ahmedawad.com.raye7news.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.List;
import ahmedawad.com.raye7news.R;
import ahmedawad.com.raye7news.model.FavoriteObject;
import ahmedawad.com.raye7news.model.NewsObject;
import ahmedawad.com.raye7news.presenter.CheckNetwork;
import ahmedawad.com.raye7news.view.activity.WebActivity;
import io.realm.Realm;
import io.realm.RealmResults;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListHolder>{
    private Context context;
    private List<NewsObject.ArticlesBean> newsObjectList;
    private int width;
    private Realm realm=Realm.getDefaultInstance();

    private SimpleDateFormat timeDateFormat=new SimpleDateFormat("dd-MM-yyyy  hh:mm a", Locale.US);
    private SimpleDateFormat groupDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    private org.joda.time.LocalDate dateCheck;
    private Dialog dialog;

    // constructor
    public NewsListAdapter(Context context, List<NewsObject.ArticlesBean> newsObjectList,int width) {
        this.context = context;
        this.newsObjectList = newsObjectList;
        this.width=width;

        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
    }

    @NonNull
    @Override
    public NewsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_newslist,parent,false);
        return new NewsListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsListHolder holder, int position) {
        final int rowPosition=position;

        // set news image
        holder.imgNews.getLayoutParams().height= (int) (width*0.5);
        Glide.with(context).load(newsObjectList.get(position).getUrlToImage()).apply(new RequestOptions().placeholder(R.drawable.loading).fallback(R.drawable.noimage).error(R.drawable.noimage)).into(holder.imgNews);

        // set title ,source and time
        holder.tvNewsSource.setText(newsObjectList.get(position).getSource().getName());
        holder.tvNewsTitle.setText(newsObjectList.get(position).getTitle());
        holder.tvNewsTime.setText(timeDateFormat.format(newsObjectList.get(position).getPublishedAt()));

        // get position date
        org.joda.time.LocalDate dateCurrent=new org.joda.time.LocalDate(newsObjectList.get(position).getPublishedAt());

        // group news by date
        if(position-1 > -1){
            dateCheck=new org.joda.time.LocalDate(newsObjectList.get(position-1).getPublishedAt());
        }

        if(position==0){
                holder.cvDateGroup.setVisibility(View.VISIBLE);
                holder.tvDateGroup.setText(groupDateFormat.format(dateCurrent.toDate()));
                holder.imgDateGroup.setVisibility(View.VISIBLE);
        }else {
            if(!dateCurrent.isEqual(dateCheck)){
                holder.cvDateGroup.setVisibility(View.VISIBLE);
                holder.tvDateGroup.setText(groupDateFormat.format(dateCurrent.toDate()));
                holder.imgDateGroup.setVisibility(View.VISIBLE);
            }
            else {
                holder.cvDateGroup.setVisibility(View.GONE);
                holder.imgDateGroup.setVisibility(View.GONE);
            }
        }

        // check if item in favorite or not
        if(realm.where(FavoriteObject.class).equalTo("title",newsObjectList.get(rowPosition).getTitle()).findFirst()!=null){
            holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_on));
            holder.imgFavorite.setTag("1");
        }else {
            holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_off));
            holder.imgFavorite.setTag("0");
        }

        // add news to favorite or delete from it
        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if news not in favorite
                if(holder.imgFavorite.getTag().toString().equals("0")){
                    // add news to favorite
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            FavoriteObject favoriteObject=realm.createObject(FavoriteObject.class);

                            favoriteObject.setTitle(newsObjectList.get(rowPosition).getTitle());
                            favoriteObject.setUrl(newsObjectList.get(rowPosition).getUrl());
                            favoriteObject.setImageUrl(newsObjectList.get(rowPosition).getUrlToImage());
                            favoriteObject.setNewsDate(newsObjectList.get(rowPosition).getPublishedAt());
                            favoriteObject.setNewsSource(newsObjectList.get(rowPosition).getSource().getName());
                        }
                    });
                    // set favorite icon
                    holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_on));
                    holder.imgFavorite.setTag("1");
                    Toast.makeText(context, "Added to favorite", Toast.LENGTH_SHORT).show();
                }
                else {
                    // delete news from favorite
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            RealmResults<FavoriteObject> realmResults=realm.where(FavoriteObject.class).equalTo("title",newsObjectList.get(rowPosition).getTitle()).findAll();
                            realmResults.deleteFirstFromRealm();
                        }
                    });
                    // set not favorite icon
                    holder.imgFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_off));
                    holder.imgFavorite.setTag("0");
                    Toast.makeText(context, "Deleted from favorite", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // open url in webview
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                // check network connection
                if (CheckNetwork.getInstance().isNetworkConnected(context)) {
                    // init dialog widget
                    TextView tvDialog = dialog.findViewById(R.id.tvDialog);
                    Button btnYes = dialog.findViewById(R.id.btnYes);
                    Button btnNo = dialog.findViewById(R.id.btnNo);

                    tvDialog.setText("No internet connection,Make sure Wi-Fi or cellular data is turned on, then try again.");
                    btnYes.setText("Try again");
                    btnNo.setText("Exit");

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            ((Activity)context).recreate();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            ((Activity)context).finish();
                        }
                    });
                    dialog.show();
                }else {
                    // go to web view
                    Intent intent=new Intent(context, WebActivity.class);
                    intent.putExtra("url",newsObjectList.get(rowPosition).getUrl());
                    intent.putExtra("activity","0");
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return newsObjectList.size();
    }

    static class NewsListHolder extends RecyclerView.ViewHolder{
        CardView cvDateGroup;
        TextView tvDateGroup;

        ImageView imgNews;
        TextView tvNewsSource;
        TextView tvNewsTitle;
        TextView tvNewsTime;
        ImageView imgFavorite;
        ImageView imgDateGroup;

        NewsListHolder(View itemView) {
            super(itemView);

            cvDateGroup=itemView.findViewById(R.id.cvDateGroup);
            tvDateGroup=itemView.findViewById(R.id.tvDateGroup);

            imgNews=itemView.findViewById(R.id.imgNews);
            tvNewsSource=itemView.findViewById(R.id.tvNewsSource);
            tvNewsTitle=itemView.findViewById(R.id.tvNewsTitle);
            tvNewsTime=itemView.findViewById(R.id.tvNewsTime);
            imgFavorite=itemView.findViewById(R.id.imgFavorite);
            imgDateGroup=itemView.findViewById(R.id.imgDateGroup);
        }
    }

}
