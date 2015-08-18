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
import android.widget.Toast;

import inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.ForgotPasswordActivity;
import us.dexon.dexonbpm.activity.TicketDetail;

public class MatrixTableAdapter extends BaseTableAdapter implements View.OnClickListener{

    private final static int WIDTH_DIP = 150;
    private final static int HEIGHT_DIP = 45;

    private final LayoutInflater inflater;

    private final Activity context;

    private String headers[] = {
            "TICKET",
            "",
            "",
            "",
            ""
    };

    private String[][] table;
    private int indexColumnID;

    private final int width;
    private final int height;

    public MatrixTableAdapter(Activity context, String[][] table, int columnID) {
        this.context = context;
        Resources r = this.context.getResources();

        this.width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
        this.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_DIP, r.getDisplayMetrics()));
        this.table = table;
        this.inflater = LayoutInflater.from(context);
        this.indexColumnID = columnID;
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

        ((TextView) convertView).setText(table[row + 1][column + 1].toString());
        if(rowType != R.layout.row_header && this.indexColumnID > -1) {
            convertView.setTag(R.string.tag_id_ticket, table[row + 1][this.indexColumnID].toString());
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
        //Toast.makeText(context, idTag, Toast.LENGTH_SHORT).show();

        String idTag = (String)v.getTag(R.string.tag_id_ticket);
        Intent incidentDetail = new Intent(context, TicketDetail.class);
        incidentDetail.putExtra("TICKET_ID", idTag);
        context.startActivity(incidentDetail);
        context.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
    }
}
