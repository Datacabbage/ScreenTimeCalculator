package tuomomees.screentimecalculator;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity{

    ViewPager mViewPager;


    AppStatsManager aStatsManager = new AppStatsManager();
    Converter timeConverter = new Converter();
    PermissionManager pManager = new PermissionManager();


    String[] apps;

    List<String> appList = new ArrayList<>();
    int counter = 0;
    int duplicateCounter = 0;

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;

    TextView textViewFirstTimeStamp;
    TextView totalTimeText;

    //Alustetaan TextViewit, joihi tulee TOP3 appsien käyttötiedot
    TextView top1Text, top2Text, top3Text, top4Text, top5Text;

    //Alustetaan ImageViewit, johini tulee TOP5 appsien iconit
    ImageView top1Icon, top2Icon, top3Icon, top4Icon, top5Icon;

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    long totalUsageTimeMillis = 0;
    long totalUsageTimeMinutes = 0;

    //Alustetaan näytön koon muuttujat, jotta niitä voidaan käyttää globaalisti
    int height = 0;
    int width = 0;

    //Muuttujat, joihin alustetaan top5 käytetyt appsit
    long top1, top2, top3, top4, top5;

    //Muuttujat, joihin alustetaan TOP5 appsin nimi
    String top1App, top2App, top3App, top4App, top5App;

    //Muuttujat, joihin alustetaan TOP5 appsien pakettien nimet
    String top1Package, top2Package, top3Package, top4Package, top5Package;

    //Alustetaan lista, johon tulee käyttötiedot
    List<UsageStats> lUsageStatsList;

    MyPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        //Alustaa tarvittavat widgetit
        initialize();

        //Tarkistaa mm. näytön koon
        checkDisplayStats();

        //Piilottaa sovelluksen nimen
        //getSupportActionBar().hide();

        //Tekee notification barista läpinäkyvän
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        fillStats();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return Top5AppsFragment.newInstance(0, "Top5 Apps");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return LastTimeUsedFragment.newInstance(1, "Last Used");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return Top5AppsFragment.newInstance(2, "Top5 Apps(3)");
                default:
                    return null;
            }
        }

        //Metodi, jossa voi muuttaa sivun yläpalkin esittelytekstin (position palauttaa  sivunumeron 0-2)
        @Override
        public CharSequence getPageTitle(int position) {





            if(position == 0)
            {
                return getResources().getString(R.string.top5appspage_title);
            }

            if(position == 1)
            {
                return getResources().getString(R.string.lastusedpage_title);
            }

            if(position == 2)
            {
                return getResources().getString(R.string.top5appspage_title);
            }


            return "Page" + position;
        }

    }

    //Metodi, jolla voidaan välittää tietoa fragmenttiin
    private void passInfoToTop5Fragment(String info1, String info2, String info3, String info4, String info5)
    {
        //Lähetetään tiedot Fragmenttiin SharedPreferencen avulla
        SharedPreferences pref = getApplicationContext().getSharedPreferences("top5", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("top1AppInfo", info1);
        editor.putString("top2AppInfo", info2);
        editor.putString("top3AppInfo", info3);
        editor.putString("top4AppInfo", info4);
        editor.putString("top5AppInfo", info5);
        //Lähetetään myös packagetiedot, jotta saadaan ikonit toimimaan
        editor.putString("top1AppPackage", top1Package);
        editor.putString("top2AppPackage", top2Package);
        editor.putString("top3AppPackage", top3Package);
        editor.putString("top4AppPackage", top4Package);
        editor.putString("top5AppPackage", top5Package);
        editor.apply();
        editor.commit();

/*
        Top5AppsFragment top5AppsObj = new Top5AppsFragment();
        Bundle data = new Bundle();
        data.putString("top1AppInfo", info1);

        Log.d("Passing to Fragment", info1);
        top5AppsObj.setTop1Info(info1);
        top5AppsObj.setArguments(data);
        */
    }

    private void initialize() {
        //Alustetaan TextViewit
        textViewFirstTimeStamp = (TextView) findViewById(R.id.textViewFirstTimeStamp);
        totalTimeText = (TextView) findViewById(R.id.textViewTotalTime);
        top1Text = (TextView) findViewById(R.id.top1App);
        top2Text = (TextView) findViewById(R.id.top2App);
        top3Text = (TextView) findViewById(R.id.top3App);
        top4Text = (TextView) findViewById(R.id.top4App);
        top5Text = (TextView) findViewById(R.id.top5App);

        //Alustetaan ImageViewit käyttöön
        top1Icon = (ImageView) findViewById(R.id.imageViewTop1);
        top2Icon = (ImageView) findViewById(R.id.imageViewTop2);
        top3Icon = (ImageView) findViewById(R.id.imageViewTop3);
        top4Icon = (ImageView) findViewById(R.id.imageViewTop4);
        top5Icon = (ImageView) findViewById(R.id.imageViewTop5);
    }

    private void checkDisplayStats()
    {
        //Haetaan näytön koko muuttujiin
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Näytön koon tsekkaaminen Logissa
        Log.d("Näytön koko on", height + "x" + width);
    }

    //Mikäli sovelluksella on tarvittavat oikeudet, hakee statistiikan. Muussa tapauksessa pyytää tarvittavia oikeuksia.
    private void fillStats() {

        if (hasPermission()){
            //Alustetaan aloitusarvot, jotta arvot eivät kertaudu
            setStartValues();
            getStats();
        }else{
            requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "resultCode " + resultCode);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                fillStats();
                break;
        }
    }

    private void requestPermission() {
        Toast.makeText(this, "Pyydetään tarvittavia oikeuksia.", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }


    private void getStats() {

        //Alla olevilla laineilla haetaan aikatiedot
        final int currentDate = Calendar.getInstance().get(Calendar.DATE);
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        final int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        Log.d("Tunnit", String.valueOf(currentHour));
        Log.d("PVM", String.valueOf(currentDate));
        Log.d("Vuosi", String.valueOf(currentYear));
        Log.d("DOY", String.valueOf(currentDayOfYear));

        //Appsien kokonaiskäyttöaika
        long totalUsage = 0;

        UsageStatsManager lUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        //Toimii ainoastaan Androidin versiolla 5.0 tai uudempi
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


            long currentTime = System.currentTimeMillis();

            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            long startTime = calendar.getTimeInMillis();
            //endTime.add(Calendar.DAY_OF_MONTH, +1);

            //Poimii tiedot aina 24H sisällä, eli 24H liukuu jatkuvasti ns. mukana
            //lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()- TimeUnit.DAYS.toMillis(1),System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(1));

            lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - TimeUnit.DAYS.toMillis(1), currentTime);

            Log.d("How many apps", String.valueOf(lUsageStatsList.size()));
            //lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end); //86 399 000 millisekuntia on 23 tuntia ja 59 minuuttia ja 59 sekuntia
        }

        for (UsageStats lUsageStats:lUsageStatsList){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                checkDuplicateApps(aStatsManager.getAppLabel(lUsageStats.getPackageName(), getApplicationContext()), lUsageStats.getTotalTimeInForeground() );

                //Mikäli appsia on käytetty enemmän kuin minuutti
                if(((lUsageStats.getTotalTimeInForeground()/ 1000)/60) > 0 )
                {
                    //Tarkastaa TOP5 käytetyimmät appsit
                    checkMostUsed(aStatsManager.getAppLabel(lUsageStats.getPackageName(), getApplicationContext()),lUsageStats.getPackageName(),lUsageStats.getTotalTimeInForeground());

                    //Tarkastaa yhteensä appsien käyttämän ajan
                    calculateTotalTime(lUsageStats.getTotalTimeInForeground(), aStatsManager.getAppLabel(lUsageStats.getPackageName(), getApplicationContext()));

                    //Log.d("kaikki apsit", getAppLabel(lUsageStats.getPackageName()));

                    //lStringBuilder.append(lUsageStats.getLastTimeUsed());

                    totalUsage = totalUsage + lUsageStats.getTotalTimeInForeground();

                    String lastTimeUsed = aStatsManager.getAppLabel(lUsageStats.getPackageName()+ "  ", getApplicationContext()) + timeConverter.convertMillisToMinutes(lUsageStats.getLastTimeUsed());
                    Log.d("LastTimeUsed", lastTimeUsed);
                }
            }
        }


        long totalUsageSec = totalUsage / 1000;
        long totalUsageMin = totalUsageSec / 60;
        Log.d("Total(MILLIS)", String.valueOf(totalUsage));
        Log.d("Total(MIN)", String.valueOf(totalUsageMin));
    }

    //Tarkistaa onko sama applikaatio esiintynyt useamman kerran
    protected String checkDuplicateApps(String appName, long usageTime)
    {

/*
        final ArrayList appArray = new ArrayList(lUsageStatsList.size());
        appArray.add(counter, appName);
*/

        //Log.d("list", String.valueOf(appArray));

        //Taulukko, johon mahtuu kaikki applikaatioiden nimet
        apps = new String[lUsageStatsList.size()];
        apps[counter] = appName;
        Log.d(appName + " alustettu paikkaan", String.valueOf(counter));

        int doubleCounter = counter + 1;

        /*
        //Silmukka, joka on yhtä pitkä kuin applikaatioita on listassa 0-128
        for(int e = 0; e < lUsageStatsList.size(); e++)
        {

                    Log.d("App found in array", appName + " @place: " + e);
        }
*/
        counter++;
        return appName;
    }

    //Metodi, jolla lasketaan yhteen kaikki käyttöajat
    public long calculateTotalTime(long usageTime, String appName)
    {
        //Log.d("Kokonaisaika on", String.valueOf(totalUsageTimeMillis));
        totalUsageTimeMillis = totalUsageTimeMillis + usageTime;

        //Log.d("Kokonaisaikaan lisätty", appName + " ajalla: " + usageTime);

        StringBuilder totalTimeStringBuilder = new StringBuilder();

        totalUsageTimeMinutes = (totalUsageTimeMillis / 1000) / 60;
        totalTimeStringBuilder.append("Ruutuaika tänään: " + String.valueOf(totalUsageTimeMinutes) + " min");

        totalTimeText.setText(totalTimeStringBuilder.toString());

        return totalUsageTimeMinutes;
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

        long top1Min = timeConverter.convertMillisToMinutes(top1);
        long top2Min = timeConverter.convertMillisToMinutes(top2);
        long top3Min = timeConverter.convertMillisToMinutes(top3);
        long top4Min = timeConverter.convertMillisToMinutes(top4);
        long top5Min = timeConverter.convertMillisToMinutes(top5);

        top1StringBuilder.append("1. " + top1App + "\r\n" + top1Min + " min" + "\r\n");
        top2StringBuilder.append("2. " + top2App + "\r\n" + top2Min + " min" + "\r\n");
        top3StringBuilder.append("3. " + top3App + "\r\n" + top3Min + " min" + "\r\n");
        top4StringBuilder.append("4. " + top4App + "\r\n" + top4Min + " min" + "\r\n");
        top5StringBuilder.append("5. " + top5App + "\r\n" + top5Min + " min" + "\r\n");

        String top1AppText, top2AppText, top3AppText, top4AppText, top5AppText;

        top1AppText = top1StringBuilder.toString();
        top2AppText = top2StringBuilder.toString();
        top3AppText = top3StringBuilder.toString();
        top4AppText = top4StringBuilder.toString();
        top5AppText = top5StringBuilder.toString();

        //Tällä metodilla voidaan toimittaa muuttujia kyseiseen Fragmenttiin
        passInfoToTop5Fragment(top1AppText, top2AppText, top3AppText, top4AppText, top5AppText);

        return new long[] {top1, top2, top3, top4, top5};
    }

    protected void setStartValues()
    {
        //Nollataan käyttöajat
        top1 = 0;
        top2 = 0;
        top3 = 0;
        top4 = 0;
        top5 = 0;

        //Nollataan pakettien nimet
        top1Package = null;
        top2Package = null;
        top3Package = null;
        top4Package = null;
        top5Package = null;

        //Nollataan appsien nimet
        top1App = null;
        top2App = null;
        top3App = null;
        top4App = null;
        top5App = null;

        //Nollataan tekstikentät
        top1Text.setText("haetaan tietoja...");
        top2Text.setText("haetaan tietoja...");
        top3Text.setText("haetaan tietoja...");
        top4Text.setText("haetaan tietoja...");
        top5Text.setText("haetaan tietoja...");

        //Nollataan kokonaisruutuaika tekstikenttä
        totalTimeText.setText("haetaan tietoja...");

        //Nollataan kokonaisruutuaika
        totalUsageTimeMinutes = 0;
        totalUsageTimeMillis = 0;

        //Nollataan laskuri
        counter = 0;

        Log.d("Arvojen nollaus ", "OK");
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

    @Override
    protected void onDestroy()
    {
        //Lopettaa MainActivityn, kun se ei ole näkyvissä
        finish();
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        Log.d("mActivity", "onResume()");
        //Haetaan arvot uudelleen
        fillStats();
        super.onResume();
    }
}
