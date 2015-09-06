package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;

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

    public static int convertSpToPixel(float sp, Context context) {
        int result = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.getResources().getDisplayMetrics());
        return result;
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

    public static void drawTicket(TicketResponseDto responseDto, Context context) {
        Activity currentActivity = (Activity) context;
        LinearLayout lstvw_ticketdetail = (LinearLayout) currentActivity.findViewById(R.id.lstvw_ticketdetail);
        lstvw_ticketdetail.removeAllViews();

        LayoutInflater inflater = currentActivity.getLayoutInflater();
        if (responseDto != null && responseDto.getDataList() != null && responseDto.getDataList().size() > 0) {
            for (TicketDetailDataDto itemDetail : responseDto.getDataList()) {
                View rowView = inflater.inflate(R.layout.item_detailticket, null);
                RenderControlType controlType = itemDetail.getFieldType();
                switch (controlType) {
                    case DXControlsTree: {
                        rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        if (responseDto.getIsOpen() && responseDto.getIsEditable()) {
                            rowView.setOnClickListener(new DexonListeners.ListViewClickListener(
                                    context,
                                    itemDetail.getFieldSonData(),
                                    itemDetail.getFieldKey()));
                        }
                        break;
                    }
                    case DXControlsGrid: {
                        rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        if (responseDto.getIsOpen() && responseDto.getIsEditable()) {
                            rowView.setOnClickListener(new DexonListeners.TableClickListener(
                                    context,
                                    itemDetail.getFieldSonData(),
                                    itemDetail.getFieldKey()));
                        }
                        break;
                    }
                    case DXControlsDate: {
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        String dateString = itemDetail.getFieldValue();
                        if (CommonValidations.validateEmpty(dateString)) {
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                                dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                            } catch (Exception ex) {
                                Log.e("Converting Date: ", ex.getMessage(), ex);
                            }
                        }

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(dateString);
                        break;
                    }
                    case DXControlsGridWithOptions: {
                        rowView = inflater.inflate(R.layout.item_detailticket_technician, null);

                        LinearLayout linear_select_technician = (LinearLayout) rowView.findViewById(R.id.linear_select_technician);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());

                        RadioButton btn_setmanual_technician = (RadioButton) rowView.findViewById(R.id.btn_setmanual_technician);
                        RadioButton btn_setautomatic_technician = (RadioButton) rowView.findViewById(R.id.btn_setautomatic_technician);
                        RadioButton btn_settome_technician = (RadioButton) rowView.findViewById(R.id.btn_settome_technician);

                        if (responseDto.getIsOpen() && responseDto.getIsEditable()) {
                            DexonListeners.TechnicianClickListener technicianClickListener = new DexonListeners.TechnicianClickListener(
                                    context,
                                    responseDto.getCurrentTechnician(),
                                    responseDto);
                            btn_setmanual_technician.setOnClickListener(technicianClickListener);
                            btn_setautomatic_technician.setOnClickListener(technicianClickListener);
                            btn_settome_technician.setOnClickListener(technicianClickListener);
                        }

                        switch (responseDto.getTechnicianSelected()) {
                            case R.id.btn_setmanual_technician: {
                                btn_setmanual_technician.setChecked(true);
                                txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                        0, //left
                                        0, //top
                                        R.drawable.ic_arrow, //right
                                        0);//bottom
                                if (responseDto.getIsOpen() && responseDto.getIsEditable()) {
                                    linear_select_technician.setOnClickListener(new DexonListeners.TableClickListener(
                                            context,
                                            itemDetail.getFieldSonData(),
                                            itemDetail.getFieldKey()));
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
                    case DXControlsMultiline: {
                        rowView = inflater.inflate(R.layout.item_detailticket_text_multi, null);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        //txt_fieldvalue.setMovementMethod(new ScrollingMovementMethod());
                        break;
                    }
                    default: {
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        break;
                    }
                }
                lstvw_ticketdetail.addView(rowView);
            }
        }
    }

    public static void saveTicket(Context context)
    {
        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(context);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        if (CommonSharedData.TicketInfoUpdated == null) {
            CommonSharedData.TicketInfoUpdated = CommonSharedData.TicketInfo;
        }

        JsonObject saveTicketInfo = CommonSharedData.TicketInfoUpdated.getTicketInfo();
        JsonObject headerInfo = CommonSharedData.TicketInfo.getTicketInfo().get("headerInfo").getAsJsonObject();
        saveTicketInfo.add("headerInfo", headerInfo);

        SaveTicketRequestDto ticketData = new SaveTicketRequestDto();
        ticketData.setLoggedUser(loggedUser);
        ticketData.setTicketInfo(saveTicketInfo);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteSaveTicket saveTicketService = serviceExecuter.new ExecuteSaveTicket(context);
        saveTicketService.execute(ticketData);
    }
    //endregion

    //region Private Methods
    //endregion
}
