package tuomomees.screentimecalculator;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.TimeUnit;


class Converter {

    long convertMillisToMinutes(long millis)
    {
        return (millis / 1000) / 60;
    }

    String convertMillisToHoursMinutesSeconds(long millis)
    {
        //Log.d("Muunnetaan", String.valueOf(millis));

        //@SuppressLint("DefaultLocale") String usageTime = String.format("%02d hour, %02d min, %02d sec",
        //@SuppressLint("DefaultLocale") String usageTime = String.format("%02d hour, %02d min, %02d sec",

        if(millis >= TimeUnit.HOURS.toMillis(1))
        {
            @SuppressLint("DefaultLocale") String usageTime = String.format("%d hour, %d min, %d sec",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            return usageTime;
        }

        if(millis >= TimeUnit.MINUTES.toMillis(1))
        {
            @SuppressLint("DefaultLocale") String usageTime = String.format("%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            return usageTime;
        }

        if(millis >= TimeUnit.SECONDS.toMillis(1))
        {
            @SuppressLint("DefaultLocale") String usageTime = String.format("%d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis));

            return usageTime;
        }

        else
        {
            return "Something wrong with converter";
        }

        //Log.d("Muunnettu", usageTime);
    }

    @SuppressLint("DefaultLocale")
    protected String convertMillisToMinutesSeconds(long millis)
    {
        return String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
