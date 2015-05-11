package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;

/**
 * Created by fuse-andronauta on 4/7/15.
 */
public class ConfigurationService{

    //region Public Methods

    public static String getConfigurationValue (Context context, String configurationName)
    {
        String finalValue = "";

        try {
            InputStream jsonFile = context.getAssets().open("settings.json");
            int size = jsonFile.available();
            byte[] buffer = new byte[size];
            jsonFile.read(buffer);
            jsonFile.close();

            String jsonString = new String(buffer, "UTF-8");

            JSONObject responseObject = new JSONObject(jsonString);
            finalValue = responseObject.get(configurationName).toString();
        }
        catch (Exception ex)
        {
            Log.e("Configuration", ex.getMessage());
        }

        return  finalValue;
    }

    //endregion

    //region Private Methods
    //endregion
}
