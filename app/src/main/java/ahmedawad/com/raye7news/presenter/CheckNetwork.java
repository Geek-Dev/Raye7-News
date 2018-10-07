package ahmedawad.com.raye7news.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {
    private static CheckNetwork checkNetwork;

    // make instance from class
    public static synchronized CheckNetwork getInstance(){
        if(checkNetwork==null){
            checkNetwork=new CheckNetwork();
        }
        return checkNetwork;
    }

    // check internet connection
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo == null || !netInfo.isConnected();

    }
}
