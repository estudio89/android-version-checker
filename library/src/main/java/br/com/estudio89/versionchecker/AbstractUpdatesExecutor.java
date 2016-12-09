package br.com.estudio89.versionchecker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by luccascorrea on 12/1/16.
 */
public abstract class AbstractUpdatesExecutor {
    private final int oldVersion;
    Context context;
    private Bundle updateInfo;
    private final int newVersion;
    private final UpdateListener listener;

    public interface UpdateListener {
        void onUpdateFinished();
        void onUpdateFailed();
    }

    public AbstractUpdatesExecutor(Context context, Bundle updateInfo, UpdateListener listener) {
        this.context = context;
        this.updateInfo = updateInfo;
        this.listener = listener;

        oldVersion = updateInfo.getInt(VersionChecker.PENDING_INFO_OLD_VERSION);
        newVersion = updateInfo.getInt(VersionChecker.PENDING_INFO_NEW_VERSION);
    }

    public int getOldVersion() {
        return oldVersion;
    }

    public int getNewVersion() {
        return newVersion;
    }

    public Bundle getUpdateInfo() {
        return updateInfo;
    }

    public UpdateListener getListener() {
        return listener;
    }

    public void onUpdateFinished() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getListener().onUpdateFinished();
            }
        });
    }

    public void onUpdateFailed() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getListener().onUpdateFailed();
            }
        });
    }

    public abstract void runUpdate();
}
