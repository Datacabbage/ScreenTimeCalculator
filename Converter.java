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
        Log.d("Muunnetaan", String.valueOf(millis));

        @SuppressLint("DefaultLocale") String usageTime = String.format("%02d hour, %02d min, %02d sec",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        Log.d("Muunnettu", usageTime);
        return usageTime;
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
