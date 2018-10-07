package ahmedawad.com.raye7news.presenter;

import android.app.Application;

import io.realm.Realm;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // init realm
        Realm.init(this);
    }
}
