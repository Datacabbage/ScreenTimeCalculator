package tuomomees.screentimecalculator;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 11.7.2017.
 */

class AppStatsQueryThread extends Thread {

    //TESTI
    private Map<String, UsageStats> usageStatsUsageTimeApps;
    private List<UsageStats> listUsageTimeApps;

    //Taulukot, johon haetaan viimeksi käytettyjen appien tiedot
    private Map<String, UsageStats> usageStatsLastUsedApps;
    private List<UsageStats> listLastUsedApps;

    //Alustetaan lista, johon tulee käyttötiedot
    List<UsageStats> lUsageStatsList;

    //Muuttujat, jotka sisältävät Applikaation nimen, top5Millis numeron ja käyttöajan
    private String top1AppInfo, top2AppInfo, top3AppInfo, top4AppInfo, top5AppInfo;

    //Muuttujat, jotka sisältävät applikaation paketin nimen
    private String top1Package, top2Package, top3Package, top4Package, top5Package;

    //Muuttujat, joihin alustetaan TOP5 appsin nimi
    private String top1AppName, top2AppName, top3AppName, top4AppName, top5AppName;

    //Muuttujat, joihin alustetaan top5 appien käyttöaika millisekunneissa
    private long top1Millis, top2Millis, top3Millis, top4Millis, top5Millis;

    //Kokonaiskäyttöaika
    private String totalUsage = null;

    private AppStatsManager aStatsManager = new AppStatsManager();
    private Converter timeConverter = new Converter();

    private int counter = 0;

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    private long totalUsageTimeMillis = 0;
    private long totalUsageTimeMinutes = 0;

    private Context mContext = null;

    AppStatsQueryThread(Context context){
        mContext = context;
    }

