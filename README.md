# MPermissions
Android Permissions Utils

# Gradle Config
    
Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    compile 'com.github.helinyun98:MPermissions:{latest version}'
}
```

# Usage
```
MPermissions.with(Activity.this)
               .request(Manifest.permission./* premissions */)
               .showRationale(new OnShowRationaleHandler() {
                   @Override
                   public void showRationaleForRequest(Context context, String[] permissions, ProceedCallback callback) {
                       /* why you need permission ( option ) */
                   }
               })
               .shouldRequestWhenDenied(new OnRequestHandler() {
                   @Override
                   public void showRequestWhenDenied(Context context, String[] permissions, final ProceedCallback callback) {
                       /* ask permissions once more ( option ) */
                   }
               })
               .start(new OnResultCallback() {
                   @Override
                   public void onGranted() {
                       /* get all permissions granted */
                   }
                   
                   @Override
                   public void onSomeDenied(String[] grantedPermissions, String[] deniedPermissions) {
                       /* some permissions has denied */
                   }
               });
```
