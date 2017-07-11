package tuomomees.screentimecalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class MainActivity extends FragmentActivity{

    //Alustetaan näytön koon muuttujat, jotta niitä voidaan käyttää globaalisti
    int height, width;

    MyPagerAdapter adapterViewPager;
    SwipeRefreshLayout swipeLayout;

    Top5AppsFragment top5AppsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top5AppsFragment = new Top5AppsFragment();

        //Alustaa liukupäivityksen käyttöön
        initializeSwipeRefresh();

        //Alustaa viewpagerin käyttöön
        initializeViewPager();

        //Tarkistaa mm. näytön koon
        checkDisplayStats();

        //Piilottaa sovelluksen nimen
        //getSupportActionBar().hide();

        //Tekee notification barista läpinäkyvän
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    //Metodi, joka alustaa käyttöön SwipeRefreshin
    protected void initializeSwipeRefresh()
    {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d("SwipeRefresh", "Päivitys aloitettu");

                        refreshFragment(top5AppsFragment);
                        swipeLayout.setRefreshing(false);

                        String toastRefreshningReady = getResources().getString(R.string.refreshing_ready);
                        Toast.makeText(MainActivity.this, toastRefreshningReady , Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    protected void initializeViewPager()
    {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 3;

        MyPagerAdapter(FragmentManager fragmentManager) {
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
                    return Top5AppsFragment.newInstance(0, "Page");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return LastTimeUsedFragment.newInstance(1, "Page");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return Top5AppsFragment.newInstance(2, "Page");
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
/*
    @Override
    protected void onDestroy()
    {
        finish();

        //Sulkee fragmentin ennen kuin MainActivity suljetaan finish() -metodilla
        detachFragment(top5AppsFragment);

        //Lopettaa MainActivityn, kun se ei ole näkyvissä


        super.onDestroy();
    }
*/

    @Override
    protected void onResume()
    {
        Log.d("mActivity", "onResume()");
        //refreshFragment(top5AppsFragment);
        super.onResume();
    }

    //Metodi, jolla voi uudelleenkäynnistää fragmentin, jolloin se luodaan uudelleen
    protected void refreshFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }

    //Metodi, jolla voi sulkea fragmentin
    protected void detachFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
    }
}
