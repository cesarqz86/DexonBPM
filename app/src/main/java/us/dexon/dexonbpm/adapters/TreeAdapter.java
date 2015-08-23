package us.dexon.dexonbpm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;

/**
 * Created by Cesar Quiroz on 8/22/15.
 */
public class TreeAdapter extends ArrayAdapter<TreeDataDto> {

    private final Context context;
    private final List<TreeDataDto> values;
    private final Map<String, List<TreeDataDto>> fullList;
    private final String sonData;

    public TreeAdapter(Context context, Map<String, List<TreeDataDto>> fullList, String key, String sonData) {
        super(context, R.layout.item_detailticket, fullList.get(key));
        this.context = context;
        this.fullList = fullList;
        this.values = fullList.get(key);
        this.sonData = sonData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_tree_regular, parent, false);
        if (this.fullList.containsKey(this.values.get(position).getElementId())) {
            rowView = inflater.inflate(R.layout.item_tree_more, parent, false);
            rowView.setOnClickListener(new DexonListeners.ListView2ClickListener(rowView.getContext(),
                    this.values.get(position).getElementId(),
                    this.sonData));
        }
        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
        txt_fieldvalue.setText(values.get(position).getElementName());
        return rowView;
    }
}
