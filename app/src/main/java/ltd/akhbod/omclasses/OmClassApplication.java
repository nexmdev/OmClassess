package ltd.akhbod.omclasses;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ibm on 29-06-2018.
 */

public class OmClassApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
