package us.dexon.dexonbpm.infrastructure.implementations;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public final class CommonValidations {

    //region Public Methods
    public static boolean validateEmpty(String dataToValidate) {
        return dataToValidate != null && !dataToValidate.isEmpty();
    }
    //endregion
}
