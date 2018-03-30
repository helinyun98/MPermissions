package top.helinyun.util.permissions.manager;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import top.helinyun.util.permissions.util.Util;

class ActivityFragmentLifecycle implements Lifecycle {
    private final Set<LifecycleListener> lifecycleListeners =
            Collections.newSetFromMap(new WeakHashMap<LifecycleListener, Boolean>());
    private boolean isReady;

    @Override
    public void addListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    void onAttach() {
        isReady = true;
        for (LifecycleListener lifecycleListener : Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onAttach();
        }
    }

    void onDestroy() {
        isReady = false;
        for (LifecycleListener lifecycleListener : Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onDestroy();
        }
    }
}
