package br.com.estudio89.versionchecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/**
 * Created by luccascorrea on 12/7/15.
 *
 * This class monitors the application version, calling a listener whenever there is a change,
 * meaning that the app was upgraded or it's a new install.
 * .
 * It should always be instantiated in the onCreate method of the application class before any other logic is run, so that
 * any changes that need to be made to the app when it is updated are run before anything else.
 *
 */
public class VersionChecker {
    public static final String APP_VERSION_PREFERENCES = "br.com.estudio89.versionchecker";
    public static final String APP_VERSION_KEY = "app_version";

    private Context context;
    private VersionChangeListener listener;

    public interface VersionChangeListener {
        void onAppUpgrade(int oldVersionCode, int newVersionCode);
        void onAppInstall(int newVersionCode);
    }

    public VersionChecker(Context context, VersionChangeListener listener) {
        this.context = context;
        this.listener = listener;

        this.runCheck();
    }

    private void runCheck() {
        int current = getCurrentVersion();
        int stored = getStoredVersion();
        updateVersion();

        if (current == stored) {
            return;
        }

        if (current != stored) {
            if (stored == -1) {
                listener.onAppInstall(current);
            } else {
                listener.onAppUpgrade(stored, current);
            }
        }
    }

    public int getStoredVersion() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getInt(APP_VERSION_KEY, -1);
    }

    public void updateVersion() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(APP_VERSION_KEY, getCurrentVersion());
        editor.commit();
    }

    public int getCurrentVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

