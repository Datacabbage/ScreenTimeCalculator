package tuomomees.screentimecalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import static android.content.Context.MODE_PRIVATE;

/**
 * Luokan on luonut tuomo päivämäärällä 7.6.2017.
 */

public class LastTimeUsedFragment extends Fragment {

    // Store instance variables
    //private String title;
    //private int page;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //page = getArguments().getInt("someInt", 0);
        //title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lasttimeused, container, false);

        //View view = inflater.inflate(R.layout.fragment_lasttimeused, container, false);
        //TextView tvLabel = (TextView) view.findViewById(R.id.textViewTop5AppsInfo);
        //tvLabel.setText(page + " -- " + title);
        return view;
    }

    // newInstance constructor for creating fragment with arguments
    public static LastTimeUsedFragment newInstance(int page, String title) {
        LastTimeUsedFragment fragmentFirst = new LastTimeUsedFragment();
        //Bundle args = new Bundle();
        //args.putInt("someInt", page);
        //args.putString("someTitle", title);
        //fragmentFirst.setArguments(args);
        return fragmentFirst;
    }



    protected String getSharedPreferences(String sharedPrefTag, String sharedVariableTag)
    {
        SharedPreferences pref = this.getActivity().getSharedPreferences(sharedPrefTag, MODE_PRIVATE);
        String str = pref.getString(sharedVariableTag, null);

        return str;
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lasttimeused, container, false);
    }
    */

}
