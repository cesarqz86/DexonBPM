package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.HomeActivity;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.interfaces.IForgotPasswordService;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.model.ReponseDTO.ForgotPassResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ForgotPassRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class ServiceExecuter {

    public class ExecuteLoginService extends AsyncTask<LoginRequestDto, Void, LoginResponseDto> {

        private Context currentContext;

        public ExecuteLoginService(Context context) {
            this.currentContext = context;
        }

        @Override
        protected LoginResponseDto doInBackground(LoginRequestDto... params) {
            ILoginService loginService = LoginService.getInstance();
            return loginService.loginUser(this.currentContext, params[0]);
        }

        protected void onPostExecute(LoginResponseDto loginResponse) {

            if (loginResponse != null && !CommonValidations.validateEmpty(loginResponse.getErrorMessage())) {

                Activity loginActivity = (Activity) this.currentContext;
                Intent homeIntent = new Intent(loginActivity, HomeActivity.class);
                homeIntent.putExtra("isTech", true);
                loginActivity.startActivity(homeIntent);
                loginActivity.finish();

            } else {

                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_login_error_title, loginResponse.getErrorMessage(), MessageTypeIcon.Error, false);
            }
        }
    }

    public class ExecuteForgotPassService extends AsyncTask<ForgotPassRequestDto, Void, ForgotPassResponseDto> {

        private Context currentContext;

        public ExecuteForgotPassService(Context context) {
            this.currentContext = context;
        }

        @Override
        protected ForgotPassResponseDto doInBackground(ForgotPassRequestDto... params) {
            IForgotPasswordService serviceInstance = ForgotPasswordService.getInstance();
            return serviceInstance.restorePassword(this.currentContext, params[0]);
        }

        protected void onPostExecute(ForgotPassResponseDto responseData) {

            if (responseData != null && !CommonValidations.validateEmpty(responseData.getErrorMessage())) {

                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_forgot_success_title, responseData.getResultMessage(), MessageTypeIcon.Information, true);

            } else {

                CommonService.ShowAlertDialog(this.currentContext, R.string.validation_forgot_error_title, responseData.getErrorMessage(), MessageTypeIcon.Error, false);
            }
        }
    }

}
