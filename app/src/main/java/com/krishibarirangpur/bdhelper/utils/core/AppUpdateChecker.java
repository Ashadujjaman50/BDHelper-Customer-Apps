package com.krishibarirangpur.bdhelper.utils.core;

import android.app.Activity;
import android.content.IntentSender;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;

public class AppUpdateChecker {

    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    public static final int REQUEST_CODE = 124;

    public interface OnUpdateCheckListener {
        void onNoUpdate();
    }

    public AppUpdateChecker(Activity activity) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity.getApplicationContext());
    }

    public void checkForUpdate(OnUpdateCheckListener listener) {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity,
                            REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    listener.onNoUpdate();
                }
            } else {
                listener.onNoUpdate();
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            listener.onNoUpdate();
        });
    }
}
