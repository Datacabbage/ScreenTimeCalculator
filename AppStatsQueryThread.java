package tuomomees.screentimecalculator;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 11.7.2017.
 */

class AppStatsQueryThread extends Thread{

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

     private AppStatsManager aStatsManager;
    private Converter timeConverter = new Converter();

    private int counter = 0;
    private int counter2 = 0;

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    private long totalUsageTimeMillis = 0;
    private long totalUsageTimeMinutes = 0;

    private Context mContext = null;
    private String querySelection = null;

    private long top1 = 0, top2 = 0, top3 = 0, top4 = 0, top5 = 0;
    private String top1App = null, top2App = null, top3App = null, top4App = null, top5App = null;

    AppStatsQueryThread(Context context){
        mContext = context;
        aStatsManager = new AppStatsManager(context);
    }

    public void run() {

        Log.d("Käynnistetään Thread", "QueryThread: OK");

        //Asetetaan alkuarvot
        setStartValues();

        //Tarvitsee API 22
        final UsageStatsManager lUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Hakee localen nykyisestä käyttökielestä
            //String locale = Locale.getDefault().getCountry();
            //Log.d("Locale", String.valueOf(locale));

            long begin = 0;
            long end = 0;

            querySelection = getSharedPreferences("spinnerselection", "top5appsfragment");

            if(querySelection != null)
            {
                Log.d("query", querySelection);
                if(querySelection.equals(mContext.getResources().getString(R.string.daily_text)))
                {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.set(Calendar.HOUR_OF_DAY, 0);
                    cal1.set(Calendar.MINUTE, 0);
                    cal1.set(Calendar.SECOND, 1);
                    cal1.set(Calendar.MILLISECOND, 0);

                    begin = cal1.getTimeInMillis();
                    //Lisätään yksi päivä ja otetaan ylös millisekunteina
                    cal1.add(Calendar.DAY_OF_YEAR, 1);
                    //Lisätään sekunti, jotta ohjelma saa hieman pelivaraa lagien varalta
                    cal1.set(Calendar.SECOND, 0);
                    end = cal1.getTimeInMillis();
                }

                if(querySelection.equals(mContext.getResources().getString(R.string.weekly_text)))
                {
                    Calendar cal1 = Calendar.getInstance();

                    cal1.set(Calendar.HOUR_OF_DAY, 0);
                    cal1.set(Calendar.MINUTE, 0);
                    cal1.set(Calendar.SECOND, 0);
                    cal1.set(Calendar.MILLISECOND, 0);

                    Log.d("getFirstDayOfWeek", String.valueOf(Calendar.getInstance().getFirstDayOfWeek()));
                    
                    int dayOfWeek = cal1.get(Calendar.DAY_OF_WEEK);
                    Log.d("DOW", String.valueOf(dayOfWeek));

                    end = cal1.getTimeInMillis();

                    //cal1.add(Calendar.DAY_OF_WEEK, -dayOfWeek);
                    //cal1.set(Calendar.DAY_OF_WEEK, 0);

                    //cal1.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().getFirstDayOfWeek());
                    cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); //TODO: tee tähän asetus, jolla voi valita aloitusviikonpäivän

                    begin = cal1.getTimeInMillis();
                }

                if(querySelection.equals(mContext.getResources().getString(R.string.monthly_text)))
                {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.set(Calendar.HOUR_OF_DAY, 0);
                    cal1.set(Calendar.MINUTE, 0);
                    cal1.set(Calendar.SECOND, 0);
                    cal1.set(Calendar.MILLISECOND, 0);

                    int currentDOM = cal1.get(Calendar.DAY_OF_MONTH);
                    Log.d("DOM", String.valueOf(currentDOM));
                    end = cal1.getTimeInMillis();

                    cal1.set(Calendar.DAY_OF_MONTH, 1);

                    begin = cal1.getTimeInMillis();
                }

                if(querySelection.equals(mContext.getResources().getString(R.string.yearly_text)))
                {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.set(Calendar.HOUR_OF_DAY, 0);
                    cal1.set(Calendar.MINUTE, 0);
                    cal1.set(Calendar.SECOND, 0);
                    cal1.set(Calendar.MILLISECOND, 0);

                    int currentDOY = cal1.get(Calendar.DAY_OF_YEAR);
                    Log.d("DOY", String.valueOf(currentDOY));
                    end = cal1.getTimeInMillis();

                    cal1.set(Calendar.MONTH, 1);
                    cal1.set(Calendar.DAY_OF_MONTH, 1);

                    begin = cal1.getTimeInMillis();
                }

            }

