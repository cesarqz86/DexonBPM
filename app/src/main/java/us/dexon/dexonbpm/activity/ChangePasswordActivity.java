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
import us.dexon.dexonbpm.model.RequestDTO.ChangePassRequestDto;

public class ChangePasswordActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    public void btnChangePasswordClick(View view){

        try {

            EditText txt_currentpassword = (EditText) this.findViewById(R.id.txt_currentpassword);
            EditText txt_newpassword = (EditText) this.findViewById(R.id.txt_newpassword);
            EditText txt_confirmpassword = (EditText) this.findViewById(R.id.txt_confirmpassword);

            String currentPasswordValue = txt_currentpassword.getText().toString();
            String newPasswordValue = txt_newpassword.getText().toString();
            String confirmPasswordValue = txt_confirmpassword.getText().toString();

            boolean isCurrentPassValid = CommonValidations.validateEmpty(currentPasswordValue);
            boolean isNewPassValid = CommonValidations.validateEmpty(newPasswordValue);
            boolean isConfirmPassValid = CommonValidations.validateEmpty(confirmPasswordValue);
            boolean isSamePasswordValid = CommonValidations.validateEqualsValues(newPasswordValue, confirmPasswordValue);

            if (isCurrentPassValid && isNewPassValid && isConfirmPassValid && isSamePasswordValid) {

                ChangePassRequestDto changePassData = new ChangePassRequestDto();
                changePassData.setUserName("");
                changePassData.setCurrentPassword(currentPasswordValue);
                changePassData.setNewPassword(newPasswordValue);
                changePassData.setConfirmPassword(confirmPasswordValue);

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteChangePassService changeService = serviceExecuter.new ExecuteChangePassService(this);
                changeService.execute(changePassData);
            }

        } catch (Exception ex) {
            CommonService.ShowAlertDialog(this, R.string.validation_change_error_title, R.string.validation_change_error_title, MessageTypeIcon.Error, false);
        }

    }
}
