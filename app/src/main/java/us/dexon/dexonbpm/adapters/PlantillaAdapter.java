package us.dexon.dexonbpm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;

/**
 * Created by Cesar Quiroz on 8/28/15.
 */
public class PlantillaAdapter extends ArrayAdapter<TreeDataDto> {

    private final Context context;
    private final List<TreeDataDto> values;
    private final Map<String, List<TreeDataDto>> fullList;
    private final TicketResponseDto ticketData;
    private final String fieldKey;

    public PlantillaAdapter(Context context,
                       Map<String, List<TreeDataDto>> fullList,
                       String key,
                       TicketResponseDto ticketInfo,
                       String fieldKey) {
        super(context, R.layout.item_detailticket, (fullList != null && fullList.containsKey(key)) ? fullList.get(key) : new ArrayList<TreeDataDto>());
        this.context = context;
        this.fullList = fullList;
        this.values = (fullList != null && fullList.containsKey(key)) ? fullList.get(key) : new ArrayList<TreeDataDto>();
        this.ticketData = ticketInfo;
        this.fieldKey = fieldKey;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_tree_regular, parent, false);
        if (this.fullList.containsKey(this.values.get(position).getElementId())) {
            rowView = inflater.inflate(R.layout.item_tree_more, parent, false);
            rowView.setOnClickListener(new DexonListeners.Plantilla2ClickListener(rowView.getContext(),
                    this.values.get(position).getElementId(),
                    this.fieldKey));
        }
        else{
            rowView.setOnClickListener(new DexonListeners.PlantillaFinalClickListener(rowView.getContext(),
                    this.values.get(position),
                    this.ticketData,
                    this.fieldKey));
        }
        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
        txt_fieldvalue.setText(values.get(position).getElementName());
        return rowView;
    }
}