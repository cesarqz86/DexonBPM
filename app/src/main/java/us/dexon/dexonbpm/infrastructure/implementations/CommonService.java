package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;

/**
 * Created by Cesar Quiroz on 4/11/15.
 */
public class CommonService {

    public static TableLayout AddRowToTable(Context context, TableLayout tableLayout, boolean isHeader, String[] contentData) {
        TableRow rowToAdd = new TableRow(context);

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams();
        // Wrap-up the content of the row
        rowParams.height = TableLayout.LayoutParams.WRAP_CONTENT;
        rowParams.width = TableLayout.LayoutParams.WRAP_CONTENT;

        for (String columnData : contentData) {
            TableRow.LayoutParams columnParams = new TableRow.LayoutParams();
            columnParams.height = TableLayout.LayoutParams.WRAP_CONTENT;
            columnParams.width = TableLayout.LayoutParams.WRAP_CONTENT;
            columnParams.gravity = isHeader ? Gravity.CENTER : Gravity.FILL;
            TextView txtColumn = new TextView(context);
            txtColumn.setText(columnData);
            rowToAdd.addView(txtColumn, columnParams);
        }

        tableLayout.addView(rowToAdd, rowParams);
        return tableLayout;
    }

    public static void ShowAlertDialog(Context context, int titleID, int messageID, MessageTypeIcon messageTypeIcon) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(titleID));
        alertDialogBuilder.setMessage(context.getString(messageID));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(context.getString(R.string.btn_ok_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );
        alertDialogBuilder.setNegativeButton(context.getString(R.string.btn_cancel_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        switch (messageTypeIcon) {
            case Success: {
                //alertDialogBuilder.setIcon(android.R.drawable);
                break;
            }
            case Warning: {
                alertDialogBuilder.setIcon(R.drawable.ic_warning_amber);
                break;
            }
            case Error: {
                //alertDialogBuilder.setIcon(android.R.drawable);
                alertDialogBuilder.setIcon(R.drawable.ic_error_red);
                break;
            }
            case Information: {
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
                break;
            }
            case Question:{
                //alertDialogBuilder.setIcon(android.R.drawable);
                break;
            }
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
