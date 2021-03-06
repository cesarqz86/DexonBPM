package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Cesar Quiroz on 8/22/15.
 */
public class RecordHeaderResponseDto {

    //region Attributes
    @SerializedName("result")
    private String resultMessage;
    @SerializedName("error")
    private String errorMessage;
    private Map<String, List<TreeDataDto>> dataList;
    private String[][] tableDataList;
    private String fieldKeyName;
    private String fieldKeyId;
    //endregion

    //region Properties
    public Map<String, List<TreeDataDto>> getDataList() {
        return dataList;
    }

    public void setDataList(Map<String, List<TreeDataDto>> dataList) {
        this.dataList = dataList;
    }

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

    public String[][] getTableDataList() {
        return tableDataList;
    }

    public void setTableDataList(String[][] tableDataList) {
        this.tableDataList = tableDataList;
    }

    public String getFieldKeyName() {
        return fieldKeyName;
    }

    public void setFieldKeyName(String fieldKeyName) {
        this.fieldKeyName = fieldKeyName;
    }

    public String getFieldKeyId() {
        return fieldKeyId;
    }

    public void setFieldKeyId(String fieldKeyId) {
        this.fieldKeyId = fieldKeyId;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
