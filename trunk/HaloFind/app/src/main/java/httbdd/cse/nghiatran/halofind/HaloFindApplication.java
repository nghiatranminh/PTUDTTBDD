package httbdd.cse.nghiatran.halofind;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;

import io.fabric.sdk.android.Fabric;

public class HaloFindApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Firebase.setAndroidContext(this);
        sContext = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
