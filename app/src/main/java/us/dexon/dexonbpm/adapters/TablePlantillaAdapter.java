package us.dexon.dexonbpm.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.NewTicketActivity;
import us.dexon.dexonbpm.activity.RelatedDataActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.TicketRelatedDataDto;

/**
 * Created by Cesar Quiroz on 12/2/15.
 */
public class TablePlantillaAdapter extends BaseTableAdapter implements View.OnClickListener {

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

    public TablePlantillaAdapter(Activity context, String[][] table, int columnID, String fieldKey) {
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

        this.relatedDataClick(rowNumber);
    }

    private void relatedDataClick(int rowNumber) {

        CommonSharedData.IsOnClick = true;

        JsonObject relatedData = CommonSharedData.RelatedData;

        StringBuilder valueKeyBuilder = new StringBuilder();
        valueKeyBuilder.append(relatedData.get("tb_name").getAsString());
        valueKeyBuilder.append("_ID");
        String valueKey = valueKeyBuilder.toString();
        int columnValueID = this.indexColumnID;
        int tempIndex = 0;
        for (String columnName : this.table[0]) {
            if (columnName.equals(valueKey)) {
                columnValueID = tempIndex;
                break;
            }
            tempIndex++;
        }

        relatedData.addProperty("record_ID", Integer.parseInt(this.table[rowNumber][columnValueID]));
        CommonSharedData.RelatedData = relatedData;
        CommonSharedData.SelectedRelatedData.setFieldSonData(relatedData);

        /*RelatedDataActivity relatedDataActivity = (RelatedDataActivity) CommonSharedData.RelatedDataActivity;
        relatedDataActivity.reloadRelatedData(relatedData);*/

        Activity currentActivity = this.context;
        Intent relatedDataIntent = new Intent(currentActivity, RelatedDataActivity.class);
        relatedDataIntent.putExtra("activityTitle", relatedData.get("tb_name").getAsString());

        relatedDataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(relatedDataIntent);
        this.context.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);

        CommonSharedData.IsReloadRelatedData = true;
    }
}
