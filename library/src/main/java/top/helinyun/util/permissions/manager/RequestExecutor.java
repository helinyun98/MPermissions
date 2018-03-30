package top.helinyun.util.permissions.manager;

public interface RequestExecutor {

    void requestPermission(String[] permissions);

    boolean requestPermissionRationale(String permission);

    void addResultListener(RequestResultListener listener);

    void removeResultListener(RequestResultListener listener);
}
