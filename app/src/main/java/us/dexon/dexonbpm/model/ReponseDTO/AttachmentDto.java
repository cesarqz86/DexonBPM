package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;

/**
 * Created by Cesar Quiroz on 10/26/15.
 */
public class AttachmentDto {

    //region Attributes
    private int documentId;
    private String attachmentName;
    private JsonObject attachmentObject;

    //endregion

    //region Properties
    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public JsonObject getAttachmentObject() {
        return attachmentObject;
    }

    public void setAttachmentObject(JsonObject attachmentObject) {
        this.attachmentObject = attachmentObject;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
