package top.helinyun.util.permissions.manager;

public interface RequestResultListener {
    void onRequestPermissionsResult(String permission, boolean granted, boolean shouldShowRequestPermissionRationale);
}
