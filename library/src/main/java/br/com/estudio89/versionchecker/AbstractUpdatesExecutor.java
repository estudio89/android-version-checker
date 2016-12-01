package br.com.estudio89.versionchecker;

import android.content.Context;
import android.os.Bundle;

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
    }

    public AbstractUpdatesExecutor(Context context, Bundle updateInfo, UpdateListener listener) {
        this.context = context;
        this.updateInfo = updateInfo;
        this.listener = listener;

        oldVersion = updateInfo.getInt(VersionChecker.PENDING_INFO_OLD_VERSION);
        newVersion = updateInfo.getInt(VersionChecker.PENDING_INFO_NEW_VERSION);
    }

    public abstract void runUpdate();
}
