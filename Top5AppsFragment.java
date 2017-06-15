package tuomomees.screentimecalculator;


import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


public class Top5AppsFragment extends Fragment {

    // Store instance variables
    private String title;
    private int page;

    //TextViewit, joihin asetetaan näkyviin top5 applikaatioiden infot
    TextView top1AppText, top2AppText, top3AppText, top4AppText, top5AppText;

    //ImageViewit, joihin asetetaan näkyviin applikaatioiden ikonit
    ImageView top1Icon, top2Icon, top3Icon, top4Icon, top5Icon;

    //Muuttujat, jotka sisältävät Applikaation nimen, top5 numeron ja käyttöajan
    public String top1AppInfo, top2AppInfo, top3AppInfo, top4AppInfo, top5AppInfo;

    //Muuttujat, jotka sisältävät applikaation paketin nimen
    String top1Package, top2Package, top3Package, top4Package, top5Package;

    //Drawablet, joihin tulee applikaatioiden ikonit
    Drawable icon1 = null;
    Drawable icon2 = null;
    Drawable icon3 = null;
    Drawable icon4 = null;
    Drawable icon5 = null;

    //Kokonaiskäyttöaika
    String totalUsage = null;
    TextView totalUsageTimeText;

    //Näkymä joka rakennetaan fragmentissa ja palautetaan lopuksi
    View view;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_top5apps, container, false);

        Log.d("Fragment", "onCreateView");

        //Alustetaan widgetit
        initialize();

        //Haetaan tarvittavat jaetut tiedot (paketin nimi ja app -info)
        getSharedPreferences();

        //Asetetaan käyttöajat näkyviin textvieweihin
        setTextViewTexts();

        //Asetetaan sovellusten kuvakkeet näkyviin imagevieweihin
        setIconDrawable();

        return view;
        //return inflater.inflate(R.layout.fragment_top5apps, container, false);
    }

    //Metodi, jolla asetetaan textvieweihin tekstit
    protected void setTextViewTexts()
    {
        //Asetetaan kokonaiskäyttöaika, mikäli se ei ole tyhjä
        if(totalUsage != null)
        {
            totalUsageTimeText.setText(totalUsage);
        }

        //Asetetaan tekstit, mikäli ne eivät ole tyhjiä
        if(!top1AppInfo.equalsIgnoreCase("") && !top2AppInfo.equalsIgnoreCase("") && !top3AppInfo.equalsIgnoreCase("") && !top4AppInfo.equalsIgnoreCase("") && !top5AppInfo.equalsIgnoreCase(""))
        {
            top1AppText.setText(top1AppInfo);
            top2AppText.setText(top2AppInfo);
            top3AppText.setText(top3AppInfo);
            top4AppText.setText(top4AppInfo);
            top5AppText.setText(top5AppInfo);
            Log.d("Tekstit asetettu", "top5");
        }
    }

    //Metodi, jolla asetetaan drawable -muotoiset iconit näkyviin imagevieweihin
    protected void setIconDrawable()
    {
        //Asetetaan TOP5 appsien iconit näkymään, mikäli arvot eivät ole NULL
        if(top1Package != null && top2Package != null && top3Package != null && top4Package != null && top5Package != null)
        {
            icon1 = getIconDrawable(top1Package);
            icon2 = getIconDrawable(top2Package);
            icon3 = getIconDrawable(top3Package);
            icon4 = getIconDrawable(top4Package);
            icon5 = getIconDrawable(top5Package);

            if(icon1 != null && icon2 != null && icon3 != null && icon4 != null && icon5 != null)
            {
                top1Icon.setImageDrawable(icon1);
                top2Icon.setImageDrawable(icon2);
                top3Icon.setImageDrawable(icon3);
                top4Icon.setImageDrawable(icon4);
                top5Icon.setImageDrawable(icon5);
                Log.d("Asetetaan ikonit", "OK");
            }
        }
    }

    //Metodi, jolla alustetaan tarvittavat widgetit
    protected void initialize()
    {
        //Alustetaan TextViewit
        top1AppText= (TextView) view.findViewById(R.id.top1App);
        top2AppText= (TextView) view.findViewById(R.id.top2App);
        top3AppText= (TextView) view.findViewById(R.id.top3App);
        top4AppText= (TextView) view.findViewById(R.id.top4App);
        top5AppText= (TextView) view.findViewById(R.id.top5App);
        totalUsageTimeText = (TextView) view.findViewById(R.id.textViewTotalTime);

        //Alustetaan ImageViewit
        top1Icon = (ImageView) view.findViewById(R.id.imageViewTop1);
        top2Icon = (ImageView) view.findViewById(R.id.imageViewTop2);
        top3Icon = (ImageView) view.findViewById(R.id.imageViewTop3);
        top4Icon = (ImageView) view.findViewById(R.id.imageViewTop4);
        top5Icon = (ImageView) view.findViewById(R.id.imageViewTop5);
    }

    //Metodi, jolla haetaan jaetut tiedot
    protected void getSharedPreferences()
    {
        //Haetaan applikaatioiden tiedot SharedPreferencen avulla String muuttujiin
        SharedPreferences pref = this.getActivity().getSharedPreferences("top5", MODE_PRIVATE);
        top1AppInfo = pref.getString("top1AppInfo", "");
        top2AppInfo = pref.getString("top2AppInfo", "");
        top3AppInfo = pref.getString("top3AppInfo", "");
        top4AppInfo = pref.getString("top4AppInfo", "");
        top5AppInfo = pref.getString("top5AppInfo", "");

        //Haetaan pakettitiedot ikoneita varten
        top1Package = pref.getString("top1AppPackage", null);
        top2Package = pref.getString("top2AppPackage", null);
        top3Package = pref.getString("top3AppPackage", null);
        top4Package = pref.getString("top4AppPackage", null);
        top5Package = pref.getString("top5AppPackage", null);

        //Haetaan kokonaiskäyttöaika
        totalUsage = pref.getString("totalUsage", null);

        //Log.d("totalUsage", totalUsage);

        /*
        Log.d("top1", top1Package);
        Log.d("top2", top2Package);
        Log.d("top3", top3Package);
        Log.d("top4", top4Package);
        Log.d("top5", top5Package);
        */

        Log.d("Fragment top1", top1AppInfo);
        Log.d("Fragment top2", top2AppInfo);
        Log.d("Fragment top3", top3AppInfo);
        Log.d("Fragment top4", top4AppInfo);
        Log.d("Fragment top5", top5AppInfo);

        Log.d("Jaetut tiedot haettu", "OK");
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

    protected void setStartValues()
    {
        //Nollataan tekstikentät
        top1AppText.setText(getResources().getString(R.string.totalusagerequest_text));
        top2AppText.setText(getResources().getString(R.string.totalusagerequest_text));
        top3AppText.setText(getResources().getString(R.string.totalusagerequest_text));
        top4AppText.setText(getResources().getString(R.string.totalusagerequest_text));
        top5AppText.setText(getResources().getString(R.string.totalusagerequest_text));
    }
}
