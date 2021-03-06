package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public class ChangePassRequestDto {

    //region Attributes
    @SerializedName("userName")
    private String userName;

    @SerializedName("password")
    private String currentPassword;

    @SerializedName("newpassword")
    private String newPassword;

    @Expose(serialize = false, deserialize = false)
    private String confirmPassword;
    //endregion

    //region Properties
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
