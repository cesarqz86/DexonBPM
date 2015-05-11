package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public class ForgotPassRequestDto {

    //region Attributes
    @SerializedName("userName")
    private String userName;
    //endregion

    //region Properties
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
