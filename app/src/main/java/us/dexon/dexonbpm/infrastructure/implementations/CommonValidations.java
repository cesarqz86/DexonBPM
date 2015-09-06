package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import us.dexon.dexonbpm.R;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public final class CommonValidations {

    //region Public Methods
    public static boolean validateEmpty(String dataToValidate) {
        return dataToValidate != null && !dataToValidate.isEmpty();
    }

    //region Public Methods
    public static boolean validateEmpty(Context context, EditText editText, String dataToValidate) {
        boolean finalResult = dataToValidate != null && !dataToValidate.isEmpty();
        if (!finalResult) {
            editText.setError(context.getString(R.string.validation_general_error_fieldrequired));
        }
        return finalResult;
    }

    public static boolean validateEqualsValues(String originalValue, String newValue) {
        return originalValue != null && newValue != null && originalValue.equalsIgnoreCase(newValue);
    }

    public static boolean validateEqualsValues(Context context, EditText editText, String originalValue, String newValue) {
        boolean finalResult = originalValue != null && newValue != null && originalValue.equalsIgnoreCase(newValue);
        if (!finalResult) {
            editText.setError(context.getString(R.string.validation_change_error_notequals));
        }
        return finalResult;
    }

    public static boolean validateConnection(Context context) {
        boolean finalResult;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        finalResult = networkInfo != null && networkInfo.isConnected();
        return finalResult;
    }

    public static boolean validateArrayNullOrEmpty(String[][] arrayData) {
        boolean finalResult = false;
        if (arrayData != null) {
            if (arrayData.length <= 0) {
                finalResult = true;
            } else {
                for (String[] internalData : arrayData) {
                    if (internalData == null || (internalData != null && internalData.length <= 0)) {
                        finalResult = true;
                        break;
                    }
                }
            }
        } else {
            finalResult = true;
        }
        return finalResult;
    }

    public static boolean validateContains(String originalValue, String filterValue) {
        return org.apache.commons.lang3.StringUtils.containsIgnoreCase(originalValue, filterValue);
    }

    public static boolean validateEqualsIgnoreCase(String firstValue, String secondValue) {
        return org.apache.commons.lang3.StringUtils.equalsIgnoreCase(firstValue, secondValue);
    }
    //endregion
}
