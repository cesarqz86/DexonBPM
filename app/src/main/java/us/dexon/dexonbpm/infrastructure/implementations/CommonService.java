package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;

/**
 * Created by Cesar Quiroz on 4/11/15.
 */
public class CommonService {

    public static final int ROW_TYPE_HEADER = 1;
    public static final int ROW_TYPE_NORMAL_WHITE = 2;
    public static final int ROW_TYPE_NORMAL_GRAY = 3;


    //region Public Methods
    public static void AddRowToTable(Activity context, TableLayout tableLayout,
                                     ArrayList<TicketsResponseDto> ticketListData) {

        if (ticketListData != null && !ticketListData.isEmpty()) {

            Object[] titles = ticketListData.get(0).getTicketDataList().keySet().toArray();

            TableRow rowToAddH = new TableRow(context);
            TableRow.LayoutParams rowParamsH = new TableRow.LayoutParams();
            rowParamsH.height = TableLayout.LayoutParams.WRAP_CONTENT;
            rowParamsH.width = TableLayout.LayoutParams.WRAP_CONTENT;
            /*rowParamsH.height = TableLayout.LayoutParams.WRAP_CONTENT;
            rowParamsH.width = TableLayout.LayoutParams.WRAP_CONTENT;*/

            for (int i = -1; i < titles.length; i++) {
                TextView txtColumn = (TextView) context.getLayoutInflater().inflate(R.layout.row_header, null);
                txtColumn.setText(i == -1 ? "TICKET" : titles[i].toString());
                rowToAddH.addView(txtColumn, rowParamsH);
            }
            tableLayout.addView(rowToAddH, rowParamsH);

            for (int k = 0; k < ticketListData.size(); k++) {
                TableRow rowToAdd = new TableRow(context);
                TicketsResponseDto ticket = ticketListData.get(k);
                for (int l = -1; l < ticket.getTicketDataList().size(); l++) {
                    TextView txtColumn = (TextView) context.getLayoutInflater()
                            .inflate(((k & 1) == 0) ? R.layout.row_odd : R.layout.row_even, null);
                    txtColumn.setText(l == -1 ? ticket.getTicketID() :
                            ticket.getTicketDataList().get(titles[l]).toString());
                    rowToAdd.addView(txtColumn, rowParamsH);
                }
                tableLayout.addView(rowToAdd, rowParamsH);
            }
        }
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
        TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setGravity(Gravity.CENTER);
        }
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
        TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setGravity(Gravity.CENTER);
        }
    }

    public static ProgressDialog getCustomProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            //dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        //dialog.setContentView(android.support.v7.appcompat.R.drawable);
        dialog.setMessage(context.getString(R.string.general_loading));
        return dialog;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static String convertToDexonDate(String dateString){
        if (CommonValidations.validateEmpty(dateString)) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
            } catch (Exception ex) {
                Log.e("Converting Date: ", ex.getMessage(), ex);
            }
        }
        return dateString;
    }
    //endregion

    //region Private Methods
    //endregion
}
