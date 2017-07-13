package tuomomees.screentimecalculator;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 7.6.2017.
 */

public class LastTimeUsedFragment extends Fragment {

    //Näkymä joka rakennetaan fragmentissa ja palautetaan lopuksi
    View view;

    //TextViewit, joihin asetetaan näkyviin top5Millis applikaatioiden infot
    TextView top1AppTextView, top2AppTextView, top3AppTextView, top4AppTextView, top5AppTextView;

    //ImageViewit, joihin asetetaan näkyviin applikaatioiden ikonit
    ImageView top1Icon, top2Icon, top3Icon, top4Icon, top5Icon;

    String top1AppInfo, top2AppInfo, top3AppInfo, top4AppInfo, top5AppInfo;

    //Drawablet, joihin tulee applikaatioiden ikonit
    Drawable icon1, icon2, icon3, icon4, icon5;

    //Muuttujat, jotka sisältävät applikaation paketin nimen
    String top1Package, top2Package, top3Package, top4Package, top5Package;

    //Kokonaiskäyttöaika
    String totalUsage = null;
    TextView totalUsageTimeText;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lasttimeused, container, false);

        //Alustaa widgetit
        initialize();

        //Hakee appien tiedot
        getStats();

        //Asettaa tiedot näkyvii tekstikenttiin
        setTextViewTexts();

        //Asettaa Top5 appsien ikonit näkyviin
        setIconDrawable();

        //Palauttaa näkymän, joka piirretään näytölle
        return view;
    }

    public void getStats()
    {
        totalUsage = getSharedPreferences("sharedStats", "totalUsage");
        top1AppInfo = getSharedPreferences("sharedStats", "top1LastUsed");
        top2AppInfo = getSharedPreferences("sharedStats", "top2LastUsed");
        top3AppInfo = getSharedPreferences("sharedStats", "top3LastUsed");
        top4AppInfo = getSharedPreferences("sharedStats", "top4LastUsed");
        top5AppInfo = getSharedPreferences("sharedStats", "top5LastUsed");

        top1Package = getSharedPreferences("sharedStats", "top1LastUsedPackage");
        top2Package = getSharedPreferences("sharedStats", "top2LastUsedPackage");
        top3Package = getSharedPreferences("sharedStats", "top3LastUsedPackage");
        top4Package = getSharedPreferences("sharedStats", "top4LastUsedPackage");
        top5Package = getSharedPreferences("sharedStats", "top5LastUsedPackage");
    }

    public void setTextViewTexts()
    {
        top1AppTextView.setText(top1AppInfo);
        top2AppTextView.setText(top2AppInfo);
        top3AppTextView.setText(top3AppInfo);
        top4AppTextView.setText(top4AppInfo);
        top5AppTextView.setText(top5AppInfo);

        totalUsageTimeText.setText(totalUsage);
    }

    public void setIconDrawable()
    {
        //Asetetaan ikoneiksi perusikoni, mikäli ikonien haku epäonnistuu
        top1Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top2Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top3Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top4Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));
        top5Icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher_round, null));

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

    public void initialize()
    {
        //Alustetaan TextViewit
        top1AppTextView = (TextView) view.findViewById(R.id.top1AppText);
        top2AppTextView = (TextView) view.findViewById(R.id.top2AppText);
        top3AppTextView = (TextView) view.findViewById(R.id.top3AppText);
        top4AppTextView = (TextView) view.findViewById(R.id.top4AppText);
        top5AppTextView = (TextView) view.findViewById(R.id.top5AppText);
        totalUsageTimeText = (TextView) view.findViewById(R.id.textViewTotalTimeSecond);

        //Alustetaan ImageViewit
        top1Icon = (ImageView) view.findViewById(R.id.imageViewTop1App);
        top2Icon = (ImageView) view.findViewById(R.id.imageViewTop2App);
        top3Icon = (ImageView) view.findViewById(R.id.imageViewTop3App);
        top4Icon = (ImageView) view.findViewById(R.id.imageViewTop4App);
        top5Icon = (ImageView) view.findViewById(R.id.imageViewTop5App);
    }

    // newInstance constructor for creating fragment with arguments
    public static LastTimeUsedFragment newInstance(int page, String title) {
        return new LastTimeUsedFragment();
    }

    //Metodi, jolla voi hakea jaetun muuttujan
    protected String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = this.getActivity().getSharedPreferences(sharedPrefTag, MODE_PRIVATE);

        return pref.getString(sharedVariableTag, null);
    }
}
