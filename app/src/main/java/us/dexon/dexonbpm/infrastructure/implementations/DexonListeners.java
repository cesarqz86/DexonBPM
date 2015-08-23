package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.activity.ListViewActivity;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;

/**
 * Created by Cesar Quiroz on 8/22/15.
 */
public final class DexonListeners {

    public static class ListViewClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String sonData;

        public ListViewClickListener(Context context, String sonElement) {
            this.currentContext = context;
            this.sonData = sonElement;
        }

        public void onClick(View v) {
            Activity previousActivity = (Activity) this.currentContext;
            //Toast.makeText(v.getContext(), "I'm clicked!", Toast.LENGTH_SHORT).show();
            Intent listViewDetailIntent = new Intent(previousActivity, ListViewActivity.class);
            listViewDetailIntent.putExtra("sonData", this.sonData);
            previousActivity.startActivity(listViewDetailIntent);
            previousActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class ListView2ClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String keyToSearch;
        private final String sonData;

        public ListView2ClickListener(Context context, String key, String sonElement) {
            this.currentContext = context;
            this.keyToSearch = key;
            this.sonData = sonElement;
        }

        public void onClick(View v) {
            Activity previousActivity = (Activity) this.currentContext;
            //Toast.makeText(v.getContext(), "I'm clicked!", Toast.LENGTH_SHORT).show();
            Intent listViewDetailIntent = new Intent(previousActivity, ListViewActivity.class);
            listViewDetailIntent.putExtra("sonData", this.sonData);
            listViewDetailIntent.putExtra("keyToSearch", this.keyToSearch);
            previousActivity.startActivity(listViewDetailIntent);
            previousActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }
}
