package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.LoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;


public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void btnForgotpassClick(View view) {
        Intent forgotPassIntent = new Intent(this, ForgotPasswordActivity.class);
        this.startActivity(forgotPassIntent);
    }

    public void btnLoginClick(View view) {

        EditText txt_loginemail = (EditText) this.findViewById(R.id.txt_loginemail);
        EditText txt_loginpassword = (EditText) this.findViewById(R.id.txt_loginpassword);
        LoginRequestDto loginData = new LoginRequestDto();
        loginData.setUserName(txt_loginemail.getText().toString());
        loginData.setPassword(txt_loginpassword.getText().toString());
        loginData.setMyGivenPass(ConfigurationService.getConfigurationValue(this, "Serial"));
        ILoginService loginService = LoginService.getInstance();
        loginService.loginUser(this, loginData);

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("isTech", true);
        //CommonService.ShowAlertDialog(this, R.string.validation_login_error_title, R.string.validation_login_error_invaliduser, MessageTypeIcon.Error);
        this.startActivity(homeIntent);
        this.finish();
    }
}
