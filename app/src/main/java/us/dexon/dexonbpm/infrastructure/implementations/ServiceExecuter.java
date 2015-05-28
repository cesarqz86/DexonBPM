package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.interfaces.IChangePasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.IForgotPasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.ChangePassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ForgotPassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
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
            return ticketService.getTicketData(this.currentContext, params[0], 1);
        }

        protected void onPostExecute(TicketWrapperResponseDto responseData) {

            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (responseData != null) {

                IncidentsActivity incidentsActivity = (IncidentsActivity) this.currentContext;

                if (responseData.getErrorMessage() == null || (responseData.getErrorMessage() != null && responseData.getErrorMessage().isEmpty())) {
                    if (incidentsActivity != null) {
                        incidentsActivity.ticketListData = responseData.getTicketArrayData();
                        incidentsActivity.inidentsCallBack();
                    }
                } else {
                    if (incidentsActivity != null) {
                        incidentsActivity.ticketListData = null;
                        incidentsActivity.inidentsCallBack();
                    }
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

                if (responseData.getErrorMessage() == null || (responseData.getErrorMessage() != null && responseData.getErrorMessage().isEmpty())) {
                    if (incidentsActivity != null) {
                        incidentsActivity.ticketListData = responseData.getTicketArrayData();
                        incidentsActivity.inidentsCallBack();
                    }
                }
            }
        }
    }
}
