# MPermissions
Android Permissions Utils

# Gradle Config
    
    * comming soon

# Usage
    
     MPermissions.with(Activity.this)
                    .request(Manifest.permission./* premissions */)
                    .shouldExplainAfterRequest(new OnAfterRequestHandler() {
                        @Override
                        public void shouldShowExplainForRequest(Context context, String[] permissions, ProceedCallback callback) {
                            /* why you need permission ( option ) */
                        }
                    })
                    .shouldRequestWhenDenied(new OnRequestHandler() {
                        @Override
                        public void shouldShowRequestWhenDenied(Context context, String[] permissions, final ProceedCallback callback) {
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
