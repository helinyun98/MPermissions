package top.helinyun.util.permissions;

import android.content.Context;

public interface OnRequestHandler {
    void showRequestWhenDenied(Context context, String[] permissions, ProceedCallback callback);
}
