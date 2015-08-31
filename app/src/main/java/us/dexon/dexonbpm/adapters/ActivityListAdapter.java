package us.dexon.dexonbpm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.model.ReponseDTO.ActivityTreeDto;

/**
 * Created by Cesar Quiroz on 8/31/15.
 */
public class ActivityListAdapter extends ArrayAdapter<ActivityTreeDto> {

    private final Context context;
    private final List<ActivityTreeDto> values;

    public ActivityListAdapter(Context context,
                               List<ActivityTreeDto> valuesData) {
        super(context, R.layout.item_activity_text, valuesData);
        this.context = context;
        this.values = valuesData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_activity_text, parent, false);
        TextView txt_next_activity = (TextView) rowView.findViewById(R.id.txt_next_activity);
        txt_next_activity.setText(values.get(position).getElementName());
        rowView.setOnClickListener(new DexonListeners.ActivityClickListener(rowView.getContext(),
                values.get(position),
                values.get(position).getElementName()));
        return rowView;
    }
}