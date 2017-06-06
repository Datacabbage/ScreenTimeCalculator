package tuomomees.screentimecalculator;

/**
 * Created by tuomo on 30.5.2017.
 */

public class Converter {

    protected long convertMillisToMinutes(long millis)
    {
        long minutes = (millis / 1000) / 60;
        return minutes;
    }
}