            Log.d("Haetaan aikavälillä ", timeConverter.convertMillisToDate(begin) + " - " + timeConverter.convertMillisToDate(end));

            //usageStatsUsageTimeApps = lUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), System.currentTimeMillis());
            usageStatsUsageTimeApps = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }
        listUsageTimeApps = new ArrayList<>();
        listUsageTimeApps.addAll(usageStatsUsageTimeApps.values());

        Log.d("Appeja yhteensä ", String.valueOf(listUsageTimeApps.size()));

        //Looppi, joka käy läpi käyttäjän kaikki appsit
        for(UsageStats lUsageStats:listUsageTimeApps){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    //Hakee applikaation käyttöajan
                    long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                    //Hakee koska appia on viimeksi käytetty
                    long lastTimeUsed = lUsageStats.getLastTimeUsed();

                    //Tarkastaa mitä appeja on käytetty viimeksi TOP5
                    checkLastUsedApp(lastTimeUsed, lUsageStats.getPackageName());

                    //Mikäli appsia on käytetty enemmän kuin minuutti
                    if(timeConverter.convertMillisToMinutes(totalTimeInForeground) > 0 )
                    {
                        Log.d("Tänään käytetty", aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext()) + " yhteensä: " + timeConverter.convertMillisToHoursMinutesSeconds(lUsageStats.getTotalTimeInForeground()));

                        //Tarkastaa TOP5 käytetyimmät appsit
                        checkMostUsed(aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext()),lUsageStats.getPackageName(), lUsageStats.getTotalTimeInForeground());

                        //TODO:
                        //Tarkastaa yhteensä appsien käyttämän ajan
                        calculateTotalTime(lUsageStats.getTotalTimeInForeground(), aStatsManager.getAppLabel(lUsageStats.getPackageName(), mContext.getApplicationContext()));
                    }

                counter++;

                //Kun kaikki appsit on käyty läpi
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

                    if(top1App != null)
                    {
                        top1StringBuilder.append("1. ").append(aStatsManager.getAppLabel(top1App, mContext.getApplicationContext())).append("\r\n").append(str1).append("\r\n");
                    }

                    if(top2App != null)
                    {
                        top2StringBuilder.append("2. ").append(aStatsManager.getAppLabel(top2App, mContext.getApplicationContext())).append("\r\n").append(str2).append("\r\n");
                    }

                    if(top3App != null)
                    {
                        top3StringBuilder.append("3. ").append(aStatsManager.getAppLabel(top3App, mContext.getApplicationContext())).append("\r\n").append(str3).append("\r\n");
                    }

                    if(top4App != null)
                    {
                        top4StringBuilder.append("4. ").append(aStatsManager.getAppLabel(top4App, mContext.getApplicationContext())).append("\r\n").append(str4).append("\r\n");
                    }

                    if(top5App != null)
                    {
                        top5StringBuilder.append("5. ").append(aStatsManager.getAppLabel(top5App, mContext.getApplicationContext())).append("\r\n").append(str5).append("\r\n");
                    }

                    //Aiheuttaa crashaamisen mikäli ei ole käytetty vielä 5 appia
                    /*
                    top1StringBuilder.append("1. ").append(aStatsManager.getAppLabel(top1App, mContext.getApplicationContext())).append("\r\n").append(str1).append("\r\n");
                    top2StringBuilder.append("2. ").append(aStatsManager.getAppLabel(top2App, mContext.getApplicationContext())).append("\r\n").append(str2).append("\r\n");
                    top3StringBuilder.append("3. ").append(aStatsManager.getAppLabel(top3App, mContext.getApplicationContext())).append("\r\n").append(str3).append("\r\n");
                    top4StringBuilder.append("4. ").append(aStatsManager.getAppLabel(top4App, mContext.getApplicationContext())).append("\r\n").append(str4).append("\r\n");
                    top5StringBuilder.append("5. ").append(aStatsManager.getAppLabel(top5App, mContext.getApplicationContext())).append("\r\n").append(str5).append("\r\n");
                    */

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

                    setSharedPreference("sharedStats", "top1LastUsedPackage", top1App);
                    setSharedPreference("sharedStats", "top2LastUsedPackage", top2App);
                    setSharedPreference("sharedStats", "top3LastUsedPackage", top3App);
                    setSharedPreference("sharedStats", "top4LastUsedPackage", top4App);
                    setSharedPreference("sharedStats", "top5LastUsedPackage", top5App);
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

    private void setStartValues()
    {
        //usageStatsUsageTimeApps = null;
        //listUsageTimeApps = null;
        //lUsageStatsList = null;

        //Nollataan viimeksi käytetty aika
        top1 = 0;
        top2 = 0;
        top3 = 0;
        top4 = 0;
        top5 = 0;

        //Nollataan appien tiedot
        top1AppInfo = null;
        top2AppInfo = null;
        top3AppInfo = null;
        top4AppInfo = null;
        top5AppInfo = null;

        //Nollataan käyttöajat
        top1Millis = 0;
        top2Millis = 0;
        top3Millis = 0;
        top4Millis = 0;
        top5Millis = 0;

        //Nollataan pakettien nimet
        top1Package = null;
        top2Package = null;
        top3Package = null;
        top4Package = null;
        top5Package = null;

        //Nollataan appsien nimet
        top1AppName = null;
        top2AppName = null;
        top3AppName = null;
        top4AppName = null;
        top5AppName = null;

        //Nollataan kokonaisruutuaika
        totalUsageTimeMinutes = 0;
        totalUsageTimeMillis = 0;
        totalUsage = null;

        counter = 0;
        counter2 = 0;


        //Nollataan jaetut String -muuttujat
        setSharedPreference("sharedStats", "totalUsage", "empty");
        setSharedPreference("sharedStats", "top1AppInfo", "empty");
        setSharedPreference("sharedStats", "top2AppInfo", "empty");
        setSharedPreference("sharedStats", "top3AppInfo", "empty");
        setSharedPreference("sharedStats", "top4AppInfo", "empty");
        setSharedPreference("sharedStats", "top5AppInfo", "empty");

        setSharedPreference("sharedStats", "top1AppPackage", null);
        setSharedPreference("sharedStats", "top2AppPackage", null);
        setSharedPreference("sharedStats", "top3AppPackage", null);
        setSharedPreference("sharedStats", "top4AppPackage", null);
        setSharedPreference("sharedStats", "top5AppPackage", null);

        setSharedPreference("sharedStats", "top1LastUsed", "empty");
        setSharedPreference("sharedStats", "top2LastUsed", "empty");
        setSharedPreference("sharedStats", "top3LastUsed", "empty");
        setSharedPreference("sharedStats", "top4LastUsed", "empty");
        setSharedPreference("sharedStats", "top5LastUsed", "empty");

        setSharedPreference("sharedStats", "top1LastUsedPackage", null);
        setSharedPreference("sharedStats", "top2LastUsedPackage", null);
        setSharedPreference("sharedStats", "top3LastUsedPackage", null);
        setSharedPreference("sharedStats", "top4LastUsedPackage", null);
        setSharedPreference("sharedStats", "top5LastUsedPackage", null);

        //setSharedPreference("spinnerselection", "top5appsfragment", "Daily");


        Log.d("Arvojen nollaus ", "Thread: OK");
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkLastUsedApp(long lastTimeUsed, String packageName)
    {

        String appName = "Applikaation nimi";

        if(packageName != null)
        {
            appName = aStatsManager.getAppLabel(packageName, mContext);
        }

        //Varmistetaan ettei tämä appi tule listalle
        if(lastTimeUsed > top1 && !Objects.equals(packageName, mContext.getApplicationContext().getPackageName()) && !appName.contains("launcher"))
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
            top1App = packageName;
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
            top2App = packageName;
        }

        if(lastTimeUsed > top3 && lastTimeUsed < top2)
        {
            top5 = top4;
            top4 = top3;
            top3 = lastTimeUsed;

            top5App = top4App;
            top4App = top3App;
            top3App = packageName;
        }

        if(lastTimeUsed > top4 && lastTimeUsed < top3)
        {
            top5 = top4;
            top4 = lastTimeUsed;

            top5App = top4App;
            top4App = packageName;
        }

        if(lastTimeUsed > top5 && lastTimeUsed < top4)
        {
            top5 = lastTimeUsed;

            top5App = packageName;
        }
    }

    //Metodi, jolla voi hakea jaetun String -muuttujan
    private String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = mContext.getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        return pref.getString(sharedVariableTag, null);
    }

}
