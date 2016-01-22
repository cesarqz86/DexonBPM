package us.dexon.dexonbpm.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;

/**
 * Created by Cesar Quiroz on 8/30/15.
 */
public class CircularProgressFragment extends Fragment {

    private Double progress;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_circular_progres, container, false);
        Bundle args = getArguments();
        this.progress = args.getDouble("progress");

        int progressInt = this.progress.intValue();
        StringBuilder progressText = new StringBuilder();
        progressText.append(progressInt);

        int pixelSize = CommonService.convertSpToPixel(25f, this.getActivity());

        String primaryColorString = ConfigurationService.getConfigurationValue(container.getContext(), "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);

        DonutProgress circularProgressBar = (DonutProgress) rootView.findViewById(R.id.circularProgressBar);
        TextView txt_progress_left = (TextView) rootView.findViewById(R.id.txt_progress_left);
        TextView txt_progress_right = (TextView) rootView.findViewById(R.id.txt_progress_right);
        txt_progress_left.setTextColor(primaryColor);
        txt_progress_right.setTextColor(primaryColor);

        circularProgressBar.setMax(progressInt > 100 ? progressInt : 100);
        circularProgressBar.setProgress(progressInt);
        circularProgressBar.setTextSize(pixelSize);
        //circularProgressBar.setTextSize(80);

        if (progressInt <= 50) {
            circularProgressBar.setTextColor(getResources().getColor(R.color.progress_green_transparent));
            circularProgressBar.setFinishedStrokeColor(getResources().getColor(R.color.progress_green_transparent));
            circularProgressBar.setUnfinishedStrokeColor(getResources().getColor(R.color.progress_background));
        } else if (progressInt <= 80) {
            circularProgressBar.setTextColor(getResources().getColor(R.color.progress_yellow_transparent));
            circularProgressBar.setFinishedStrokeColor(getResources().getColor(R.color.progress_yellow_transparent));
            circularProgressBar.setUnfinishedStrokeColor(getResources().getColor(R.color.progress_background));
        } else {
            circularProgressBar.setTextColor(getResources().getColor(R.color.progress_red_transparent));
            circularProgressBar.setFinishedStrokeColor(getResources().getColor(R.color.progress_red_transparent));
            circularProgressBar.setUnfinishedStrokeColor(getResources().getColor(R.color.progress_background));
        }

        if (progressText.length() >= 4) {
            pixelSize = CommonService.convertSpToPixel(15f, this.getActivity());
            //circularProgressBar.setTextSize(30);
        }
        return rootView;
    }
}
