package us.dexon.dexonbpm.model.ReponseDTO;

import java.util.List;

/**
 * Created by Cesar Quiroz on 8/22/15.
 */
public class TreeDataDto {

    //region Attributes
    private String elementId;
    private String elementName;
    private String parentId;
    private String headerId;
    //private List<TreeDataDto> childList;
    //endregion

    //region Properties
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /*public List<TreeDataDto> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeDataDto> childList) {
        this.childList = childList;
    }*/

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
