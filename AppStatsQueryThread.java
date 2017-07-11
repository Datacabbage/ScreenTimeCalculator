package tuomomees.screentimecalculator;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private int counter2 = 0;

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    private long totalUsageTimeMillis = 0;
    private long totalUsageTimeMinutes = 0;

    private Context mContext = null;

    private long top1 = 0, top2 = 0, top3 = 0, top4 = 0, top5 = 0;
    private String top1App = null, top2App = null, top3App = null, top4App = null, top5App = null;

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
            final Date currentDate = Calendar.getInstance().getTime();

            Log.d("DATE", String.valueOf(currentDate));
            //usageStatsUsageTimeApps = lUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), System.currentTimeMillis());

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


                    //Hakee koska appia on viimeksi käytetty
                    long lastTimeUsed = lUsageStats.getLastTimeUsed();



                    //Log.d(aLabelName, " viimeksi käytetty " + lastTimeUsedString);

                    //Tarkastaa mitä appeja on käytetty viimeksi TOP5
                    checkLastUsedApp(lastTimeUsed, aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext()));

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


                    StringBuilder top1StringBuilder = new StringBuilder();
                    StringBuilder top2StringBuilder = new StringBuilder();
                    StringBuilder top3StringBuilder = new StringBuilder();
                    StringBuilder top4StringBuilder = new StringBuilder();
                    StringBuilder top5StringBuilder = new StringBuilder();

                    String str1 = timeConverter.convertMillisToDate(top1);
                    String str2 = timeConverter.convertMillisToDate(top2);
                    String str3 = timeConverter.convertMillisToDate(top3);
                    String str4 = timeConverter.convertMillisToDate(top4);
                    String str5 = timeConverter.convertMillisToDate(top5);

                    top1StringBuilder.append("1. ").append(top1App).append("\r\n").append(str1).append("\r\n");
                    top2StringBuilder.append("2. ").append(top2App).append("\r\n").append(str2).append("\r\n");
                    top3StringBuilder.append("3. ").append(top3App).append("\r\n").append(str3).append("\r\n");
                    top4StringBuilder.append("4. ").append(top4App).append("\r\n").append(str4).append("\r\n");
                    top5StringBuilder.append("5. ").append(top5App).append("\r\n").append(str5).append("\r\n");

                    String top1LastTimeUsedInfo = top1StringBuilder.toString();
                    String top2LastTimeUsedInfo = top2StringBuilder.toString();
                    String top3LastTimeUsedInfo = top3StringBuilder.toString();
                    String top4LastTimeUsedInfo = top4StringBuilder.toString();
                    String top5LastTimeUsedInfo = top5StringBuilder.toString();

                    setSharedPreference("sharedStats", "top1LastUsed", top1LastTimeUsedInfo);
                    setSharedPreference("sharedStats", "top2LastUsed", top2LastTimeUsedInfo);
                    setSharedPreference("sharedStats", "top3LastUsed", top3LastTimeUsedInfo);
                    setSharedPreference("sharedStats", "top4LastUsed", top4LastTimeUsedInfo);
                    setSharedPreference("sharedStats", "top5LastUsed", top5LastTimeUsedInfo);
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

        top1AppInfo = top1StringBuilder.toString();
        top2AppInfo = top2StringBuilder.toString();
        top3AppInfo = top3StringBuilder.toString();
        top4AppInfo = top4StringBuilder.toString();
        top5AppInfo = top5StringBuilder.toString();

        return new long[] {top1Millis, top2Millis, top3Millis, top4Millis, top5Millis};
    }

    //Metodi, jolla näkee mitä appeja on käytetty viimeksi top5
    private void checkLastUsedApp(long lastTimeUsed, String appName)
    {
        if(lastTimeUsed > top1)
        {
            top5 = top4;
            top4 = top3;
            top3 = top2;
            top2 = top1;
            top1 = lastTimeUsed;

            top5App = top4App;
            top4App = top3App;
            top3App = top2App;
            top2App = top1App;
            top1App = appName;
        }

        if(lastTimeUsed > top2 && lastTimeUsed < top1)
        {
            top5 = top4;
            top4 = top3;
            top3 = top2;
            top2 = lastTimeUsed;

            top5App = top4App;
            top4App = top3App;
            top3App = top2App;
            top2App = appName;
        }

        if(lastTimeUsed > top3 && lastTimeUsed < top2)
        {
            top5 = top4;
            top4 = top3;
            top3 = lastTimeUsed;

            top5App = top4App;
            top4App = top3App;
            top3App = appName;
        }

        if(lastTimeUsed > top4 && lastTimeUsed < top3)
        {
            top5 = top4;
            top4 = lastTimeUsed;

            top5App = top4App;
            top4App = appName;
        }

        if(lastTimeUsed > top5 && lastTimeUsed < top4)
        {
            top5 = lastTimeUsed;

            top5App = appName;
        }
    }
}
