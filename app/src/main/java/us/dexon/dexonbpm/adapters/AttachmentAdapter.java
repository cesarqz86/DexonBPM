package us.dexon.dexonbpm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.model.ReponseDTO.AttachmentDto;

/**
 * Created by Cesar Quiroz on 10/26/15.
 */
public class AttachmentAdapter extends ArrayAdapter<AttachmentDto> {

    private final Context context;
    private final List<AttachmentDto> values;

    public AttachmentAdapter (Context context, List<AttachmentDto> values){
        super(context, R.layout.item_detailticket, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_tree_regular, parent, false);

        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
        txt_fieldvalue.setText(values.get(position).getAttachmentName());
        txt_fieldvalue.setTag(values.get(position).getAttachmentObject());
        return rowView;
    }
}
