package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;

import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

/**
 * Created by fuse-andronauta on 4/7/15.
 */
public class ConfigurationService {

    //region Attributes
    private static final String USER_SETTINGS = "DEXONUSERINFO";
    private static final String USER_SETTINGS_VALUE = "LoginInfo";
    //endregion

    //region Public Methods

    public static String getConfigurationValue(Context context, String configurationName) {
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
        } catch (Exception ex) {
            Log.e("Configuration", ex.getMessage());
        }

        return finalValue;
    }

    public static void saveUserInfo(Context context, LoginRequestDto loginData) {
        Gson gsonSerializer = new Gson();
        if (loginData != null) {
            String dataToStore = gsonSerializer.toJson(loginData);
            SharedPreferences deviceSettings = context.getSharedPreferences(USER_SETTINGS, 0);
            SharedPreferences.Editor editorSettings = deviceSettings.edit();
            editorSettings.putString(USER_SETTINGS_VALUE, dataToStore);
            editorSettings.commit();
        }
    }

    public static LoginRequestDto getUserInfo(Context context) {
        LoginRequestDto finalResult = null;
        Gson gsonSerializer = new Gson();
        SharedPreferences deviceSettings = context.getSharedPreferences(USER_SETTINGS, 0);
        String dataToStore = deviceSettings.getString(USER_SETTINGS_VALUE, null);
        if (dataToStore != null && !dataToStore.isEmpty()) {
            finalResult = gsonSerializer.fromJson(dataToStore, LoginRequestDto.class);
        }
        return finalResult;
    }

    //endregion

    //region Private Methods
    //endregion
}
