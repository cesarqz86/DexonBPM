package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.LoginService;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void btnForgotpassClick(View view) {
        Intent forgotPassIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPassIntent);
        overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
    }

    public void btnLoginClick(View view) {
        try {

            EditText txt_loginemail = (EditText) this.findViewById(R.id.txt_loginemail);
            EditText txt_loginpassword = (EditText) this.findViewById(R.id.txt_loginpassword);
            String loginEmailValue = txt_loginemail.getText().toString();
            String loginPasswordValue = txt_loginpassword.getText().toString();
            boolean isLoginEmailValid = CommonValidations.validateEmpty(loginEmailValue);
            boolean isPasswordValid = CommonValidations.validateEmpty(loginPasswordValue);

            if (isLoginEmailValid && isPasswordValid) {

                LoginRequestDto loginData = new LoginRequestDto();
                loginData.setUserName(loginEmailValue);
                loginData.setPassword(loginPasswordValue);
                loginData.setMyGivenPass(ConfigurationService.getConfigurationValue(this, "Serial"));
                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteLoginService loginService = serviceExecuter.new ExecuteLoginService(this);
                loginService.wait();
                LoginResponseDto loginResponse = loginService.execute(loginData).get();

                if (loginResponse != null && CommonValidations.validateEmpty(loginResponse.getErrorMessage())) {

                    Intent homeIntent = new Intent(this, HomeActivity.class);
                    homeIntent.putExtra("isTech", true);
                    this.startActivity(homeIntent);
                    this.finish();
                } else {
                    CommonService.ShowAlertDialog(this, R.string.validation_login_error_title, loginResponse.getErrorMessage(), MessageTypeIcon.Error);
                }
            }
        } catch (Exception ex) {
            CommonService.ShowAlertDialog(this, R.string.validation_login_error_title, R.string.validation_login_error_invaliduser, MessageTypeIcon.Error);
        }
    }
}
