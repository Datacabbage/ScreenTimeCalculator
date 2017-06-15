package tuomomees.screentimecalculator;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Luokan on luonut tuomo päivämäärällä 6.6.2017.
 */

public class PermissionManager extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void requestPermission(Intent intent) {
        Toast.makeText(this, "Pyydetään tarvittavia oikeuksia.", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }
    }

    protected boolean hasPermission(AppOpsManager appOpsManager) {
        //AppOpsManager appOps = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appOpsManager = (AppOpsManager)
                    getSystemService(Context.APP_OPS_SERVICE);
        }
        int mode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            assert appOpsManager != null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        Process.myUid(), getPackageName());
            }
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "resultCode " + resultCode);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:

                break;
        }
    }
}
