package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import inqbarna.tablefixheaders.TableFixHeaders;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TableAdapter;
import us.dexon.dexonbpm.adapters.TablePlantillaAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;

public class PlantillaTableActivity extends FragmentActivity {


    private String keyToSearch;
    private String fieldKey;
    private String[][] originalData;
    private TablePlantillaAdapter matrixTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantilla_table);

        Intent currentIntent = this.getIntent();
        this.keyToSearch = currentIntent.getStringExtra("keyToSearch");
        this.fieldKey = currentIntent.getStringExtra("fieldKey");

        if (CommonSharedData.TreeData != null) {

            this.originalData = CommonSharedData.TreeData.getTableDataList();
            this.drawData(this.originalData);

            final EditText findDaemon = (EditText) findViewById(R.id.search_field);
            findDaemon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String filterValue = v.getText().toString();
                        if (!CommonValidations.validateEmpty(filterValue)) {
                            drawData(originalData);
                        } else {
                            String[][] filteredList = null;
                            List<String[]> filteredTempList = new ArrayList<String[]>();
                            int count = 0;
                            for (String[] itemTree : originalData) {
                                if (count == 0) {
                                    filteredTempList.add(itemTree);
                                } else {
                                    for (String itemTreeDetail : itemTree) {
                                        if (CommonValidations.validateContains(itemTreeDetail, filterValue)) {
                                            filteredTempList.add(itemTree);
                                            break;
                                        }
                                    }
                                }
                                count++;
                            }
                            filteredList = new String[filteredTempList.size()][];
                            count = 0;
                            for (String[] itemTree : filteredTempList) {
                                filteredList[count] = itemTree;
                                count++;
                            }
                            drawData(filteredList);
                        }
                    }
                    return false;
                }
            });
        }
    }

    private void drawData(String[][] dataList) {
        int indexColumnID = -1;
        if (dataList.length > 0) {
            int tempIndex = 0;
            for (String columnData : dataList[0]) {
                if (CommonValidations.validateEmpty(columnData)) {
                    indexColumnID = tempIndex;
                    break;
                }
                tempIndex++;
            }
        }
        TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table_container);
        this.matrixTableAdapter = new TablePlantillaAdapter(this, dataList, indexColumnID, this.fieldKey);
        tableFixHeaders.setAdapter(this.matrixTableAdapter);
    }
}
