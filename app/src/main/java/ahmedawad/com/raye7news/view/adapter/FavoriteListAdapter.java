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

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ahmedawad.com.raye7news.R;
import ahmedawad.com.raye7news.model.FavoriteObject;
import ahmedawad.com.raye7news.presenter.CheckNetwork;
import ahmedawad.com.raye7news.view.activity.WebActivity;
import io.realm.Realm;
import io.realm.RealmResults;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.FavoriteListHolder>{
    private Context context;
    private List<FavoriteObject> favoriteObjectList;
    private int width;
    private Realm realm=Realm.getDefaultInstance();

    private SimpleDateFormat timeDateFormat=new SimpleDateFormat("dd-MM-yyyy  hh:mm a", Locale.US);
    private SimpleDateFormat groupDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private Dialog dialog;

    private org.joda.time.LocalDate dateCheck;

    // constructor
    public FavoriteListAdapter(Context context, List<FavoriteObject> favoriteObjectList,int width) {
        this.context = context;
        this.favoriteObjectList = favoriteObjectList;
        this.width=width;

        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
    }

    @NonNull
    @Override
    public FavoriteListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_newslist,parent,false);
        return new FavoriteListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteListHolder holder,int position) {
        final int rowPosition=position;

        // set news image
        holder.imgNews.getLayoutParams().height= (int) (width*0.5);
        Glide.with(context).load(favoriteObjectList.get(position).getImageUrl()).into(holder.imgNews);

        // set title ,source and time
        holder.tvNewsSource.setText(favoriteObjectList.get(position).getNewsSource());
        holder.tvNewsTitle.setText(favoriteObjectList.get(position).getTitle());
        holder.tvNewsTime.setText(timeDateFormat.format(favoriteObjectList.get(position).getNewsDate()));

        // get position date
        org.joda.time.LocalDate dateCurrent=new org.joda.time.LocalDate(favoriteObjectList.get(position).getNewsDate());

        // group news by date
        if(position+1 < favoriteObjectList.size()){
            dateCheck=new org.joda.time.LocalDate(favoriteObjectList.get(position+1).getNewsDate());
        }

        if(position==favoriteObjectList.size()-1){
                holder.cvDateGroup.setVisibility(View.VISIBLE);
                holder.tvDateGroup.setText(groupDateFormat.format(dateCurrent.toDate()));
        }else {
            if(!dateCurrent.isEqual(dateCheck)){
                holder.cvDateGroup.setVisibility(View.VISIBLE);
                holder.tvDateGroup.setText(groupDateFormat.format(dateCurrent.toDate()));
            }
            else {
                holder.cvDateGroup.setVisibility(View.GONE);
            }
        }

        // delete favorite news
        holder.imgFavorite.setImageResource(R.drawable.ic_delete);
        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.dialog_alert);

                Button btnYes=dialog.findViewById(R.id.btnYes);
                Button btnNo=dialog.findViewById(R.id.btnNo);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                RealmResults<FavoriteObject> realmResults=realm.where(FavoriteObject.class).equalTo("title",favoriteObjectList.get(rowPosition).getTitle()).findAll();
                                realmResults.deleteFirstFromRealm();
                            }
                        });
                        notifyDataSetChanged();
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
                    intent.putExtra("url",favoriteObjectList.get(rowPosition).getUrl());
                    intent.putExtra("activity","1");
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return favoriteObjectList.size();
    }

    // view holder class
    static class FavoriteListHolder extends RecyclerView.ViewHolder{
        CardView cvDateGroup;
        TextView tvDateGroup;

        ImageView imgNews;
        TextView tvNewsSource;
        TextView tvNewsTitle;
        TextView tvNewsTime;
        ImageView imgFavorite;

        FavoriteListHolder(View itemView) {
            super(itemView);

            cvDateGroup=itemView.findViewById(R.id.cvDateGroup);
            tvDateGroup=itemView.findViewById(R.id.tvDateGroup);

            imgNews=itemView.findViewById(R.id.imgNews);
            tvNewsSource=itemView.findViewById(R.id.tvNewsSource);
            tvNewsTitle=itemView.findViewById(R.id.tvNewsTitle);
            tvNewsTime=itemView.findViewById(R.id.tvNewsTime);
            imgFavorite=itemView.findViewById(R.id.imgFavorite);
        }
    }

}
