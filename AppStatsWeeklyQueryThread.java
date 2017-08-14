package tuomomees.screentimecalculator;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 10.8.2017.
 */

public class AppStatsWeeklyQueryThread extends Thread{

    private Context mContext = null;
    private AppStatsManager aStatsManager;
    private UsageStatsManager lUsageStatsManager;

    private Converter timeConverter = new Converter();

    private Map<String, UsageStats> usageStatsForOneWeekday;

    private long totalUsageMonday;
    private long totalUsageTuesday;
    private long totalUsageWednesday;
    private long totalUsageThursday;
    private long totalUsageFriday;
    private long totalUsageSaturday;
    private long totalUsageSunday;

    private long totalUsageTime;

    AppStatsWeeklyQueryThread(Context context){
        mContext = context;
        aStatsManager = new AppStatsManager(context);
    }

    public void run() {

        setStartValues();

        //Tarvitsee API 22
        lUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        //Hankitaan viikonpäivien kokonaiskäyttöajat
        getMonday();
        getTuesday();
        getWednesday();
        getThursday();
        getFriday();
        getSaturday();
        getSunday();

        shareStats();
    }

    private void setStartValues()
    {
        totalUsageTime = 0;

        totalUsageTuesday = 0;
        totalUsageWednesday = 0;
        totalUsageThursday = 0;
        totalUsageFriday = 0;
        totalUsageSaturday = 0;
        totalUsageSunday = 0;
    }

    //Metodi, jolla lasketaan yhteen kaikki käyttöajat
    private long calculateTotalTime(long usageTime) {
        totalUsageTime = totalUsageTime + usageTime;

        return totalUsageTime;
    }

    private void shareStats()
    {
        totalUsageMonday = timeConverter.convertMillisToMinutes(totalUsageMonday);
        totalUsageTuesday = timeConverter.convertMillisToMinutes(totalUsageTuesday);
        totalUsageWednesday = timeConverter.convertMillisToMinutes(totalUsageWednesday);
        totalUsageThursday = timeConverter.convertMillisToMinutes(totalUsageThursday);
        totalUsageFriday = timeConverter.convertMillisToMinutes(totalUsageFriday);
        totalUsageSaturday = timeConverter.convertMillisToMinutes(totalUsageSaturday);
        totalUsageSunday = timeConverter.convertMillisToMinutes(totalUsageSunday);

        setSharedPreference("WeeklyQuery", "totalUsageMonday", String.valueOf(totalUsageMonday));
        setSharedPreference("WeeklyQuery", "totalUsageTuesday", String.valueOf(totalUsageTuesday));
        setSharedPreference("WeeklyQuery", "totalUsageWednesday", String.valueOf(totalUsageWednesday));
        setSharedPreference("WeeklyQuery", "totalUsageThursday", String.valueOf(totalUsageThursday));
        setSharedPreference("WeeklyQuery", "totalUsageFriday", String.valueOf(totalUsageFriday));
        setSharedPreference("WeeklyQuery", "totalUsageSaturday", String.valueOf(totalUsageSaturday));
        setSharedPreference("WeeklyQuery", "totalUsageSunday", String.valueOf(totalUsageSunday));

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

    private void getMonday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listMondayAppStats = new ArrayList<>();
        listMondayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listMondayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageMonday = calculateTotalTime(totalTimeInForeground);
            }
        }

        Log.d("Monday", String.valueOf(totalUsageMonday));
        }

        //TIISTAIN STATSIT
    private void getTuesday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        usageStatsForOneWeekday.clear();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listTuesdayAppStats = new ArrayList<>();
        listTuesdayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listTuesdayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageTuesday = calculateTotalTime(totalTimeInForeground);
            }
        }
        Log.d("TUESDAY", String.valueOf(totalUsageTuesday));
    }

    private void getWednesday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        usageStatsForOneWeekday.clear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listWednesdayAppStats = new ArrayList<>();
        listWednesdayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listWednesdayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageWednesday = calculateTotalTime(totalTimeInForeground);
            }
        }
        Log.d("Wednesday", String.valueOf(totalUsageWednesday));
    }

    private void getThursday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        usageStatsForOneWeekday.clear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listThursdayAppStats = new ArrayList<>();
        listThursdayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listThursdayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageThursday = calculateTotalTime(totalTimeInForeground);
            }
        }
        Log.d("Thursday", String.valueOf(totalUsageThursday));
    }

    private void getFriday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        usageStatsForOneWeekday.clear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listFridayAppStats = new ArrayList<>();
        listFridayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listFridayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageFriday = calculateTotalTime(totalTimeInForeground);
            }
        }
        Log.d("Friday", String.valueOf(totalUsageFriday));
    }

    private void getSaturday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        usageStatsForOneWeekday.clear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listSaturdayAppStats = new ArrayList<>();
        listSaturdayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listSaturdayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageSaturday = calculateTotalTime(totalTimeInForeground);
            }
        }
        Log.d("Saturday", String.valueOf(totalUsageSaturday));
    }

    private void getSunday()
    {
        long begin = 0;
        long end = 0;

        totalUsageTime = 0;

        usageStatsForOneWeekday.clear();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        begin = cal.getTimeInMillis();
        //Lisätään yksi päivä ja otetaan ylös millisekunteina
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        end = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsForOneWeekday = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listSundayAppStats = new ArrayList<>();
        listSundayAppStats.addAll(usageStatsForOneWeekday.values());

        for(UsageStats lUsageStats: listSundayAppStats) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                totalUsageSunday = calculateTotalTime(totalTimeInForeground);
            }
        }
        Log.d("Sunday", String.valueOf(totalUsageSunday));
    }
}
