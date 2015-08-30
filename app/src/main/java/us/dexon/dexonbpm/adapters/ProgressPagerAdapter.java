package us.dexon.dexonbpm.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import us.dexon.dexonbpm.fragments.CircularProgressFragment;
import us.dexon.dexonbpm.fragments.RectangularProgressFragment;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;

/**
 * Created by Cesar Quiroz on 8/30/15.
 */
public class ProgressPagerAdapter extends FragmentStatePagerAdapter {

    private final TicketResponseDto ticketInfo;

    public ProgressPagerAdapter(FragmentManager fm, TicketResponseDto progress) {
        super(fm);
        this.ticketInfo = progress;
    }

    public Fragment getItem(int i) {
        Fragment fragment;
        Bundle args = new Bundle();
        if (i == 0) {
            fragment = new CircularProgressFragment();
            args.putDouble("progress", this.ticketInfo.getCircularPercentDone());
        } else {
            fragment = new RectangularProgressFragment();
            args.putDouble("progress", this.ticketInfo.getBarPercentDone() == null ? -1f : this.ticketInfo.getBarPercentDone());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        int pagerCount = 2;
        /*if (this.ticketInfo.getBarPercentDone() == null) {
            pagerCount = 1;
        }*/
        return pagerCount;
    }
}
