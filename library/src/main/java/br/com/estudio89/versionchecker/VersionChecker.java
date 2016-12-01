package br.com.estudio89.versionchecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

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
    private static final String PENDING_UPDATE_KEY = "pending_update";
    public static final String PENDING_INFO_OLD_VERSION = "pending_old_version";
    public static final String PENDING_INFO_NEW_VERSION = "pending_new_version";

    public static VersionChecker instance;

    private Context context;
    private VersionChangeListener listener;

    public interface VersionChangeListener {
        void onAppUpgrade(int oldVersionCode, int newVersionCode);
        void onAppInstall(int newVersionCode);
    }

    public VersionChecker(Context context, VersionChangeListener listener) {
        this.context = context;
        this.listener = listener;
        instance = this;

        this.runCheck();
    }

    public static VersionChecker getInstance() {
        if (instance == null) {
            throw new IllegalAccessError("Version checker was not initialized!");
        }
        return instance;
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

    public Bundle getPendingUpdateInfo() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
        String info = sharedPref.getString(PENDING_UPDATE_KEY, null);
        if (info == null) {
            return null;
        }
        String oldVersion = info.split(";")[0];
        String newVersion = info.split(";")[0];

        Bundle bundle = new Bundle();
        bundle.putInt(PENDING_INFO_OLD_VERSION, Integer.parseInt(oldVersion));
        bundle.putInt(PENDING_INFO_NEW_VERSION, Integer.parseInt(newVersion));
        return bundle;
    }
    public boolean hasPendingUpdates() {
        return getPendingUpdateInfo() != null;
    }

    public void addPendingUpdate(int oldVersion, int newVersion) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PENDING_UPDATE_KEY, String.valueOf(oldVersion) + ";" + String.valueOf(newVersion));
        editor.commit();
    }

    public void clearPendingUpdates() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(PENDING_UPDATE_KEY);
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

