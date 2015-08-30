package us.dexon.dexonbpm.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.NewTicketActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;

/**
 * Created by Cesar Quiroz on 8/23/15.
 */
public class TableAdapter extends BaseTableAdapter implements View.OnClickListener {

    private final static int WIDTH_DIP = 150;
    private final static int HEIGHT_DIP = 45;

    private final LayoutInflater inflater;

    private final Activity context;

    private String headers[] = {
            "",
            "",
            "",
            "",
            ""
    };

    private String[][] table;
    private int indexColumnID;
    private String fieldKey;

    private final int width;
    private final int height;

    public TableAdapter(Activity context, String[][] table, int columnID, String fieldKey) {
        this.context = context;
        Resources r = this.context.getResources();

        this.width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
        this.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_DIP, r.getDisplayMetrics()));
        this.table = table;
        this.inflater = LayoutInflater.from(context);
        this.indexColumnID = columnID;
        this.fieldKey = fieldKey;
    }

    @Override
    public int getRowCount() {
        return this.table.length - 1;
    }

    @Override
    public int getColumnCount() {
        if (this.table != null && this.table.length > 0) {
            return this.table[0].length - 1;
        } else {
            return this.headers.length - 1; //this.table[0].length - 1;
        }
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {

        int rowType = getLayoutResource(row, column);
        if (convertView == null) {
            convertView = inflater.inflate(rowType, parent, false);
            ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
        }

        ((TextView) convertView).setText(table[row + 1][column + 1]);
        if (rowType != R.layout.row_header && this.indexColumnID > -1) {
            int rowNumber = row + 1;
            convertView.setTag(R.id.tag_column_table, String.valueOf(rowNumber));
            convertView.setOnClickListener(this);
        }
        return convertView;
    }

    @Override
    public int getHeight(int row) {
        return height;
    }

    @Override
    public int getWidth(int column) {
        return width;
    }

    public int getLayoutResource(int row, int column) {
        final int layoutResource;
        switch (getItemViewType(row, column)) {
            case 0:
                layoutResource = R.layout.row_header;
                break;
            case 1:
                layoutResource = R.layout.row_odd;
                break;
            case 2:
                layoutResource = R.layout.row_even;
                break;
            default:
                throw new RuntimeException("");
        }
        return layoutResource;
    }

    @Override
    public int getItemViewType(int row, int column) {
        if (row < 0) {
            return 0;
        } else {
            return (row & 1) == 0 ? 1 : 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public void onClick(View v) {

        String rowNumberString = (String) v.getTag(R.id.tag_column_table);
        int rowNumber = Integer.parseInt(rowNumberString);

        JsonObject ticketJsonInfo = CommonSharedData.TicketInfo.getTicketInfo();
        JsonObject headerInfo = ticketJsonInfo.get("headerInfo").getAsJsonObject();
        JsonObject fieldInfo = headerInfo.get(this.fieldKey).getAsJsonObject();
        JsonObject sonData = fieldInfo.get("son").getAsJsonObject();

        String valueKey = sonData.get("tb_name") + "_ID";
        int columnValueID = this.indexColumnID;
        int tempIndex = 0;
        for (String columnName : this.table[0]) {
            if (columnName.equals(valueKey)) {
                columnValueID = tempIndex;
                break;
            }
            tempIndex++;
        }

        fieldInfo.addProperty("Value", this.table[rowNumber][columnValueID]);
        sonData.addProperty("short_description", this.table[rowNumber][this.indexColumnID]);

        fieldInfo.add("son", sonData);
        headerInfo.add(this.fieldKey, fieldInfo);
        ticketJsonInfo.add("headerInfo", headerInfo);
        ITicketService ticketService = TicketService.getInstance();
        CommonSharedData.TicketInfo = ticketService.convertToTicketData(ticketJsonInfo, R.id.btn_setmanual_technician, null);

        Activity currentActivity = (Activity) this.context;
        TicketDetail ticketDetail = null;
        NewTicketActivity newTicket = null;
        Intent ticketDetailActivity = null;

        if (CommonSharedData.TicketActivity instanceof TicketDetail) {
            ticketDetail = (TicketDetail) CommonSharedData.TicketActivity;
            ticketDetailActivity = new Intent(currentActivity, TicketDetail.class);
        } else if (CommonSharedData.TicketActivity instanceof NewTicketActivity) {
            newTicket = (NewTicketActivity) CommonSharedData.TicketActivity;
            ticketDetailActivity = new Intent(currentActivity, NewTicketActivity.class);
        }

        if (ticketDetail != null)
            ticketDetail.inidentsCallBack(CommonSharedData.TicketInfo);

        if (newTicket != null)
            newTicket.inidentsCallBack(CommonSharedData.TicketInfo);

        ticketDetailActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(ticketDetailActivity);
        this.context.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
        Boolean isStatus = this.fieldKey.equals("status");
        Boolean isReloadRequired = sonData.get("can_trigger_BF").getAsBoolean();
        if (isReloadRequired || isStatus) {
            Date currentDate = new Date();

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'-05:00'");
            CharSequence currentDateString = dateFormater.format(currentDate);
            ticketJsonInfo.addProperty("LastUpdateTime", currentDateString.toString());
            if (ticketDetail != null)
                ticketDetail.reloadCallback(ticketJsonInfo);

            if (newTicket != null)
                newTicket.reloadCallback(ticketJsonInfo);
        }
    }
}
