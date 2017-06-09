package tuomomees.screentimecalculator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by tuomo on 6.6.2017.
 */

public class AppStatsManager extends AppCompatActivity {

    //Muuttujat, joihin alustetaan top5 käytetyt appsit
    long top1, top2, top3, top4, top5;

    //Muuttujat, joihin alustetaan TOP5 appsin nimi
    String top1App, top2App, top3App, top4App, top5App;

    //Muuttujat, joihin alustetaan TOP5 appsien pakettien nimet
    String top1Package, top2Package, top3Package, top4Package, top5Package;

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    long totalUsageTimeMillis = 0;
    long totalUsageTimeMinutes = 0;

    String applicationName = "Noutaminen ei onnistunut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);
    }

    //Metodi, jolla paketin nimen avulla voi hakea applikaation labelin
    public String getAppLabel(String packageName, Context context)
    {
        PackageManager packageManager = context.getPackageManager();

        try {
            applicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return applicationName;
    }

    //TOP5 applikaatiot tsekataan tässä metodissa
    public long[] checkMostUsed(String appName, String packageName, long usageTime)
    {
        StringBuilder top1StringBuilder = new StringBuilder();
        StringBuilder top2StringBuilder = new StringBuilder();
        StringBuilder top3StringBuilder = new StringBuilder();
        StringBuilder top4StringBuilder = new StringBuilder();
        StringBuilder top5StringBuilder = new StringBuilder();

        //Tarkastetaan onko appi jo top5 listalla, jos on, niin nollataan
        //checkIfAppAlreadyExist(appName);
        if(checkIfAppAlreadyExist(appName) == "null")
        {
            appName = "null";
            packageName = "null";
            usageTime = 0;
        }

        //Jos käyttöaika on isompi kuin top1
        if(usageTime > top1)
        {
            top5 = top4;
            top4 = top3;
            top3 = top2;
            top2 = top1;
            top1 = usageTime;

            //Log.d("Bigger than", "top1");

            top5App = top4App;
            top4App = top3App;
            top3App = top2App;
            top2App = top1App;
            top1App = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = top2Package;
            top2Package = top1Package;
            top1Package = packageName;

            //Log.d("top1 set: ", top1App);
        }

        //Jos käyttöaika on isompi kuin top2, mutta pienempi kuin top1
        else if(usageTime > top2 && usageTime < top1)
        {
            top5 = top4;
            top4 = top3;
            top3 = top2;
            top2 = usageTime;

            //Log.d("Bigger than", "top2");

            top5App = top4App;
            top4App = top3App;
            top3App = top2App;
            top2App = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = top2Package;
            top2Package = packageName;

            //Log.d("top2 set: ", top2App);
        }

        //Jos käyttöaika on isompi kuin top3, mutta pienempi kuin top2
        else if(usageTime > top3 && usageTime < top2)
        {
            top5 = top4;
            top4 = top3;
            top3 = usageTime;

            //Log.d("Bigger than", "top3");

            top5App = top4App;
            top4App = top3App;
            top3App = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = packageName;

            //Log.d("top3 set: ", top3App);
        }

        //Jos käyttöaika on isompi kuin top4, mutta pienempi kuin top3
        else if(usageTime > top4 && usageTime < top3)
        {
            top5 = top4;
            top4 = usageTime;

            //Log.d("Bigger than", "top4");

            top5App = top4App;
            top4App = appName;

            top5Package = top4Package;
            top4Package = packageName;

            //Log.d("top4 set: ", top4App);
        }

        //jos käyttöaika on isompi kuin top4, mutta pienempi kuin top4
        else if(usageTime > top5 && usageTime < top4)
        {
            top5 = usageTime;

            //Log.d("Bigger than", "top5");
            top5App = appName;
            top5Package = packageName;

            //Log.d("top5 set: ", top5App);
        }

        long top1Min = convertMillisToMinutes(top1);
        long top2Min = convertMillisToMinutes(top2);
        long top3Min = convertMillisToMinutes(top3);
        long top4Min = convertMillisToMinutes(top4);
        long top5Min = convertMillisToMinutes(top5);

        top1StringBuilder.append("1. " + top1App + "\r\n" + top1Min + " min" + "\r\n");
        top2StringBuilder.append("2. " + top2App + "\r\n" + top2Min + " min" + "\r\n");
        top3StringBuilder.append("3. " + top3App + "\r\n" + top3Min + " min" + "\r\n");
        top4StringBuilder.append("4. " + top4App + "\r\n" + top4Min + " min" + "\r\n");
        top5StringBuilder.append("5. " + top5App + "\r\n" + top5Min + " min" + "\r\n");

        return new long[] {top1, top2, top3, top4, top5};
    }



    //Metodi, joka tarkastaa onko kyseinen appi jo TOP5 listalla
    protected String checkIfAppAlreadyExist(String appName)
    {
        if(appName.equals(top1App) || appName.equals(top2App) || appName.equals(top3App) || appName.equals(top4App) || appName.equals(top5App))
        {
            Log.d("Toistamiseen", appName);
            appName = "null";
        }

        else
        {
            //Do nothing
        }

        return appName;
    }

    protected long convertMillisToMinutes(long millis)
    {
        long minutes = (millis / 1000) / 60;
        return minutes;
    }


}
