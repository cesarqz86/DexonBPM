package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.model.RequestDTO.ChangePassRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

public class ChangePasswordActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    public void btnChangePasswordClick(View view) {

        try {

            EditText txt_currentpassword = (EditText) this.findViewById(R.id.txt_currentpassword);
            EditText txt_newpassword = (EditText) this.findViewById(R.id.txt_newpassword);
            EditText txt_confirmpassword = (EditText) this.findViewById(R.id.txt_confirmpassword);

            String currentPasswordValue = txt_currentpassword.getText().toString();
            String newPasswordValue = txt_newpassword.getText().toString();
            String confirmPasswordValue = txt_confirmpassword.getText().toString();

            boolean isCurrentPassValid = CommonValidations.validateEmpty(this, txt_currentpassword, currentPasswordValue);
            boolean isNewPassValid = CommonValidations.validateEmpty(this, txt_newpassword, newPasswordValue);
            boolean isConfirmPassValid = CommonValidations.validateEmpty(this, txt_confirmpassword, confirmPasswordValue);
            boolean isSamePasswordValid = CommonValidations.validateEqualsValues(this, txt_confirmpassword, newPasswordValue, confirmPasswordValue);

            LoginRequestDto loginObject = ConfigurationService.getUserInfo(this);

            if (isCurrentPassValid && isNewPassValid && isConfirmPassValid && isSamePasswordValid && loginObject != null) {

                ChangePassRequestDto changePassData = new ChangePassRequestDto();
                changePassData.setUserName(loginObject.getUserName());
                changePassData.setCurrentPassword(currentPasswordValue);
                changePassData.setNewPassword(newPasswordValue);
                changePassData.setConfirmPassword(confirmPasswordValue);

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteChangePassService changeService = serviceExecuter.new ExecuteChangePassService(this);
                changeService.execute(changePassData);
            } else {
                CommonService.ShowAlertDialog(this, R.string.validation_general_error_title, R.string.validation_general_error_required, MessageTypeIcon.Error, false);
            }

        } catch (Exception ex) {
            CommonService.ShowAlertDialog(this, R.string.validation_change_error_title, R.string.validation_change_error_title, MessageTypeIcon.Error, false);
        }

    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }
}
