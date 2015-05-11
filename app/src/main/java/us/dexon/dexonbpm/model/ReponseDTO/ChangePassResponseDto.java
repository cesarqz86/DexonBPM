package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public class ChangePassResponseDto {

    //region Attributes
    @SerializedName("changed")
    private boolean wasPasswordChanged;

    @SerializedName("error")
    private String errorMessage;
    //endregion

    //region Properties
    public boolean isWasPasswordChanged() {
        return wasPasswordChanged;
    }

    public void setWasPasswordChanged(boolean wasPasswordChanged) {
        this.wasPasswordChanged = wasPasswordChanged;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
