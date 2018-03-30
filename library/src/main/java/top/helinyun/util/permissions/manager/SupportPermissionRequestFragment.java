package top.helinyun.util.permissions.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import top.helinyun.util.permissions.RequestManager;
import top.helinyun.util.permissions.util.Util;

public class SupportPermissionRequestFragment extends Fragment implements RequestExecutor {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private final ActivityFragmentLifecycle lifecycle = new ActivityFragmentLifecycle();
    private RequestManager requestManager;

    private final Set<RequestResultListener> resultListeners =
            Collections.newSetFromMap(new WeakHashMap<RequestResultListener, Boolean>());

    public void requestPermission(String[] permissions) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public boolean requestPermissionRationale(String permission) {
        return shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void addResultListener(RequestResultListener listener) {
        resultListeners.add(listener);
    }

    @Override
    public void removeResultListener(RequestResultListener listener) {
        resultListeners.remove(listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }

        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    void onRequestPermissionsResult(String[] permissions, int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            if (requestManager == null) {
                // No requestManager found
                return;
            }
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            for (RequestResultListener requestResultListener : Util.getSnapshot(resultListeners)) {
                requestResultListener.onRequestPermissionsResult(
                        permissions[i], granted, shouldShowRequestPermissionRationale[i]);
            }
        }
    }

    ActivityFragmentLifecycle getActivityLifecycle() {
        return lifecycle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycle.onAttach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }

    public RequestExecutor getExecutor() {
        return this;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(RequestManager requestManager) {
        this.requestManager = requestManager;
    }
}
