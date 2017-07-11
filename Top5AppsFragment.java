package tuomomees.screentimecalculator;


import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Top5AppsFragment extends Fragment {

    //TESTI
    Map<String, UsageStats> usageStats;
    List<UsageStats> stats;

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;

    // Store instance variables
    private String title;
    private int page;

    //Alustetaan lista, johon tulee käyttötiedot
    List<UsageStats> lUsageStatsList;

    //TextViewit, joihin asetetaan näkyviin top5Millis applikaatioiden infot
    TextView top1AppTextView, top2AppTextView, top3AppTextView, top4AppTextView, top5AppTextView;

    //ImageViewit, joihin asetetaan näkyviin applikaatioiden ikonit
    ImageView top1Icon, top2Icon, top3Icon, top4Icon, top5Icon;

    //Muuttujat, jotka sisältävät Applikaation nimen, top5Millis numeron ja käyttöajan
    public String top1AppInfo, top2AppInfo, top3AppInfo, top4AppInfo, top5AppInfo;

    //Muuttujat, jotka sisältävät applikaation paketin nimen
    String top1Package, top2Package, top3Package, top4Package, top5Package;

    //Muuttujat, joihin alustetaan TOP5 appsin nimi
    String top1AppName, top2AppName, top3AppName, top4AppName, top5AppName;

    //Muuttujat, joihin alustetaan top5 appien käyttöaika millisekunneissa
    long top1Millis, top2Millis, top3Millis, top4Millis, top5Millis;

    //Drawablet, joihin tulee applikaatioiden ikonit
    Drawable icon1, icon2, icon3, icon4, icon5;

    //Kokonaiskäyttöaika
    String totalUsage = null;
    TextView totalUsageTimeText;

    //Näkymä joka rakennetaan fragmentissa ja palautetaan lopuksi
    View view;

    //TESTI
    AppStatsManager aStatsManager = new AppStatsManager();
    Converter timeConverter = new Converter();

    Data[] appData;

    int idCounter = 0;
    int id = 0;

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    long totalUsageTimeMillis = 0;
    long totalUsageTimeMinutes = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_top5apps, container, false);

        Log.d("Fragment", "onCreateView");

        //Alustetaan widgetit
        initialize();

        //Nollataan arvot
        setStartValues();

        //Haetaan tiedot
        fillStats();

        //Asettaa aikatiedot näkyviin textvieweihin
        setTextViewTexts();

        //Asettaa Top5 appsien ikonit näkyviin
        setIconDrawable();

        //Palauttaa näkymän, joka piirretään näytölle
        return view;
    }

    //Metodi, jolla voi lisätä jaetun muuttujan
    protected void setSharedPreference(String sharedPrefTag, String sharedVariableTag, String sharedVariable)
    {
        //Lähetetään tiedot Fragmenttiin SharedPreferencen avulla
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(sharedVariableTag, sharedVariable);
        editor.apply();
        Log.d("Shared variable", sharedVariable + " with tag " + sharedVariableTag);
    }

    /*
    //Metodi, jolla asetetaan textvieweihin tekstit
    protected void setTextViewTexts()
    {
        //Nollataan tekstikentät, mikäli tekstien haku epäonnistuu
        top1AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top2AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top3AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top4AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top5AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));

        //Asetetaan kokonaiskäyttöaika, mikäli se ei ole tyhjä
        if(totalUsage != null)
        {totalUsageTimeText.setText(totalUsage);}

        if(top1Millis != 0)
        {top1AppTextView.setText(top1AppInfo);}

        if(top2Millis != 0)
        {top2AppTextView.setText(top2AppInfo);}

        if(top3Millis != 0)
        {top3AppTextView.setText(top3AppInfo);}

        if(top4Millis != 0)
        {top4AppTextView.setText(top4AppInfo);}

        if(top5Millis != 0)
        {top5AppTextView.setText(top5AppInfo);}

        Log.d("Top5 tekstit asetettu", "OK");
    }
    */

    protected void setTextViewTexts()
    {
        //Nollataan tekstikentät, mikäli tekstien haku epäonnistuu
        top1AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top2AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top3AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top4AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top5AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));

        totalUsageTimeText.setText(totalUsage);

        top1AppTextView.setText(top1AppInfo);
        top2AppTextView.setText(top2AppInfo);
        top3AppTextView.setText(top3AppInfo);
        top4AppTextView.setText(top4AppInfo);
        top5AppTextView.setText(top5AppInfo);

        Log.d("Top5 tekstit asetettu", "OK");
    }

    //Metodi, jolla asetetaan drawable -muotoiset iconit näkyviin imagevieweihin
    protected void setIconDrawable()
    {
        //Asetetaan ikoneiksi perusikoni, mikäli ikonien haku epäonnistuu
        top1Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top2Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top3Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top4Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top5Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));

        //Asetetaan TOP5 appsien iconit näkymään, mikäli arvot eivät ole NULL
        if(top1Package != null)
        {
            icon1 = getIconDrawable(top1Package);

            if(icon1 != null)
            {top1Icon.setImageDrawable(icon1);}
        }

        if(top2Package != null)
        {
            icon2 = getIconDrawable(top2Package);

            if(icon2 != null)
            {top2Icon.setImageDrawable(icon2);}
        }

        if(top3Package != null)
        {
            icon3 = getIconDrawable(top3Package);

            if(icon3 != null)
            {top3Icon.setImageDrawable(icon3);}
        }

        if(top4Package != null)
        {
            icon4 = getIconDrawable(top4Package);

            if(icon4 != null)
            {top4Icon.setImageDrawable(icon4);}
        }

        if(top5Package != null)
        {
            icon5 = getIconDrawable(top5Package);

            if(icon5 != null)
            {top5Icon.setImageDrawable(icon5);}
        }

        Log.d("Asetetaan ikonit", "OK");
    }

    //Metodi, jolla voi hakea jaetun String -muuttujan
    protected String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = this.getActivity().getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        return pref.getString(sharedVariableTag, null);
    }


    //Metodi, jolla alustetaan tarvittavat widgetit
    protected void initialize()
    {
        //Alustetaan TextViewit
        top1AppTextView = (TextView) view.findViewById(R.id.top1App);
        top2AppTextView = (TextView) view.findViewById(R.id.top2App);
        top3AppTextView = (TextView) view.findViewById(R.id.top3App);
        top4AppTextView = (TextView) view.findViewById(R.id.top4App);
        top5AppTextView = (TextView) view.findViewById(R.id.top5App);
        totalUsageTimeText = (TextView) view.findViewById(R.id.textViewTotalTime);

        //Alustetaan ImageViewit
        top1Icon = (ImageView) view.findViewById(R.id.imageViewTop1);
        top2Icon = (ImageView) view.findViewById(R.id.imageViewTop2);
        top3Icon = (ImageView) view.findViewById(R.id.imageViewTop3);
        top4Icon = (ImageView) view.findViewById(R.id.imageViewTop4);
        top5Icon = (ImageView) view.findViewById(R.id.imageViewTop5);
    }

    //Metodi, jolla voi hakea tarvittavien applikaatioiden app-ikonit paketin nimen avulla
    protected Drawable getIconDrawable(String packageName) {
        Drawable icon = null;
        try {
            icon = getActivity().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return icon;
    }

    // newInstance constructor for creating fragment with arguments
    public static Top5AppsFragment newInstance(int page, String title) {
        Top5AppsFragment top5AppsObj = new Top5AppsFragment();
        Bundle args = new Bundle();

        args.putInt("someInt", page);
        args.putString("someTitle", title);
        top5AppsObj.setArguments(args);
        return top5AppsObj;
    }

    //Mikäli sovelluksella on tarvittavat oikeudet, hakee statistiikan. Muussa tapauksessa pyytää tarvittavia oikeuksia.
    private void fillStats() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasPermission()){
                //Alustetaan aloitusarvot, jotta arvot eivät kertaudu
                setStartValues();
                getStats();
            }else{
                requestPermission();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "resultCode " + resultCode);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                fillStats();
                break;
        }
    }

    private void requestPermission() {

        String toastPermissionRequest = getResources().getString(R.string.permission_request);
        Toast.makeText(getActivity().getApplicationContext(), toastPermissionRequest, Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermission() {
        AppOpsManager appOps = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appOps = (AppOpsManager)
                    getActivity().getSystemService(Context.APP_OPS_SERVICE);
        }
        int mode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            assert appOps != null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        Process.myUid(), getActivity().getPackageName());
            }
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void getStats() {

        /*
        //Alla olevilla laineilla haetaan aikatiedot
        final int currentDate = Calendar.getInstance().get(Calendar.DATE);
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        final int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        Log.d("Tunnit", String.valueOf(currentHour));
        Log.d("PVM", String.valueOf(currentDate));
        Log.d("Vuosi", String.valueOf(currentYear));
        Log.d("DOY", String.valueOf(currentDayOfYear));

        */

        final UsageStatsManager lUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);

            //lUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);


        /*
        //Toimii ainoastaan Androidin versiolla 5.0 tai uudempi
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Calendar cal = Calendar.getInstance();
            //Nykyinen aika - yksi päivä
            //cal.add(Calendar.DAY_OF_WEEK, - 1);

            //Alkuperäinen query, jolla tulee ongelmallisesti tuplatapauksia
            //lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(), System.currentTimeMillis());

            //usageStats = lUsageStatsManager.queryAndAggregateUsageStats(cal.getTimeInMillis(), System.currentTimeMillis());

            //Hakee tiedot 24H sisällä eli 86400000 millis
            usageStats = lUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), System.currentTimeMillis());
            stats = new ArrayList<>();
            stats.addAll(usageStats.values());

            Log.d("How many apps ", String.valueOf(stats.size()));
        }
*/


        Context context = getActivity().getApplicationContext();
        Thread appStatsQueryThread = new AppStatsQueryThread(context);
        appStatsQueryThread.run();

        /*
        //Testi, jossa luodaan uusi säie eli thread
        new Thread(new Runnable() {
            public void run() {

                //Toimii ainoastaan Androidin versiolla 5.0 tai uudempi
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    Calendar cal = Calendar.getInstance();
                    //Nykyinen aika - yksi päivä
                    //cal.add(Calendar.DAY_OF_WEEK, - 1);

                    //Alkuperäinen query, jolla tulee ongelmallisesti tuplatapauksia
                    //lUsageStatsList = lUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(), System.currentTimeMillis());

                    //usageStats = lUsageStatsManager.queryAndAggregateUsageStats(cal.getTimeInMillis(), System.currentTimeMillis());

                    //Hakee tiedot 24H sisällä eli 86400000 millis
                    usageStats = lUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1), System.currentTimeMillis());
                    stats = new ArrayList<>();
                    stats.addAll(usageStats.values());

                    Log.d("How many apps ", String.valueOf(stats.size()));
                }

                //Looppi, joka käy läpi käyttäjän kaikki appsit
                for(UsageStats lUsageStats:stats){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        //Hakee applikaation nimen
                        String aLabelName = aStatsManager.getAppLabel(lUsageStats.getPackageName(), getActivity().getApplicationContext());
                        //Hakee applikaation paketin nimen
                        String aPackageName = lUsageStats.getPackageName();
                        //Hakee applikaation käyttöajan
                        long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                        //Alustaa taulukon, johon tulee käyttäjän kaikki applikaatiot (nimi, id, käyttöaika)
                        initializeAppArray(aLabelName, totalTimeInForeground);

                        //Mikäli appsia on käytetty enemmän kuin minuutti
                        if(timeConverter.convertMillisToMinutes(totalTimeInForeground) > 0 )
                        {
                            //Tarkastaa TOP5 käytetyimmät appsit
                            checkMostUsed(aStatsManager.getAppLabel(lUsageStats.getPackageName(), getActivity().getApplicationContext()),lUsageStats.getPackageName(), lUsageStats.getTotalTimeInForeground());

                            //Tarkastaa yhteensä appsien käyttämän ajan
                            calculateTotalTime(lUsageStats.getTotalTimeInForeground(), aStatsManager.getAppLabel(lUsageStats.getPackageName(), getActivity().getApplicationContext()));

                            setSharedPreference("sharedStats", "totalUsage", totalUsage);

                        }
                    }
                }
            }
        }).start();

*/


        totalUsage = getSharedPreferences("sharedStats", "totalUsage");
        top1AppInfo = getSharedPreferences("sharedStats", "top1AppInfo");
        top2AppInfo = getSharedPreferences("sharedStats", "top2AppInfo");
        top3AppInfo = getSharedPreferences("sharedStats", "top3AppInfo");
        top4AppInfo = getSharedPreferences("sharedStats", "top4AppInfo");
        top5AppInfo = getSharedPreferences("sharedStats", "top5AppInfo");

        top1Package = getSharedPreferences("sharedStats", "top1AppPackage");
        top2Package = getSharedPreferences("sharedStats", "top2AppPackage");
        top3Package = getSharedPreferences("sharedStats", "top3AppPackage");
        top4Package = getSharedPreferences("sharedStats", "top4AppPackage");
        top5Package = getSharedPreferences("sharedStats", "top5AppPackage");

        Log.d("getting sharedPref", "totalUsage");
        if(totalUsage != null)
        {
            Log.d("pref", totalUsage);
        }




        /*
        for(UsageStats lUsageStats:stats){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //Hakee applikaation nimen
                String aLabelName = aStatsManager.getAppLabel(lUsageStats.getPackageName(), getActivity().getApplicationContext());
                //Hakee applikaation paketin nimen
                String aPackageName = lUsageStats.getPackageName();
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                //Alustaa taulukon, johon tulee käyttäjän kaikki applikaatiot (nimi, id, käyttöaika)
                initializeAppArray(aLabelName, totalTimeInForeground);

                //Mikäli appsia on käytetty enemmän kuin minuutti
                if(timeConverter.convertMillisToMinutes(totalTimeInForeground) > 0 )
                {
                    //Tarkastaa TOP5 käytetyimmät appsit
                    checkMostUsed(aStatsManager.getAppLabel(lUsageStats.getPackageName(), getActivity().getApplicationContext()),lUsageStats.getPackageName(), lUsageStats.getTotalTimeInForeground());

                    //Tarkastaa yhteensä appsien käyttämän ajan
                    calculateTotalTime(lUsageStats.getTotalTimeInForeground(), aStatsManager.getAppLabel(lUsageStats.getPackageName(), getActivity().getApplicationContext()));

                    //Asetetaan tekstit ja ikonit vasta kun kaikki tiedot on haettu
                    if(stats.size() - 1  <= id)
                    {
                        //Asettaa aikatiedot näkyviin textvieweihin
                        setTextViewTexts();

                        //Asettaa Top5 appsien ikonit näkyviin
                        setIconDrawable();
                    }

                }
            }
        }
        */



    }

    protected void initializeAppArray(String appName, long usageTime)
    {
        //Varmistaa, että taulukko luodaan vain kerran
        //Taulukko, johon mahtuu kaikki applikaatioiden nimet
        if(id == 0)
        { appData = new Data[stats.size()]; }

        Log.d("Alustetaan ", appName + " paikkaan " + id);

        appData[id] = new Data(appName, id, usageTime);
        id++;
    }

    //Metodi, jolla lasketaan yhteen kaikki käyttöajat
    public String calculateTotalTime(long usageTime, String appName)
    {
        StringBuilder totalUsageStringBuilder = new StringBuilder();

        totalUsageTimeMillis = totalUsageTimeMillis + usageTime;
        totalUsageTimeMinutes = timeConverter.convertMillisToMinutes(totalUsageTimeMillis);

        totalUsageStringBuilder.append(getResources().getString(R.string.totalusage_text)).append("\r\n").append(timeConverter.convertMillisToHoursMinutesSeconds(totalUsageTimeMillis));
        totalUsage = totalUsageStringBuilder.toString();

        return totalUsage;
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
        if(checkIfAppAlreadyExist(appName).equals("null"))
        {
            appName = "null";
            packageName = "null";
            usageTime = 0;
        }

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

    protected void setStartValues()
    {
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

        //Nollataan tekstikentät
        top1AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top2AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top3AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top4AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top5AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));

        //Nollataan imageviewit
        top1Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top2Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top3Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top4Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        top5Icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));

        //Nollataan kokonaisruutuaika tekstikenttä
        totalUsageTimeText.setText(getResources().getString(R.string.usagerequestfailed_text));

        //Nollataan kokonaisruutuaika
        totalUsageTimeMinutes = 0;
        totalUsageTimeMillis = 0;

        //Nollataan id-laskuri
        id = 0;

        Log.d("Arvojen nollaus ", "OK");
    }

    //Metodi, joka tarkastaa onko kyseinen appi jo TOP5 listalla
    protected String checkIfAppAlreadyExist(String appName)
    {

        /*
        if(appName.equals(top1AppName) || appName.equals(top2AppName) || appName.equals(top3AppName) || appName.equals(top4AppName) || appName.equals(top5AppName))
        {
            Log.d("Toistamiseen", appName);
            appName = "null";
        }
*/
        return appName;
    }

    //Tarkistaa onko sama applikaatio esiintynyt useamman kerran, mikäli esiintyy, metodi laskee keskimääräisen käyttöajan
    public long checkDuplicateApps(String appName)
    {
        //Log.d("ID koko", String.valueOf(id));

        long originalAppUsageTime;
        long duplicateAppUsageTime;

        int duplicateAppId;
        int originalAppId;

        //Varmistaa, että taulukossa on vähintään 2 arvoa, jotta niitä voidaan verrata
        if(id > 0)
        {
            //Silmukka, joka on yhtä pitkä kuin alustettujen applikaatioiden määrä (ID:stä päätellen)
            //for(int e = id; e >= 0; e--) {
            for (int e = id - 1; e >= 0; e--) {

                if(appName.equals(appData[e].appName))
                {
                    idCounter++;

                    Log.d("App found in array", appData[e].appName + " @place: " + e);

                    //Otetaan talteen alkuperäinen applikaation ID
                    if(idCounter == 1 && appName.equals(appData[e].appName))
                    {
                        originalAppId = appData[e].appId;
                        Log.d("OriginalAppID", String.valueOf(originalAppId));
                        originalAppUsageTime = appData[originalAppId].appUsageTime;

                        return originalAppUsageTime;
                    }

                    if(idCounter == 2 && appName.equals(appData[e].appName))
                    {
                        //Mikäli appi esiintyy useamman kerran taulukossa, otetaan talteen toinen app ID
                        duplicateAppId = appData[e].appId;

                        //Haetaan tupla-appin käyttämä aika
                        duplicateAppUsageTime = appData[duplicateAppId].appUsageTime;

                        Log.d("duplicateUsagetime2", String.valueOf(duplicateAppUsageTime));

                        Log.d("DuplicateAppID", String.valueOf(duplicateAppId));

                        Log.d("Same app again", appName + " times: " +  idCounter);

                        //Mikäli appi esiintyy arrayssa useammin kuin kerran, palautetaan ainoastaan tuplapainoksen käyttöaika (alkup. on jo palautettu tässä vaiheessa)
                        return duplicateAppUsageTime;
                    }

                    if(idCounter == 3 && appName.equals(appData[e].appName))
                    {
                        Log.d("Same app again", appName + " times: " +  idCounter);
                    }

                    //Log.d("App found in array", appName + " @place: " + e);
                    //Log.d("App ID", String.valueOf(appData[e].appId));
                }
            }
        }

        //Nollataan appin esiintymiskertalaskuri, kun kaikki appit on vertailtu taulukosta
        idCounter = 0;

        return 0;
    }

    //Staattinen luokka, johon voi alustaa kaikki appsit ja niiden käyttöaika, sekä identifoida ne ID:n avulla
    private static class Data
    {
        private String appName;
        private int appId;
        private long appUsageTime;

        Data(String n, int id, long uT)
        {
            appName = n;
            appId = id;
            appUsageTime = uT;
        }

        public int getAppId()
        {
            return appId;
        }

        public String getAppName()
        {
            return appName;
        }

        public long getAppUsageTime() { return appUsageTime; }

        public void setAppUsageTime(long usageTime){ appUsageTime = usageTime; }
    }
}
