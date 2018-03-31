package top.helinyun.permissions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import top.helinyun.util.permissions.MPermissions;
import top.helinyun.util.permissions.OnShowRationaleHandler;
import top.helinyun.util.permissions.OnRequestHandler;
import top.helinyun.util.permissions.OnResultCallback;
import top.helinyun.util.permissions.ProceedCallback;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "PermissionsDemo";
    private Button write;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        write = findViewById(R.id.write);
        setListener();
    }

    private void setListener() {
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFileWithPermission();
            }
        });
    }

    private void writeFileWithPermission() {
        MPermissions.with(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .showRationale(new OnShowRationaleHandler() {
                    @Override
                    public void showRationaleForRequest(Context context, String[] permissions, ProceedCallback callback) {
                        showRationaleDialog(callback);
                    }
                })
                .shouldRequestWhenDenied(new OnRequestHandler() {
                    @Override
                    public void showRequestWhenDenied(Context context, String[] permissions, ProceedCallback callback) {
                        showRequestAgainDialog(callback);
                    }
                })
                .start(new OnResultCallback() {
                    @Override
                    public void onGranted() {
                        try {
                            boolean success = writeTest();
                            Toast.makeText(getApplicationContext(),
                                    "success" + success,
                                    Toast.LENGTH_LONG)
                                    .show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSomeDenied(String[] grantedPermissions, String[] deniedPermissions) {
                        showDeniedResult(grantedPermissions, deniedPermissions);
                    }
                });
    }

    private void showRationaleDialog(final ProceedCallback callback) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("rationale")
                .setMessage("need write permission for output files")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.proceed();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.cancel();
                    }
                })
                .create()
                .show();
    }

    private void showRequestAgainDialog(final ProceedCallback callback) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("request once more")
                .setMessage("need write permission for output files")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.proceed();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.cancel();
                    }
                })
                .create()
                .show();
    }

    private void showDeniedResult(String[] grantedPermissions, String[] deniedPermissions) {
        Toast.makeText(getApplicationContext(),
                "grantedPermissions : " + Arrays.toString(grantedPermissions)
                        + "deniedPermissions : " + Arrays.toString(deniedPermissions),
                Toast.LENGTH_LONG).show();
        Log.d(TAG,
                "grantedPermissions : " + Arrays.toString(grantedPermissions)
                        + "deniedPermissions : " + Arrays.toString(deniedPermissions));
    }

    private boolean writeTest() throws IOException {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists() || !directory.canWrite()) return false;
        File file = new File(directory, "ANDROID.PERMISSION.TEST");
        if (file.exists()) {
            return file.delete();
        } else {
            return file.createNewFile();
        }
    }
}
