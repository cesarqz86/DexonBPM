package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // We have to enable this later.
        LoginRequestDto loginUser = ConfigurationService.getUserInfo(this);
        if (loginUser != null) {
            Intent incidentActivity = new Intent(this, IncidentsActivity.class);
            incidentActivity.putExtra("isTech", true);
            this.startActivity(incidentActivity);
            this.finish();
        }

        this.updateOrientationVisibility();
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
            boolean isLoginEmailValid = CommonValidations.validateEmpty(this, txt_loginemail, loginEmailValue);
            boolean isPasswordValid = CommonValidations.validateEmpty(this, txt_loginpassword, loginPasswordValue);

            if (isLoginEmailValid && isPasswordValid) {

                LoginRequestDto loginData = new LoginRequestDto();
                loginData.setUserName(loginEmailValue);
                loginData.setPassword(loginPasswordValue);
                loginData.setMyGivenPass(ConfigurationService.getConfigurationValue(this, "Serial"));
                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteLoginService loginService = serviceExecuter.new ExecuteLoginService(this);
                loginService.execute(loginData);
            } else {
                CommonService.ShowAlertDialog(this, R.string.validation_general_error_title, R.string.validation_general_error_required, MessageTypeIcon.Error, false);
            }
        } catch (Exception ex) {
            CommonService.ShowAlertDialog(this, R.string.validation_login_error_title, R.string.validation_login_error_invaliduser, MessageTypeIcon.Error, false);
        }
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    private void updateOrientationVisibility() {
        ImageView logo_image = (ImageView) this.findViewById(R.id.logo_image);
        View line_separator = this.findViewById(R.id.line_separator);
        ImageView logo_bottom = (ImageView) this.findViewById(R.id.logo_bottom);

        logo_image.setVisibility(View.INVISIBLE);
        line_separator.setVisibility(View.INVISIBLE);
        logo_bottom.setVisibility(View.INVISIBLE);

        int currentOrientation = this.getResources().getConfiguration().orientation;
        if(currentOrientation == Configuration.ORIENTATION_PORTRAIT){
            logo_image.setVisibility(View.VISIBLE);
            line_separator.setVisibility(View.VISIBLE);
            logo_bottom.setVisibility(View.VISIBLE);
        }
    }
}
