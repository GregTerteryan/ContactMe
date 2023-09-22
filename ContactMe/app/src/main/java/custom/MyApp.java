package custom;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static Context appContext;

    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
