package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    //endregion
}
