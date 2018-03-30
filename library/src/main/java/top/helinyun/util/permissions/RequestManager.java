package top.helinyun.util.permissions;

import android.content.Context;

import top.helinyun.util.permissions.manager.Lifecycle;
import top.helinyun.util.permissions.manager.RequestExecutor;

public class RequestManager {

    private Context context;
    private Lifecycle lifecycle;
    private RequestExecutor executor;

    public RequestManager(Context context, Lifecycle lifecycle, RequestExecutor executor) {
        this.context = context;
        this.lifecycle = lifecycle;
        this.executor = executor;
    }

    public PermissionRequest request(String... permissions) {
        return new PermissionRequest(context, lifecycle, executor).request(permissions);
    }
}
