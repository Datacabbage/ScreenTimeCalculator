package tuomomees.screentimecalculator;

/**
 * Luokan on luonut tuomo päivämäärällä 14.8.2017.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

//TextViewin extendaaminen herjaa erroria, mutta toimii
public class customTextView extends TextView {
    public customTextView(Context context) {
        super(context);
        setFont();
    }
    public customTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public customTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    //Fontin voi vaihtaa polusta C:\Users\tuomo\AndroidStudioProjects\ScreenTimeCalculator\app\src\main\assets
    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "TimeTravelerFont.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
