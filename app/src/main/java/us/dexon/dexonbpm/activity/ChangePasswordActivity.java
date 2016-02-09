package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
        this.SetConfiguredColors();
        this.updateOrientationVisibility();
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

    private void SetConfiguredColors() {
        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        /*Button btn_changepass = (Button) this.findViewById(R.id.btn_changepass);

        LayerDrawable blue_btn_style_state_enabled = (LayerDrawable) this.getResources().getDrawable(R.drawable.blue_btn_style_state_enabled);
        LayerDrawable blue_btn_style_state_pressed = (LayerDrawable) this.getResources().getDrawable(R.drawable.blue_btn_style_state_pressed);
        LayerDrawable blue_btn_style_shadow = (LayerDrawable) this.getResources().getDrawable(R.drawable.blue_btn_style_shadow);

        Drawable firstDrawable = blue_btn_style_state_enabled.getDrawable(0);
        firstDrawable.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        blue_btn_style_state_enabled.setDrawableByLayerId(0, firstDrawable);

        //blue_btn_style_state_enabled.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        //blue_btn_style_state_pressed.setColorFilter(secondaryColor, PorterDuff.Mode.SRC_ATOP);

        StateListDrawable testDrawable = new StateListDrawable();
        testDrawable.addState(new int[]{android.R.attr.state_enabled}, blue_btn_style_state_enabled);
        testDrawable.addState(new int[]{android.R.attr.state_pressed}, blue_btn_style_state_pressed);
        testDrawable.addState(new int[]{}, blue_btn_style_shadow);

        btn_changepass.setBackground(testDrawable);*/

    }

    private void updateOrientationVisibility() {
        ImageView logo_image = (ImageView) this.findViewById(R.id.logo_image);
        View line_separator = this.findViewById(R.id.line_separator);
        ImageView logo_bottom = (ImageView) this.findViewById(R.id.logo_bottom);

        logo_image.setVisibility(View.INVISIBLE);
        line_separator.setVisibility(View.INVISIBLE);
        logo_bottom.setVisibility(View.INVISIBLE);

        int currentOrientation = this.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            logo_image.setVisibility(View.VISIBLE);
            line_separator.setVisibility(View.VISIBLE);
            logo_bottom.setVisibility(View.VISIBLE);
        }
    }
}
