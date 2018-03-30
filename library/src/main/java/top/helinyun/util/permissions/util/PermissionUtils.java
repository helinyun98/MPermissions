package top.helinyun.util.permissions.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.util.SimpleArrayMap;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * PermissionsDispatcher Utils
 */
public class PermissionUtils {
    // Map of dangerous permissions introduced in later framework versions.
    // Used to conditionally bypass permission-hold checks on older devices.
    private static final SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;

    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(8);
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23);
    }

    private PermissionUtils() {
    }

    /**
     * Returns true if the permission exists in this SDK version
     *
     * @param permission permission
     * @return returns true if the permission exists in this SDK version
     */
    private static boolean permissionExists(String permission) {
        // Check if the permission could potentially be missing on this device
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        // If null was returned from the above call, there is no need for a device API level check for the permission;
        // otherwise, we check if its minimum API level requirement is met
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion;
    }

    /**
     * Returns true if the Activity or Fragment has access to given permission.
     *
     * @param context    context
     * @param permission permission list
     * @return returns true if the Activity or Fragment has access to given permission.
     */
    public static boolean hasSelfPermissions(Context context, String permission) {
        return !(permissionExists(permission)
                && !hasSelfPermission(context, permission));
    }

    /**
     * Determine context has access to the given permission.
     * <p>
     * This is a workaround for RuntimeException of Parcel#readException.
     * For more detail, check this issue https://github.com/hotchemi/PermissionsDispatcher/issues/107
     *
     * @param context    context
     * @param permission permission
     * @return returns true if context has access to the given permission, false otherwise.
     * @see #hasSelfPermissions(Context, String)
     */
    private static boolean hasSelfPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
            return hasSelfPermissionForXiaomi(context, permission);
        }
        try {
            return checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException t) {
            return false;
        }
    }

    private static boolean hasSelfPermissionForXiaomi(Context context, String permission) {
        String permissionToOp = AppOpsManagerCompat.permissionToOp(permission);
        if (permissionToOp == null) {
            // in case of normal permissions(e.g. INTERNET)
            return true;
        }
        int noteOp = AppOpsManagerCompat.noteOp(context, permissionToOp, Process.myUid(), context.getPackageName());
        return noteOp == AppOpsManagerCompat.MODE_ALLOWED && checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}