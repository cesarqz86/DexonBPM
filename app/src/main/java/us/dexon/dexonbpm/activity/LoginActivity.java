package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.HttpService;
import us.dexon.dexonbpm.model.LoginDto;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnForgotpassClick(View view) {
        Intent forgotPassIntent = new Intent(this, ForgotPasswordActivity.class);
        this.startActivity(forgotPassIntent);
    }

    public void btnLoginClick(View view) {

        EditText txt_loginemail = (EditText) this.findViewById(R.id.txt_loginemail);
        EditText txt_loginpassword = (EditText) this.findViewById(R.id.txt_loginpassword);
        LoginDto logintData = new LoginDto();
        logintData.setUserName(txt_loginemail.getText().toString());
        logintData.setPassword(txt_loginpassword.getText().toString());
        logintData.setMyGivenPass(ConfigurationService.getConfigurationValue(this, "Serial"));
        /*String dataResult = HttpService.CallPost(this, "URLLogon", logintData);
        Toast.makeText(this, dataResult, Toast.LENGTH_LONG).show();*/

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("isTech", true);
        //CommonService.ShowAlertDialog(this, R.string.validation_login_error_title, R.string.validation_login_error_invaliduser, MessageTypeIcon.Error);
        this.startActivity(homeIntent);
        this.finish();
    }
}
