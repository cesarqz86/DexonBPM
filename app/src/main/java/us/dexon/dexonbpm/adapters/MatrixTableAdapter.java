package us.dexon.dexonbpm.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;

public class MatrixTableAdapter<T> extends BaseTableAdapter {

    private final static int WIDTH_DIP = 150;
    private final static int HEIGHT_DIP = 45;

    private final LayoutInflater inflater;

    private final Context context;

    private String[][] table;

    private final int width;
    private final int height;

    public MatrixTableAdapter(Context context, String[][] table) {
        this.context = context;
        Resources r = this.context.getResources();

        this.width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH_DIP, r.getDisplayMetrics()));
        this.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_DIP, r.getDisplayMetrics()));
        this.table = table;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getRowCount() {
        return this.table.length - 1;
    }

    @Override
    public int getColumnCount() {
        return this.table[0].length - 1;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(getLayoutResource(row, column), parent, false);
            ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
        }

        ((TextView) convertView).setText(table[row + 1][column + 1].toString());
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
}
