package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 8/23/15.
 */
public class TechnicianResponseDto {

    //region Attributes
    @SerializedName("result")
    private String resultMessage;
    @SerializedName("error")
    private String errorMessage;
    private JsonObject technicianInfo;
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

    public JsonObject getTechnicianInfo() {
        return technicianInfo;
    }

    public void setTechnicianInfo(JsonObject technicianInfo) {
        this.technicianInfo = technicianInfo;
    }

    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
