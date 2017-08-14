package tuomomees.screentimecalculator;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import static android.content.Context.MODE_PRIVATE;

public class WeeklyBarDiagramFragment extends Fragment {

    View view;
    GraphView graph;

    TextView textViewAvgUsage;

    String mondayStr;
    String tuesdayStr;
    String wednesdayStr;
    String thursdayStr;
    String fridayStr;
    String saturdayStr;
    String sundayStr;
    long totalUsageMonday, totalUsageTuesday, totalUsageWednesday, totalUsageThursday, totalUsageFriday, totalUsageSaturday, totalUsageSunday;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_weeklybardiagram, container, false);

        //Alustetaan palkkidiagrammi
        initializeBarDiagram();

        //Haetaan data
        getBarDiagramData();

        convertBarDiagramData();

        //Asetetaan diagrammissa näkyvä data
        setBarDiagramData();

        return view;
    }

    protected void initializeBarDiagram()
    {
        graph = (GraphView) view.findViewById(R.id.graph);
        textViewAvgUsage = (TextView) view.findViewById(R.id.textViewAvgUsage);
    }

    protected void getBarDiagramData()
    {
        mondayStr = getSharedPreferences("WeeklyQuery", "totalUsageMonday");
        tuesdayStr = getSharedPreferences("WeeklyQuery", "totalUsageTuesday");
        wednesdayStr = getSharedPreferences("WeeklyQuery", "totalUsageWednesday");
        thursdayStr = getSharedPreferences("WeeklyQuery", "totalUsageThursday");
        fridayStr = getSharedPreferences("WeeklyQuery", "totalUsageFriday");
        saturdayStr = getSharedPreferences("WeeklyQuery", "totalUsageSaturday");
        sundayStr = getSharedPreferences("WeeklyQuery", "totalUsageSunday");
    }

    protected void convertBarDiagramData()
    {
        totalUsageMonday = Long.parseLong(mondayStr);
        totalUsageTuesday = Long.parseLong(tuesdayStr);
        totalUsageWednesday = Long.parseLong(wednesdayStr);
        totalUsageThursday = Long.parseLong(thursdayStr);
        totalUsageFriday = Long.parseLong(fridayStr);
        totalUsageSaturday = Long.parseLong(saturdayStr);
        totalUsageSunday = Long.parseLong(sundayStr);
    }

    protected void setBarDiagramData()
    {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, totalUsageSunday),     //Sun
                new DataPoint(2, totalUsageMonday),     //Mon
                new DataPoint(4, totalUsageTuesday),    //Tue
                new DataPoint(6, totalUsageWednesday),  //Wed
                new DataPoint(8, totalUsageThursday),   //Thu
                new DataPoint(10, totalUsageFriday),     //Fri
                new DataPoint(12, totalUsageSaturday)    //Sat
        });



        final long avgUsage = (totalUsageMonday + totalUsageTuesday + totalUsageWednesday + totalUsageThursday + totalUsageFriday + totalUsageSaturday + totalUsageSunday) / 7;



        // Staattiset muuttujat X-akselille (SUN - SAT)
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"});


        //Kirjoittaa arvot palkkien päälle
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);

        //staticLabelsFormatter.setVerticalLabels(new String[] {"", ""});
        graph.setTitle("Usage stats are described in minutes"); //TODO: tähän stringi

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);



        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {

                if(data.getY() < avgUsage)
                {
                    return Color.GREEN;
                }

                else{
                    return Color.RED;
                }

                //return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(75);

        //Asettaa automaattisen animoinnin päälle
        series.setAnimated(true);
        //graph.setTitle("min");
        graph.setTitleColor(Color.WHITE);

        graph.addSeries(series);

        String str = String.valueOf(avgUsage);

        textViewAvgUsage.setText(getResources().getString(R.string.avgtime_thisweek)+ " " + str + " min");
    }

    //Metodi, jolla voi hakea jaetun String -muuttujan
    protected String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = this.getActivity().getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        return pref.getString(sharedVariableTag, null);
    }
}
