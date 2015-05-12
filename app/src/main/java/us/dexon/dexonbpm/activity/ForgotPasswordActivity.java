package us.dexon.dexonbpm.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.model.RequestDTO.ForgotPassRequestDto;

public class ForgotPasswordActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void btnRecoverPasswordClick(View view) {
        try {

            EditText txt_forgotemail = (EditText) this.findViewById(R.id.txt_forgotemail);
            String loginEmailValue = txt_forgotemail.getText().toString();
            boolean isLoginEmailValid = CommonValidations.validateEmpty(loginEmailValue);
            if (isLoginEmailValid) {

                ForgotPassRequestDto forgotData = new ForgotPassRequestDto();
                forgotData.setUserName(loginEmailValue);

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteForgotPassService forgotService = serviceExecuter.new ExecuteForgotPassService(this);
                forgotService.execute(forgotData);
            }

        } catch (Exception ex) {
            CommonService.ShowAlertDialog(this, R.string.validation_forgot_error_title, R.string.validation_forgot_error_genericerror, MessageTypeIcon.Error, false);
        }

    }
}