    public void run() {

        final UsageStatsManager lUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar cal = Calendar.getInstance();
        //Nykyinen aika - yksi päivä
        //cal.add(Calendar.DAY_OF_WEEK, - 1);

        //Alkuperäinen query, jolla tulee ongelmallisesti tuplatapauksia
        //lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(), System.currentTimeMillis());

        //usageStats = lUsageStatsManager.queryAndAggregateUsageStats(cal.getTimeInMillis(), System.currentTimeMillis());

        //Hakee tiedot 24H sisällä eli 86400000 millis
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsUsageTimeApps = lUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), System.currentTimeMillis());
        }
        listUsageTimeApps = new ArrayList<>();
        listUsageTimeApps.addAll(usageStatsUsageTimeApps.values());

        Log.d("How many apps ", String.valueOf(listUsageTimeApps.size()));


        //Looppi, joka käy läpi käyttäjän kaikki appsit
        for(UsageStats lUsageStats:listUsageTimeApps){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                counter++;

                //Hakee applikaation nimen
                String aLabelName = aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext());
                //Hakee applikaation paketin nimen
                String aPackageName = lUsageStats.getPackageName();
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                //Alustaa taulukon, johon tulee käyttäjän kaikki applikaatiot (nimi, id, käyttöaika)
                //initializeAppArray(aLabelName, totalTimeInForeground);

                //Mikäli appsia on käytetty enemmän kuin minuutti
                if(timeConverter.convertMillisToMinutes(totalTimeInForeground) > 0 )
                {
                    //Tarkastaa TOP5 käytetyimmät appsit
                    checkMostUsed(aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext()),lUsageStats.getPackageName(), lUsageStats.getTotalTimeInForeground());

                    //Tarkastaa yhteensä appsien käyttämän ajan
                    calculateTotalTime(lUsageStats.getTotalTimeInForeground(), aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext()));
                }

                if(counter == listUsageTimeApps.size())
                {
                    setSharedPreference("sharedStats", "totalUsage", totalUsage);
                    setSharedPreference("sharedStats", "top1AppInfo", top1AppInfo);
                    setSharedPreference("sharedStats", "top2AppInfo", top2AppInfo);
                    setSharedPreference("sharedStats", "top3AppInfo", top3AppInfo);
                    setSharedPreference("sharedStats", "top4AppInfo", top4AppInfo);
                    setSharedPreference("sharedStats", "top5AppInfo", top5AppInfo);

                    setSharedPreference("sharedStats", "top1AppPackage", top1Package);
                    setSharedPreference("sharedStats", "top2AppPackage", top2Package);
                    setSharedPreference("sharedStats", "top3AppPackage", top3Package);
                    setSharedPreference("sharedStats", "top4AppPackage", top4Package);
                    setSharedPreference("sharedStats", "top5AppPackage", top5Package);
                }
            }
        }
    }

    //Metodi, jolla voi lisätä jaetun muuttujan
    private void setSharedPreference(String sharedPrefTag, String sharedVariableTag, String sharedVariable)
    {
        //Lähetetään tiedot Fragmenttiin SharedPreferencen avulla
        SharedPreferences pref = mContext.getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(sharedVariableTag, sharedVariable);
        editor.apply();
        Log.d("Shared variable", sharedVariable + " w/ tag: " + sharedVariableTag);
    }

    //Metodi, jolla lasketaan yhteen kaikki käyttöajat
    private String calculateTotalTime(long usageTime, String appName)
    {
        StringBuilder totalUsageStringBuilder = new StringBuilder();

        totalUsageTimeMillis = totalUsageTimeMillis + usageTime;
        totalUsageTimeMinutes = timeConverter.convertMillisToMinutes(totalUsageTimeMillis);

        totalUsageStringBuilder.append(mContext.getResources().getString(R.string.totalusage_text)).append("\r\n").append(timeConverter.convertMillisToHoursMinutesSeconds(totalUsageTimeMillis));
        totalUsage = totalUsageStringBuilder.toString();

        return totalUsage;
    }

    //TOP5 applikaatiot tsekataan tässä metodissa
    private long[] checkMostUsed(String appName, String packageName, long usageTime)
    {
        StringBuilder top1StringBuilder = new StringBuilder();
        StringBuilder top2StringBuilder = new StringBuilder();
        StringBuilder top3StringBuilder = new StringBuilder();
        StringBuilder top4StringBuilder = new StringBuilder();
        StringBuilder top5StringBuilder = new StringBuilder();

        //Jos käyttöaika on isompi kuin top1Millis
        if(usageTime > top1Millis)
        {
            top5Millis = top4Millis;
            top4Millis = top3Millis;
            top3Millis = top2Millis;
            top2Millis = top1Millis;
            top1Millis = usageTime;

            //Log.d("Bigger than", "top1Millis");

            top5AppName = top4AppName;
            top4AppName = top3AppName;
            top3AppName = top2AppName;
            top2AppName = top1AppName;
            top1AppName = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = top2Package;
            top2Package = top1Package;
            top1Package = packageName;

            //Log.d("top1Millis set: ", top1AppName);
        }

        //Jos käyttöaika on isompi kuin top2Millis, mutta pienempi kuin top1Millis
        else if(usageTime > top2Millis && usageTime < top1Millis)
        {
            top5Millis = top4Millis;
            top4Millis = top3Millis;
            top3Millis = top2Millis;
            top2Millis = usageTime;

            //Log.d("Bigger than", "top2Millis");

            top5AppName = top4AppName;
            top4AppName = top3AppName;
            top3AppName = top2AppName;
            top2AppName = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = top2Package;
            top2Package = packageName;

            //Log.d("top2Millis set: ", top2AppName);
        }

        //Jos käyttöaika on isompi kuin top3Millis, mutta pienempi kuin top2Millis
        else if(usageTime > top3Millis && usageTime < top2Millis)
        {
            top5Millis = top4Millis;
            top4Millis = top3Millis;
            top3Millis = usageTime;

            //Log.d("Bigger than", "top3Millis");

            top5AppName = top4AppName;
            top4AppName = top3AppName;
            top3AppName = appName;

            top5Package = top4Package;
            top4Package = top3Package;
            top3Package = packageName;

            //Log.d("top3Millis set: ", top3AppName);
        }

        //Jos käyttöaika on isompi kuin top4Millis, mutta pienempi kuin top3Millis
        else if(usageTime > top4Millis && usageTime < top3Millis)
        {
            top5Millis = top4Millis;
            top4Millis = usageTime;

            //Log.d("Bigger than", "top4Millis");

            top5AppName = top4AppName;
            top4AppName = appName;

            top5Package = top4Package;
            top4Package = packageName;

            //Log.d("top4Millis set: ", top4AppName);
        }

        //jos käyttöaika on isompi kuin top4Millis, mutta pienempi kuin top4Millis
        else if(usageTime > top5Millis && usageTime < top4Millis)
        {
            top5Millis = usageTime;

            //Log.d("Bigger than", "top5Millis");
            top5AppName = appName;
            top5Package = packageName;

            //Log.d("top5Millis set: ", top5AppName);
        }

        /*
        Log.d("top1", top1AppName + " " + timeConverter.convertMillisToHoursMinutesSeconds(top1Millis));
        Log.d("top2", top2AppName + " " + timeConverter.convertMillisToHoursMinutesSeconds(top2Millis));
        Log.d("top3", top3AppName + " " + timeConverter.convertMillisToHoursMinutesSeconds(top3Millis));
        Log.d("top4", top4AppName + " " + timeConverter.convertMillisToHoursMinutesSeconds(top4Millis));
        Log.d("top5", top5AppName + " " + timeConverter.convertMillisToHoursMinutesSeconds(top5Millis));
*/
        String top1Time = timeConverter.convertMillisToHoursMinutesSeconds(top1Millis);
        String top2Time = timeConverter.convertMillisToHoursMinutesSeconds(top2Millis);
        String top3Time = timeConverter.convertMillisToHoursMinutesSeconds(top3Millis);
        String top4Time = timeConverter.convertMillisToHoursMinutesSeconds(top4Millis);
        String top5Time = timeConverter.convertMillisToHoursMinutesSeconds(top5Millis);

        top1StringBuilder.append("1. ").append(top1AppName).append("\r\n").append(top1Time).append("\r\n");
        top2StringBuilder.append("2. ").append(top2AppName).append("\r\n").append(top2Time).append("\r\n");
        top3StringBuilder.append("3. ").append(top3AppName).append("\r\n").append(top3Time).append("\r\n");
        top4StringBuilder.append("4. ").append(top4AppName).append("\r\n").append(top4Time).append("\r\n");
        top5StringBuilder.append("5. ").append(top5AppName).append("\r\n").append(top5Time).append("\r\n");

        String top1AppText, top2AppText, top3AppText, top4AppText, top5AppText;

        top1AppInfo = top1StringBuilder.toString();
        top2AppInfo = top2StringBuilder.toString();
        top3AppInfo = top3StringBuilder.toString();
        top4AppInfo = top4StringBuilder.toString();
        top5AppInfo = top5StringBuilder.toString();


        return new long[] {top1Millis, top2Millis, top3Millis, top4Millis, top5Millis};
    }
}
