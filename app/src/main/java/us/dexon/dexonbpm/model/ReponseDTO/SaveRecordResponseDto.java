package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 9/27/15.
 */
public class SaveRecordResponseDto {

    //region Attributes
    @SerializedName("error")
    private String errorMessage;

    private JsonObject recordObject;
    //endregion

    //region Properties
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public JsonObject getRecordObject() {
        return recordObject;
    }

    public void setRecordObject(JsonObject recordObject) {
        this.recordObject = recordObject;
    }
    //endregion
}
