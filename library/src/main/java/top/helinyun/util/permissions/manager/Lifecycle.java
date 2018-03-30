package top.helinyun.util.permissions.manager;

public interface Lifecycle {

    void addListener(LifecycleListener listener);

    boolean isReady();
}
