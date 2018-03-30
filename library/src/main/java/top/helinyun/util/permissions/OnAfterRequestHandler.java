package top.helinyun.util.permissions;

import android.content.Context;

public interface OnAfterRequestHandler {
    void shouldShowExplainForRequest(Context context, String[] permissions, ProceedCallback callback);
}
