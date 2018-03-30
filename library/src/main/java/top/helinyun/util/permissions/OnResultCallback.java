package top.helinyun.util.permissions;

public interface OnResultCallback {

    void onGranted();

    void onSomeDenied(String[] grantedPermissions, String[] deniedPermissions);
}
