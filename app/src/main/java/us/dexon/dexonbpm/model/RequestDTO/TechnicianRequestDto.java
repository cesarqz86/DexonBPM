package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 8/23/15.
 */
public class TechnicianRequestDto {

    //region Attributes
    @SerializedName("incidentHeaderModel")
    private JsonObject headerInfo;
    //endregion

    //region Properties
    public JsonObject getHeaderInfo() {
        return headerInfo;
    }

    public void setHeaderInfo(JsonObject headerInfo) {
        this.headerInfo = headerInfo;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
