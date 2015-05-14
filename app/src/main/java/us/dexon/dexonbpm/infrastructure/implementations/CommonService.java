package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;

/**
 * Created by Cesar Quiroz on 4/11/15.
 */
public class CommonService {

    //region Public Methods
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

    public static void ShowAlertDialog(final Context context, int titleID, int messageID, MessageTypeIcon messageTypeIcon, final boolean closeView) {

        final Activity currentActivity = (Activity) context;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(titleID));
        alertDialogBuilder.setMessage(context.getString(messageID));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (closeView) {
                            currentActivity.finish();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    public static void ShowAlertDialog(Context context, int titleID, String message, MessageTypeIcon messageTypeIcon, final boolean closeView) {

        final Activity currentActivity = (Activity) context;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(titleID));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (closeView) {
                            currentActivity.finish();
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }
    //endregion

    //region Private Methods
    //endregion
}
