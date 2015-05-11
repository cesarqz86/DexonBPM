package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public class ForgotPassResponseDto {

    //region Attributes
    @SerializedName("result")
    private String resultMessage;
    @SerializedName("error")
    private String errorMessage;
    //endregion

    //region Properties
    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
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
