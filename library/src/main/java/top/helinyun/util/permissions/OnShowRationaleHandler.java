package top.helinyun.util.permissions;

import android.content.Context;

public interface OnShowRationaleHandler {
    void showRationaleForRequest(Context context, String[] permissions, ProceedCallback callback);
}
