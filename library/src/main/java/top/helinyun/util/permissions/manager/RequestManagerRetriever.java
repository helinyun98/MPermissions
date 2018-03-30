package top.helinyun.util.permissions.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import top.helinyun.util.permissions.RequestManager;

public class RequestManagerRetriever {

    private static final RequestManagerRetriever INSTANCE = new RequestManagerRetriever();
    private static final String FRAGMENT_TAG = "cn.dface.util.permissions";

    public static RequestManagerRetriever get() {
        return INSTANCE;
    }

    public RequestManager get(final FragmentActivity activity) {
        assertNotDestroyed(activity);
        FragmentManager fm = activity.getSupportFragmentManager();
        return supportFragmentGet(activity, fm);
    }

    public RequestManager get(Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a request on a fragment before it is attached");
        }
        return get(fragment.getActivity());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RequestManager get(Activity activity) {
        assertNotDestroyed(activity);
        android.app.FragmentManager fm = activity.getFragmentManager();
        return fragmentGet(activity, fm);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public RequestManager get(android.app.Fragment fragment) {
        if (fragment.getActivity() == null) {
            throw new IllegalArgumentException("You cannot start a request on a fragment before it is attached");
        }
        return get(fragment.getActivity());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a request for a destroyed activity");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    PermissionRequestFragment getRequestManagerFragment(final android.app.FragmentManager fm) {
        PermissionRequestFragment current =
                (PermissionRequestFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = new PermissionRequestFragment();
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return current;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    RequestManager fragmentGet(Context context, android.app.FragmentManager fm) {
        PermissionRequestFragment current = getRequestManagerFragment(fm);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            requestManager = new RequestManager(context, current.getActivityLifecycle(), current.getExecutor());
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

    SupportPermissionRequestFragment getSupportRequestManagerFragment(final FragmentManager fm) {
        SupportPermissionRequestFragment current =
                (SupportPermissionRequestFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = new SupportPermissionRequestFragment();
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        return current;
    }

    RequestManager supportFragmentGet(Context context, FragmentManager fm) {
        SupportPermissionRequestFragment current = getSupportRequestManagerFragment(fm);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            requestManager = new RequestManager(context, current.getActivityLifecycle(), current.getExecutor());
            current.setRequestManager(requestManager);
        }
        return requestManager;
    }

}
