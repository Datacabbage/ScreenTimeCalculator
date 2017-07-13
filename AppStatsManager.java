package tuomomees.screentimecalculator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Luokan on luonut tuomo päivämäärällä 6.6.2017.
 */

public class AppStatsManager extends AppCompatActivity {

    String applicationName = "Noutaminen ei onnistunut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Metodi, jolla paketin nimen avulla voi hakea applikaation labelin
    public String getAppLabel(String packageName, Context context)
    {
        PackageManager packageManager = context.getPackageManager();

        try {
            applicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

        } catch (PackageManager.NameNotFoundException e) {
            Toast toast = Toast.makeText(this, "error in getting name", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }

        return applicationName;
    }

    //Metodi, jolla voi hakea tarvittavien applikaatioiden app-ikonit paketin nimen avulla
    protected Drawable getIconDrawable(String packageName, Context context) {
        Drawable icon;

        PackageManager packageManager = context.getPackageManager();
        try {
            icon = packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
            toast.show();
            icon = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null);
            e.printStackTrace();
        }

        return icon;
    }
}
