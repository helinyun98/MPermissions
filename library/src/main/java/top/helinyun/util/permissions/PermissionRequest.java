package top.helinyun.util.permissions;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.helinyun.util.permissions.manager.Lifecycle;
import top.helinyun.util.permissions.manager.LifecycleListener;
import top.helinyun.util.permissions.manager.RequestExecutor;
import top.helinyun.util.permissions.manager.RequestResultListener;
import top.helinyun.util.permissions.util.PermissionUtils;
import top.helinyun.util.permissions.util.Util;

@SuppressWarnings("unused")
public class PermissionRequest implements LifecycleListener, RequestResultListener {

    private Context context;
    private Lifecycle lifecycle;
    private RequestExecutor executor;

    private OnShowRationaleHandler afterHandler;
    private OnRequestHandler handler;
    private OnResultCallback callback;

    private Set<String> needPermissions = new HashSet<>();
    private List<String> pendingRequestPermissions = new ArrayList<>();
    private Set<String> delayRequestPermissions = new HashSet<>();
    private Set<String> shouldShowRationale = new HashSet<>();
    private Set<String> grantedPermissions = new HashSet<>();
    private Set<String> deniedPermissions = new HashSet<>();

    PermissionRequest(Context context, Lifecycle lifecycle, RequestExecutor executor) {
        this.context = context;
        this.lifecycle = lifecycle;
        this.executor = executor;
        lifecycle.addListener(this);
        executor.addResultListener(this);
    }

    public PermissionRequest request(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("MPermissions.request requires at least one input permission");
        }
        Collections.addAll(needPermissions, permissions);
        return this;
    }

    public PermissionRequest showRationale(OnShowRationaleHandler afterHandler) {
        this.afterHandler = afterHandler;
        return this;
    }

    public PermissionRequest shouldRequestWhenDenied(OnRequestHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public void onAttach() {
        if (!delayRequestPermissions.isEmpty()) {
            String[] permissions = delayRequestPermissions.toArray(new String[delayRequestPermissions.size()]);
            requestInternal(permissions);
            delayRequestPermissions.clear();
        }
    }

    @Override
    public void onDestroy() {
        executor.removeResultListener(this);
    }

    public void start(OnResultCallback callback) {
        Util.assertMainThread();
        this.callback = callback;

        for (String permission : needPermissions) {
            if (PermissionUtils.hasSelfPermissions(context, permission)) {
                grantedPermissions.add(permission);
                continue;
            }
            pendingRequestPermissions.add(permission);
        }

        if (!pendingRequestPermissions.isEmpty()) {
            if (lifecycle.isReady()) {
                String[] permissions = pendingRequestPermissions.toArray(new String[pendingRequestPermissions.size()]);
                requestInternal(permissions);
            } else {
                delayRequestPermissions.addAll(pendingRequestPermissions);
            }
        } else {
            finishRequest();
        }
    }

    private void requestInternal(String[] permissions) {
        boolean needExplain = false;
        for (String permission : permissions) {
            if (executor.requestPermissionRationale(permission)) {
                needExplain = true;
                break;
            }
        }
        if (needExplain && afterHandler != null) {
            afterHandler.showRationaleForRequest(context, permissions, getProceedHandler(permissions));
        } else {
            executor.requestPermission(permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(String permission, boolean granted, boolean shouldShowRequestPermissionRationale) {
        if (granted) {
            grantedPermissions.add(permission);
        } else if (handler != null && shouldShowRequestPermissionRationale) {
            shouldShowRationale.add(permission);
        } else {
            deniedPermissions.add(permission);
        }

        if (grantedPermissions.size() + deniedPermissions.size()
                == needPermissions.size()) {
            finishRequest();
            return;
        }

        if (shouldShowRationale.size() + grantedPermissions.size() + deniedPermissions.size()
                == needPermissions.size()) {
            final String[] needProceedPermissions = shouldShowRationale.toArray(new String[shouldShowRationale.size()]);
            handler.showRequestWhenDenied(context, needProceedPermissions, getProceedHandler(needProceedPermissions));
        }
    }

    @NonNull
    private ProceedCallback getProceedHandler(final String[] needProceedPermissions) {
        return new ProceedCallback() {
            @Override
            public void proceed() {
                if (lifecycle.isReady()) {
                    executor.requestPermission(needProceedPermissions);
                }
            }

            @Override
            public void cancel() {
                Collections.addAll(deniedPermissions, needProceedPermissions);
                finishRequest();
            }
        };
    }

    private void finishRequest() {
        if (grantedPermissions.size() == needPermissions.size()) {
            callback.onGranted();
        } else {
            String[] grantedPermissions = this.grantedPermissions.toArray(new String[this.grantedPermissions.size()]);
            String[] deniedPermissions = this.deniedPermissions.toArray(new String[this.deniedPermissions.size()]);
            callback.onSomeDenied(grantedPermissions, deniedPermissions);
        }
        executor.removeResultListener(this);
        callback = null;
    }
}
