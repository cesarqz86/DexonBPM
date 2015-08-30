package us.dexon.dexonbpm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import us.dexon.dexonbpm.R;

/**
 * Created by Cesar Quiroz on 8/30/15.
 */
public class RectangularProgressFragment extends Fragment {

    private Double progress;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        View rootView = inflater.inflate(R.layout.fragment_rectangular_progress, container, false);
        Bundle args = getArguments();
        this.progress = args.getDouble("progress");

        int progressInt = this.progress.intValue();
        StringBuilder progressText = new StringBuilder();
        progressText.append(progressInt);
        progressText.append("%");

        TextView txt_progresstext = (TextView) rootView.findViewById(R.id.txt_progresstext);
        RoundCornerProgressBar rectangularProgressBar = (RoundCornerProgressBar) rootView.findViewById(R.id.rectangularProgressBar);
        rectangularProgressBar.setMax(progressInt > 100 ? progressInt : 100);
        rectangularProgressBar.setProgress(progressInt);
        txt_progresstext.setText(progressText.toString());

        if (progressInt <= 50) {
            txt_progresstext.setTextColor(getResources().getColor(R.color.progress_green_transparent));
            rectangularProgressBar.setProgressColor(getResources().getColor(R.color.progress_green));
        } else if (progressInt <= 80) {
            txt_progresstext.setTextColor(getResources().getColor(R.color.progress_yellow_transparent));
            rectangularProgressBar.setProgressColor(getResources().getColor(R.color.progress_yellow));
        } else {
            txt_progresstext.setTextColor(getResources().getColor(R.color.progress_red_transparent));
            rectangularProgressBar.setProgressColor(getResources().getColor(R.color.progress_red));
        }

        if (progressText.length() >= 4) {
            txt_progresstext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        }
        return rootView;
    }
}
