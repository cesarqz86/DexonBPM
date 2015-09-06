package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.ActivityListActivity;
import us.dexon.dexonbpm.activity.ActivityOptionsTableActivity;
import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.activity.ListViewActivity;
import us.dexon.dexonbpm.activity.NewTicketActivity;
import us.dexon.dexonbpm.activity.PlantillaListViewActivity;
import us.dexon.dexonbpm.activity.TableActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.activity.WorkflowGridActivity;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.interfaces.IChangePasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.IForgotPasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.ChangePassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ForgotPassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ReopenResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TechnicianResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.AllLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ChangePassRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ForgotPassRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;
import us.dexon.dexonbpm.model.RequestDTO.RelatedActivitiesRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TechnicianRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketByLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.WorkflowRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class ServiceExecuter {

    public class ExecuteLoginService extends AsyncTask<LoginRequestDto, Void, LoginResponseDto> {

        private Context currentContext;
        private LoginRequestDto loginRequestDto;
        private ProgressDialog progressDialog;

        public ExecuteLoginService(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);
            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected LoginResponseDto doInBackground(LoginRequestDto... params) {
            ILoginService loginService = LoginService.getInstance();
            this.loginRequestDto = params[0];
            return loginService.loginUser(this.currentContext, this.loginRequestDto);
        }

        protected void onPostExecute(LoginResponseDto loginResponse) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (loginResponse != null && !CommonValidations.validateEmpty(loginResponse.getErrorMessage())) {

                ConfigurationService.saveUserInfo(this.currentContext, this.loginRequestDto);

                Activity loginActivity = (Activity) this.currentContext;
                Intent incidentActivity = new Intent(loginActivity, IncidentsActivity.class);
                incidentActivity.putExtra("isTech", true);
                loginActivity.startActivity(incidentActivity);
                loginActivity.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                loginActivity.finish();

            } else {

                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_login_error_title, loginResponse.getErrorMessage(), MessageTypeIcon.Error, false);
            }
        }
    }

    public class ExecuteForgotPassService extends AsyncTask<ForgotPassRequestDto, Void, ForgotPassResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;

        public ExecuteForgotPassService(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);
            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected ForgotPassResponseDto doInBackground(ForgotPassRequestDto... params) {
            IForgotPasswordService serviceInstance = ForgotPasswordService.getInstance();
            return serviceInstance.restorePassword(this.currentContext, params[0]);
        }

        protected void onPostExecute(ForgotPassResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null && !CommonValidations.validateEmpty(responseData.getErrorMessage())) {

                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_forgot_success_title, responseData.getResultMessage(), MessageTypeIcon.Information, true);

            } else {

                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, responseData.getErrorMessage(), MessageTypeIcon.Error, false);
            }
        }
    }

    public class ExecuteChangePassService extends AsyncTask<ChangePassRequestDto, Void, ChangePassResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;

        public ExecuteChangePassService(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);
            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected ChangePassResponseDto doInBackground(ChangePassRequestDto... params) {
            IChangePasswordService serviceInstance = ChangePasswordService.getInstance();
            return serviceInstance.changePassword(this.currentContext, params[0]);
        }

        protected void onPostExecute(ChangePassResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null && CommonValidations.validateEqualsValues(responseData.getErrorMessage(), "No error") && responseData.isWasPasswordChanged()) {
                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_change_success_title, responseData.getErrorMessage(), MessageTypeIcon.Information, true);
            } else {
                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_change_error_title, responseData.getErrorMessage(), MessageTypeIcon.Error, false);
            }
        }
    }

    public class ExecuteTicketService extends AsyncTask<TicketsRequestDto, Void, TicketWrapperResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private TicketsRequestDto ticketRequest;

        public ExecuteTicketService(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TicketWrapperResponseDto doInBackground(TicketsRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.ticketRequest = params[0];
            return ticketService.getTicketData(this.currentContext, this.ticketRequest, 1);
        }

        protected void onPostExecute(TicketWrapperResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                IncidentsActivity incidentsActivity = (IncidentsActivity) this.currentContext;
                if (incidentsActivity != null) {
                    incidentsActivity.originalTicketListData = responseData.getTicketArrayData();
                    incidentsActivity.ticketListData = responseData.getTicketArrayData();
                    incidentsActivity.inidentsCallBack(responseData.getTicketArrayData());
                }

                if ((responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) || CommonValidations.validateArrayNullOrEmpty(responseData.getTicketArrayData())) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteTicketTotalService extends AsyncTask<TicketsRequestDto, Void, TicketWrapperResponseDto> {

        private Context currentContext;

        public ExecuteTicketTotalService(Context context) {
            this.currentContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected TicketWrapperResponseDto doInBackground(TicketsRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            return ticketService.getTicketData(this.currentContext, params[0], 1);
        }

        protected void onPostExecute(TicketWrapperResponseDto responseData) {

            if (responseData != null) {

                IncidentsActivity incidentsActivity = (IncidentsActivity) this.currentContext;
                if (incidentsActivity != null && !CommonValidations.validateArrayNullOrEmpty(responseData.getTicketArrayData())) {
                    incidentsActivity.originalTicketListData = responseData.getTicketArrayData();
                    incidentsActivity.ticketListData = responseData.getTicketArrayData();
                    incidentsActivity.inidentsCallBack(responseData.getTicketArrayData());
                }
            }
        }
    }

    public class ExecuteFilter extends AsyncTask<String, Void, String[][]> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private IncidentsActivity incidentsActivity;

        public ExecuteFilter(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);
            this.incidentsActivity = (IncidentsActivity) this.currentContext;
            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected String[][] doInBackground(String... params) {
            if (this.incidentsActivity != null) {
                String[][] originalList = this.incidentsActivity.originalTicketListData == null ? this.incidentsActivity.ticketListData : this.incidentsActivity.originalTicketListData;
                if (this.incidentsActivity.originalTicketListData == null) {
                    this.incidentsActivity.originalTicketListData = originalList;
                }
                String filterText = params[0];
                filterText = filterText == null ? "" : filterText.toLowerCase();
                if (filterText.compareTo("") != 0) {
                    List<String[]> tempResult = new ArrayList<>();
                    int indexSize = 0;
                    for (String[] dataValue : originalList) {
                        if (indexSize != 0 && dataValue != null) {
                            for (String dataTextValue : dataValue) {
                                if (dataTextValue != null && dataTextValue.toLowerCase().contains(filterText)) {
                                    tempResult.add(dataValue);
                                    break;
                                }
                            }
                            indexSize++;
                        } else if (indexSize == 0) {
                            tempResult.add(dataValue);
                            indexSize++;
                        }
                    }
                    String[][] finalResult = new String[tempResult.size()][];
                    indexSize = 0;
                    for (String[] dataValue : tempResult) {
                        finalResult[indexSize] = dataValue;
                        indexSize++;
                    }
                    return finalResult;
                } else {
                    return originalList;
                }
            }
            return null;
        }

        protected void onPostExecute(String[][] responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null && incidentsActivity != null) {
                incidentsActivity.ticketListData = responseData;
                incidentsActivity.inidentsCallBack(responseData);
            }
        }
    }

    public class ExecuteTicketDetailService extends AsyncTask<TicketDetailRequestDto, Void, TicketResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private TicketDetailRequestDto ticketRequest;

        public ExecuteTicketDetailService(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TicketResponseDto doInBackground(TicketDetailRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.ticketRequest = params[0];
            return ticketService.getTicketInfo(this.currentContext, this.ticketRequest, 1);
        }

        protected void onPostExecute(TicketResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                CommonSharedData.OriginalTechnician = responseData.getCurrentTechnician();

                if (this.currentContext instanceof TicketDetail) {
                    TicketDetail ticketDetail = (TicketDetail) this.currentContext;
                    ticketDetail.inidentsCallBack(responseData);
                } else if (this.currentContext instanceof NewTicketActivity) {
                    NewTicketActivity newTicket = (NewTicketActivity) this.currentContext;
                    newTicket.inidentsCallBack(responseData);
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteAllRecordHeaderTree extends AsyncTask<RecordHeaderResquestDto, Void, RecordHeaderResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private RecordHeaderResquestDto recordRequest;

        public ExecuteAllRecordHeaderTree(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected RecordHeaderResponseDto doInBackground(RecordHeaderResquestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.recordRequest = params[0];
            return ticketService.getAllRecordsHeaderTree(this.currentContext, this.recordRequest, 1);
        }

        protected void onPostExecute(RecordHeaderResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                ListViewActivity treeView = (ListViewActivity) this.currentContext;
                if (treeView != null) {
                    treeView.inidentsCallBack(responseData);
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteAllRecordHeaderTable extends AsyncTask<RecordHeaderResquestDto, Void, RecordHeaderResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private RecordHeaderResquestDto recordRequest;

        public ExecuteAllRecordHeaderTable(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected RecordHeaderResponseDto doInBackground(RecordHeaderResquestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.recordRequest = params[0];
            return ticketService.getAllRecordsHeaderTable(this.currentContext, this.recordRequest, 1);
        }

        protected void onPostExecute(RecordHeaderResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                TableActivity tableView = (TableActivity) this.currentContext;
                if (tableView != null) {
                    tableView.inidentsCallBack(responseData.getTableDataList());
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteAllRecordHeaderTable2 extends AsyncTask<RecordHeaderResquestDto, Void, RecordHeaderResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private RecordHeaderResquestDto recordRequest;
        private int position;

        public ExecuteAllRecordHeaderTable2(Context context, int positionData) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);
            this.position = positionData;

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected RecordHeaderResponseDto doInBackground(RecordHeaderResquestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.recordRequest = params[0];
            return ticketService.getAllRecordsHeaderTable(this.currentContext, this.recordRequest, 1, this.position);
        }

        protected void onPostExecute(RecordHeaderResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                ActivityOptionsTableActivity tableView = (ActivityOptionsTableActivity) this.currentContext;
                if (tableView != null) {
                    tableView.inidentsCallBack(responseData.getTableDataList());
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteAutomaticTechnician extends AsyncTask<TechnicianRequestDto, Void, TechnicianResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private TechnicianRequestDto technicianRequest;
        private TicketResponseDto ticketInfo;
        private JsonObject currentTechnician;

        public ExecuteAutomaticTechnician(Context context, TicketResponseDto ticketData, JsonObject technicinaInfo) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);
            this.ticketInfo = ticketData;
            this.currentTechnician = technicinaInfo;
            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TechnicianResponseDto doInBackground(TechnicianRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.technicianRequest = params[0];
            return ticketService.getTechnician(this.currentContext, this.technicianRequest);
        }

        protected void onPostExecute(TechnicianResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                ITicketService ticketService = TicketService.getInstance();

                TicketDetail ticketDetail = null;
                NewTicketActivity newTicket = null;

                if (this.currentContext instanceof TicketDetail) {
                    ticketDetail = (TicketDetail) this.currentContext;
                } else if (this.currentContext instanceof NewTicketActivity) {
                    newTicket = (NewTicketActivity) this.currentContext;
                }

                if (ticketDetail != null) {
                    JsonObject ticketInfoTemp = this.ticketInfo.getTicketInfo();
                    ticketInfoTemp.get("headerInfo").getAsJsonObject().add("current_technician", responseData.getTechnicianInfo());
                    this.ticketInfo = ticketService.convertToTicketData(ticketInfoTemp, R.id.btn_setautomatic_technician, this.currentTechnician);
                    ticketDetail.inidentsCallBack(this.ticketInfo);
                }
                if (newTicket != null) {
                    JsonObject ticketInfoTemp = this.ticketInfo.getTicketInfo();
                    ticketInfoTemp.get("headerInfo").getAsJsonObject().add("current_technician", responseData.getTechnicianInfo());
                    this.ticketInfo = ticketService.convertToTicketData(ticketInfoTemp, R.id.btn_setautomatic_technician, this.currentTechnician);
                    newTicket.inidentsCallBack(this.ticketInfo);
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteReopenTicket extends AsyncTask<ReopenRequestDto, Void, ReopenResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private ReopenRequestDto recordRequest;

        public ExecuteReopenTicket(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected ReopenResponseDto doInBackground(ReopenRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.recordRequest = params[0];
            return ticketService.reopenTicket(this.currentContext, this.recordRequest, 1);
        }

        protected void onPostExecute(ReopenResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                TicketDetail ticketDetail = (TicketDetail) this.currentContext;
                if (ticketDetail != null) {
                    ticketDetail.inidentsCallBack(responseData.getTicketData());
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteReloadTicket extends AsyncTask<ReloadRequestDto, Void, TicketResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private ReloadRequestDto ticketRequest;

        public ExecuteReloadTicket(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TicketResponseDto doInBackground(ReloadRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.ticketRequest = params[0];
            return ticketService.reloadTicket(this.currentContext, this.ticketRequest, 1);
        }

        protected void onPostExecute(TicketResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                } else {

                    CommonSharedData.TicketInfoUpdated = responseData;

                    JsonObject headerInfo = responseData.getTicketInfo().get("headerInfo").getAsJsonObject();
                    Boolean isClosed = headerInfo.get("closureStatus").getAsBoolean();
                    if (isClosed) {
                        Intent workfloIntent = new Intent(this.currentContext, WorkflowGridActivity.class);
                        CommonSharedData.TicketActivity.startActivityForResult(workfloIntent, 0);
                        CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                                R.anim.right_slide_out);
                    }
                }
            }
        }
    }

    public class ExecuteSaveTicket extends AsyncTask<SaveTicketRequestDto, Void, TicketResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private SaveTicketRequestDto ticketRequest;

        public ExecuteSaveTicket(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TicketResponseDto doInBackground(SaveTicketRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.ticketRequest = params[0];
            return ticketService.saveTicket(this.currentContext, this.ticketRequest, 1);
        }

        protected void onPostExecute(TicketResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                CommonSharedData.TicketInfoUpdated = responseData;

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext,
                            R.string.validation_general_error_title,
                            R.string.validation_general_connection_message,
                            MessageTypeIcon.Error,
                            false);
                } else {
                    CommonService.ShowAlertDialog(this.currentContext,
                            R.string.validation_ticket_success_title,
                            R.string.validation_ticket_success_message,
                            MessageTypeIcon.Error,
                            false);
                }
            }
        }
    }

    public class ExecuteAllLayout extends AsyncTask<AllLayoutRequestDto, Void, RecordHeaderResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private AllLayoutRequestDto recordRequest;

        public ExecuteAllLayout(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected RecordHeaderResponseDto doInBackground(AllLayoutRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.recordRequest = params[0];
            return ticketService.getAllLayouts(this.currentContext, this.recordRequest, 1);
        }

        protected void onPostExecute(RecordHeaderResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                PlantillaListViewActivity plantillaView = (PlantillaListViewActivity) this.currentContext;
                if (plantillaView != null) {
                    plantillaView.inidentsCallBack(responseData);
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteTicketByLayout extends AsyncTask<TicketByLayoutRequestDto, Void, TicketResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private TicketByLayoutRequestDto ticketRequest;

        public ExecuteTicketByLayout(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TicketResponseDto doInBackground(TicketByLayoutRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.ticketRequest = params[0];
            return ticketService.getTicketByLayout(this.currentContext, this.ticketRequest, 1);
        }

        protected void onPostExecute(TicketResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                NewTicketActivity newTicket = (NewTicketActivity) this.currentContext;
                newTicket.inidentsCallBack(responseData);

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteLoadWorkflowTable extends AsyncTask<WorkflowRequestDto, Void, RecordHeaderResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private WorkflowRequestDto recordRequest;

        public ExecuteLoadWorkflowTable(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected RecordHeaderResponseDto doInBackground(WorkflowRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.recordRequest = params[0];
            return ticketService.loadWorkflow(this.currentContext, this.recordRequest);
        }

        protected void onPostExecute(RecordHeaderResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                WorkflowGridActivity tableView = (WorkflowGridActivity) this.currentContext;
                if (tableView != null) {
                    tableView.inidentsCallBack(responseData.getTableDataList());
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    public class ExecuteRelatedActivities extends AsyncTask<RelatedActivitiesRequestDto, Void, TicketResponseDto> {

        private Context currentContext;
        private ProgressDialog progressDialog;
        private RelatedActivitiesRequestDto ticketRequest;

        public ExecuteRelatedActivities(Context context) {
            this.currentContext = context;
            this.progressDialog = CommonService.getCustomProgressDialog(this.currentContext);

            //this.progressDialog = new ProgressDialog(this.currentContext);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.show();
        }

        @Override
        protected TicketResponseDto doInBackground(RelatedActivitiesRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            this.ticketRequest = params[0];
            return ticketService.getRelatedActivities(this.currentContext, this.ticketRequest, 1);
        }

        protected void onPostExecute(TicketResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                CommonSharedData.TicketInfoUpdated = responseData;

                ActivityListActivity activityListActivity = (ActivityListActivity) this.currentContext;
                if (activityListActivity != null) {
                    activityListActivity.inidentsCallBack(responseData);
                }

                if (responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty()) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }
}
