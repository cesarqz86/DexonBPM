package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.interfaces.IChangePasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.IForgotPasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.ChangePassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ForgotPassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ChangePassRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ForgotPassRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

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
                    incidentsActivity.inidentsCallBack();
                }

                if ((responseData.getErrorMessage() != null && !responseData.getErrorMessage().isEmpty())) {
                    CommonService.ShowAlertDialog(this.currentContext, R.string.validation_general_error_title, R.string.validation_general_connection_message, MessageTypeIcon.Error, false);
                }
            }
        }
    }

    /*public class ExecuteTicketTotalService extends AsyncTask<TicketsRequestDto, Void, Void> {

        private Context currentContext;

        public ExecuteTicketTotalService(Context context) {
            this.currentContext = context;
        }

        @Override
        protected void onPreExecute() {
            Log.i("GrabadoDB", "Inicio el proceso de guardado de la DB");
        }

        @Override
        protected Void doInBackground(TicketsRequestDto... params) {
            ITicketService ticketService = TicketService.getInstance();
            ticketService.getTicketDataDB(this.currentContext, params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            IncidentsActivity incidentsActivity = (IncidentsActivity) this.currentContext;
            if (incidentsActivity != null) {
                IDexonDatabaseWrapper databaseWrapper = DexonDatabaseWrapper.getInstance();
                //incidentsActivity.ticketListData = databaseWrapper.getTicketData(null, null);
                incidentsActivity.inidentsCallBack();
            }

            Log.i("GrabadoDB", "Finalizo el proceso de guardado de la DB");
        }
    }*/

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
                incidentsActivity.inidentsCallBack();
            }
        }
    }
}
