package us.dexon.dexonbpm.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import us.dexon.dexonbpm.R;

public class ConfirmPasswordActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
    }

    public void btnChangePasswordConfirmClick(View view) {
        this.finish();
    }
}
