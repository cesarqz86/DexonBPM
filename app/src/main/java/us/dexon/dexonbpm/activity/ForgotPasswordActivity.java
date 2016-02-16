package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
        this.updateOrientationVisibility();
    }

    public void btnRecoverPasswordClick(View view) {
        try {

            EditText txt_forgotemail = (EditText) this.findViewById(R.id.txt_forgotemail);
            String loginEmailValue = txt_forgotemail.getText().toString();
            boolean isLoginEmailValid = CommonValidations.validateEmpty(this, txt_forgotemail, loginEmailValue);
            if (isLoginEmailValid) {

                ForgotPassRequestDto forgotData = new ForgotPassRequestDto();
                forgotData.setUserName(loginEmailValue);

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteForgotPassService forgotService = serviceExecuter.new ExecuteForgotPassService(this);
                forgotService.execute(forgotData);
            } else {
                CommonService.ShowAlertDialog(this, R.string.validation_general_error_title, R.string.validation_general_error_required, MessageTypeIcon.Error, false);
            }

        } catch (Exception ex) {
            CommonService.ShowAlertDialog(this, R.string.validation_general_error_title, R.string.validation_forgot_error_genericerror, MessageTypeIcon.Error, false);
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
        RelativeLayout main_container = (RelativeLayout) this.findViewById(R.id.main_container);
        Drawable recoveryImage = this.getResources().getDrawable(R.drawable.recovery_bitmap);

        logo_image.setVisibility(View.INVISIBLE);
        line_separator.setVisibility(View.INVISIBLE);
        logo_bottom.setVisibility(View.INVISIBLE);

        int currentOrientation = this.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            logo_image.setVisibility(View.VISIBLE);
            line_separator.setVisibility(View.VISIBLE);
            logo_bottom.setVisibility(View.VISIBLE);
        }
        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            main_container.setBackground(recoveryImage);
        }
    }
}
