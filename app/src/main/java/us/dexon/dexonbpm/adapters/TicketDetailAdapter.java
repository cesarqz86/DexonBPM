package us.dexon.dexonbpm.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;

/**
 * Created by Cesar Quiroz on 8/17/15.
 */
public class TicketDetailAdapter extends ArrayAdapter<TicketDetailDataDto> {
    private final Context context;
    private final List<TicketDetailDataDto> values;
    private final TicketResponseDto ticketData;

    public TicketDetailAdapter(Context context, List<TicketDetailDataDto> values, TicketResponseDto ticketInfo) {
        super(context, R.layout.item_detailticket, values);
        this.context = context;
        this.values = values;
        this.ticketData = ticketInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_detailticket, parent, false);

        RenderControlType controlType = values.get(position).getFieldType();
        switch (controlType) {
            case DXControlsTree: {
                rowView = inflater.inflate(R.layout.item_detailticket_tree, parent, false);
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(this.values.get(position).getFieldName());
                txt_fieldvalue.setText(this.values.get(position).getFieldValue());
                if(this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                    rowView.setOnClickListener(new DexonListeners.ListViewClickListener(this.getContext(),
                            this.values.get(position).getFieldSonData(),
                            this.values.get(position).getFieldKey()));
                }
                break;
            }
            case DXControlsGrid: {
                rowView = inflater.inflate(R.layout.item_detailticket_tree, parent, false);
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(this.values.get(position).getFieldName());
                txt_fieldvalue.setText(this.values.get(position).getFieldValue());
                if(this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                    rowView.setOnClickListener(new DexonListeners.TableClickListener(this.getContext(), this.values.get(position).getFieldSonData()));
                }
                break;
            }
            case DXControlsDate: {
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                String dateString = this.values.get(position).getFieldValue();
                if (CommonValidations.validateEmpty(dateString)) {
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                        dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                    } catch (Exception ex) {
                        Log.e("Converting Date: ", ex.getMessage(), ex);
                    }
                }

                txt_fieldtitle.setText(this.values.get(position).getFieldName());
                txt_fieldvalue.setText(dateString);
                break;
            }
            case DXControlsGridWithOptions: {
                rowView = inflater.inflate(R.layout.item_detailticket_technician, parent, false);

                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(this.values.get(position).getFieldName());
                txt_fieldvalue.setText(this.values.get(position).getFieldValue());

                RadioButton btn_setmanual_technician = (RadioButton) rowView.findViewById(R.id.btn_setmanual_technician);
                RadioButton btn_setautomatic_technician = (RadioButton) rowView.findViewById(R.id.btn_setautomatic_technician);
                RadioButton btn_settome_technician = (RadioButton) rowView.findViewById(R.id.btn_settome_technician);

                if(this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                    DexonListeners.TechnicianClickListener technicianClickListener = new DexonListeners.TechnicianClickListener(this.getContext(),
                            this.ticketData.getCurrentTechnician(),
                            this.ticketData);
                    btn_setmanual_technician.setOnClickListener(technicianClickListener);
                    btn_setautomatic_technician.setOnClickListener(technicianClickListener);
                    btn_settome_technician.setOnClickListener(technicianClickListener);
                }

                switch (this.ticketData.getTechnicianSelected()) {
                    case R.id.btn_setmanual_technician: {
                        btn_setmanual_technician.setChecked(true);
                        txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                0, //left
                                0, //top
                                R.drawable.ic_arrow, //right
                                0);//bottom
                        if(this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                            rowView.setOnClickListener(new DexonListeners.TableClickListener(this.getContext(), values.get(position).getFieldSonData()));
                        }
                        break;
                    }
                    case R.id.btn_setautomatic_technician: {
                        btn_setautomatic_technician.setChecked(true);
                        txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                0, //left
                                0, //top
                                0, //right
                                0);//bottom
                        break;
                    }
                    case R.id.btn_settome_technician: {
                        btn_settome_technician.setChecked(true);
                        txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                0, //left
                                0, //top
                                0, //right
                                0);//bottom
                        break;
                    }
                }
                break;
            }
            default: {
                TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                txt_fieldtitle.setText(this.values.get(position).getFieldName());
                txt_fieldvalue.setText(this.values.get(position).getFieldValue());
                break;
            }
        }
        return rowView;
    }
}
