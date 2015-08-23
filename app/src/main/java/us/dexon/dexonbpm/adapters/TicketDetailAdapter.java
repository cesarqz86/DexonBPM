package us.dexon.dexonbpm.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;

/**
 * Created by Cesar Quiroz on 8/17/15.
 */
public class TicketDetailAdapter extends ArrayAdapter<TicketDetailDataDto> {
    private final Context context;
    private final List<TicketDetailDataDto> values;

    public TicketDetailAdapter(Context context, List<TicketDetailDataDto> values) {
        super(context, R.layout.item_detailticket, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_detailticket, parent, false);

        RenderControlType controlType = values.get(position).getFieldType();
        switch (controlType) {
            case DXControlsTree: {
                rowView = inflater.inflate(R.layout.item_detailticket_tree, parent, false);
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(values.get(position).getFieldName());
                txt_fieldvalue.setText(values.get(position).getFieldValue());
                rowView.setOnClickListener(new DexonListeners.ListViewClickListener(this.getContext(), values.get(position).getFieldSonData()));
                break;
            }
            case DXControlsGrid:{
                rowView = inflater.inflate(R.layout.item_detailticket_tree, parent, false);
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(values.get(position).getFieldName());
                txt_fieldvalue.setText(values.get(position).getFieldValue());
                rowView.setOnClickListener(new DexonListeners.TableClickListener(this.getContext(), values.get(position).getFieldSonData()));
                break;
            }
            case DXControlsDate: {
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                String dateString = values.get(position).getFieldValue();
                if (CommonValidations.validateEmpty(dateString)) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                        dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                    } catch (Exception ex) {
                        Log.e("Converting Date: ", ex.getMessage(), ex);
                    }
                }

                txt_fieldtitle.setText(values.get(position).getFieldName());
                txt_fieldvalue.setText(dateString);
                break;
            }
            case DXControlsGridWithOptions: {
                rowView = inflater.inflate(R.layout.item_detailticket_technician, parent, false);

                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(values.get(position).getFieldName());
                txt_fieldvalue.setText(values.get(position).getFieldValue());

                Button btn_setmanual_technician = (Button) rowView.findViewById(R.id.btn_setmanual_technician);
                Button btn_setautomatic_technician = (Button) rowView.findViewById(R.id.btn_setautomatic_technician);
                Button btn_settome_technician = (Button) rowView.findViewById(R.id.btn_settome_technician);

                btn_setmanual_technician.setPressed(true);
                break;
            }
            default: {
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(values.get(position).getFieldName());
                txt_fieldvalue.setText(values.get(position).getFieldValue());
                break;
            }
        }
        return rowView;
    }
}
