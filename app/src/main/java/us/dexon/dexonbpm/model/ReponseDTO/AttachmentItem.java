package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;

/**
 * Created by Cesar Quiroz on 1/11/16.
 */
public class AttachmentItem {

    //region Attributes
    private String fileName;
    private String fileExtension;
    private String attachmentName;
    private String attachmentData;
    //endregion

    //region Properties
    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentData() {
        return attachmentData;
    }

    public void setAttachmentData(String attachmentData) {
        this.attachmentData = attachmentData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
