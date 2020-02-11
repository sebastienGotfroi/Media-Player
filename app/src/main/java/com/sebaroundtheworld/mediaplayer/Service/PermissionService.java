package com.sebaroundtheworld.mediaplayer.Service;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionService {

    public static boolean hasPermission (String permission, int constantPermissionRequest, Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{permission}, constantPermissionRequest);
            return false;
        } else {
            return true;
        }
    }
}
