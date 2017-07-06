package tuomomees.screentimecalculator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Luokan on luonut tuomo päivämäärällä 6.6.2017.
 */

public class AppStatsManager extends AppCompatActivity {

    //Muuttujat, joihin alustetaan top5Millis käytetyt appsit
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

        //Tarkastetaan onko appi jo top5Millis listalla, jos on, niin nollataan
        //checkIfAppAlreadyExist(appName);
        if(checkIfAppAlreadyExist(appName).equals("null"))
        {
            appName = "null";
            packageName = "null";
            usageTime = 0;
        }

        //Jos käyttöaika on isompi kuin top1Millis
        if(usageTime > top1)
        {
            top5 = top4;
            top4 = top3;
            top3 = top2;
            top2 = top1;
            top1 = usageTime;

            //Log.d("Bigger than", "top1Millis");

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

            //Log.d("top1Millis set: ", top1AppName);
        }

        //Jos käyttöaika on isompi kuin top2Millis, mutta pienempi kuin top1Millis
        else if(usageTime > top2 && usageTime < top1)
        {
            top5 = top4;
            top4 = top3;
            top3 = top2;
            top2 = usageTime;

            //Log.d("Bigger than", "top2Millis");

            top5App = top4App;
            top4App = top3App;
            top3App = top2App;
            top2App = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = top2Package;
            top2Package = packageName;

            //Log.d("top2Millis set: ", top2AppName);
        }

        //Jos käyttöaika on isompi kuin top3Millis, mutta pienempi kuin top2Millis
        else if(usageTime > top3 && usageTime < top2)
        {
            top5 = top4;
            top4 = top3;
            top3 = usageTime;

            //Log.d("Bigger than", "top3Millis");

            top5App = top4App;
            top4App = top3App;
            top3App = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = packageName;

            //Log.d("top3Millis set: ", top3AppName);
        }

        //Jos käyttöaika on isompi kuin top4Millis, mutta pienempi kuin top3Millis
        else if(usageTime > top4 && usageTime < top3)
        {
            top5 = top4;
            top4 = usageTime;

            //Log.d("Bigger than", "top4Millis");

            top5App = top4App;
            top4App = appName;

            top5Package = top4Package;
            top4Package = packageName;

            //Log.d("top4Millis set: ", top4AppName);
        }

        //jos käyttöaika on isompi kuin top4Millis, mutta pienempi kuin top4Millis
        else if(usageTime > top5 && usageTime < top4)
        {
            top5 = usageTime;

            //Log.d("Bigger than", "top5Millis");
            top5App = appName;
            top5Package = packageName;

            //Log.d("top5Millis set: ", top5AppName);
        }

        Converter converter = null;


        String top1Min = converter.convertMillisToHoursMinutesSeconds(top1);
        String top2Min = converter.convertMillisToHoursMinutesSeconds(top2);
        String top3Min = converter.convertMillisToHoursMinutesSeconds(top3);
        String top4Min = converter.convertMillisToHoursMinutesSeconds(top4);
        String top5Min = converter.convertMillisToHoursMinutesSeconds(top5);


        top1StringBuilder.append("1. ").append(top1App).append("\r\n").append(top1Min).append(" min").append("\r\n");
        top2StringBuilder.append("2. ").append(top2App).append("\r\n").append(top2Min).append(" min").append("\r\n");
        top3StringBuilder.append("3. ").append(top3App).append("\r\n").append(top3Min).append(" min").append("\r\n");
        top4StringBuilder.append("4. ").append(top4App).append("\r\n").append(top4Min).append(" min").append("\r\n");
        top5StringBuilder.append("5. ").append(top5App).append("\r\n").append(top5Min).append(" min").append("\r\n");

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

        return appName;
    }

}
