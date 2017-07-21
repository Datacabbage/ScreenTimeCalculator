package tuomomees.screentimecalculator;


import android.app.AppOpsManager;
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
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class Top5AppsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;

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

    //Muuttujat, joihin tulee kokonaiskäyttöaika
    long totalUsageTimeMillis = 0;
    long totalUsageTimeMinutes = 0;

    AppStatsManager appStatsManager;

    Thread appStatsQueryThread;

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

    protected void setTextViewTexts()
    {

        //Nollataan tekstikentät, mikäli tekstien haku epäonnistuu
        top1AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top2AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top3AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top4AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));
        top5AppTextView.setText(getResources().getString(R.string.usagerequestfailed_text));

        if(totalUsage != null)
        { totalUsageTimeText.setText(totalUsage); }

        if(top1AppInfo != null)
        { top1AppTextView.setText(top1AppInfo); }

        if(top2AppInfo != null)
        { top2AppTextView.setText(top2AppInfo); }

        if(top3AppInfo != null)
        { top3AppTextView.setText(top3AppInfo); }

        if(top4AppInfo != null)
        { top4AppTextView.setText(top4AppInfo); }

        if(top5AppInfo != null)
        { top5AppTextView.setText(top5AppInfo); }

        Log.d("Top5 tekstit asetettu", "Most used: OK");
    }

    //Metodi, jolla asetetaan drawable -muotoiset iconit näkyviin imagevieweihin
    protected void setIconDrawable()
    {
        //Asetetaan ikoneiksi perusikoni, mikäli ikonien haku epäonnistuu
        top1Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top2Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top3Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top4Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top5Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));

        Context context = getContext().getApplicationContext();

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

        Log.d("Asetetaan ikonit", "Most used: OK");
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

        //Alustetaan Spinner -pudotavalikko
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerQuerySelect);
        spinner.setOnItemSelectedListener(this);

        //Luodaan adapteri spinnerille
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.queries_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
    public static Top5AppsFragment newInstance(int position) { return new Top5AppsFragment(); }

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
        Context context = getActivity().getApplicationContext();
        Thread appStatsQueryThread = new AppStatsQueryThread(context);
        appStatsQueryThread.run();
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
        top1Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top2Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top3Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top4Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top5Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));

        //Nollataan kokonaisruutuaika tekstikenttä
        totalUsageTimeText.setText(getResources().getString(R.string.usagerequestfailed_text));

        //Nollataan kokonaisruutuaika
        totalUsageTimeMinutes = 0;
        totalUsageTimeMillis = 0;

        Log.d("Arvojen nollaus ", "Most Used Fragment: OK");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        setSharedPreference("spinnerselection", "top5appsfragment", item);

        Context context = getActivity().getApplicationContext(); //TODO: mikäli testi ei onnistu, siirrä threadin aloitus takaisin mainactivityyn
        //Thread appStatsQueryThread = new AppStatsQueryThread(context, item);
        appStatsQueryThread = new AppStatsQueryThread(context, item);
        appStatsQueryThread.run();

        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show(); //TODO: tee tähän tarvittava stringi

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
}
