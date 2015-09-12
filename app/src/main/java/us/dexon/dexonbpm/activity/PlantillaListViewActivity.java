package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.PlantillaAdapter;
import us.dexon.dexonbpm.adapters.TreeAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;
import us.dexon.dexonbpm.model.RequestDTO.AllLayoutRequestDto;

public class PlantillaListViewActivity extends FragmentActivity {

    private String keyToSearch;
    private String fieldKey;
    private Map<String, List<TreeDataDto>> originalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantilla_list_view);

        Intent currentIntent = this.getIntent();
        this.keyToSearch = currentIntent.getStringExtra("keyToSearch");
        this.fieldKey = currentIntent.getStringExtra("fieldKey");

        if (CommonSharedData.TreeData == null) {
            IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
            dexonDatabase.setContext(this);

            LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

            AllLayoutRequestDto allLayoutRequestDto = new AllLayoutRequestDto();
            allLayoutRequestDto.setLoggedUser(loggedUser);

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteAllLayout getAllLayouts = serviceExecuter.new ExecuteAllLayout(this);
            getAllLayouts.execute(allLayoutRequestDto);
        }
        else
        {
            this.inidentsCallBack(CommonSharedData.TreeData);
        }

        final EditText findDaemon = (EditText) findViewById(R.id.search_field);
        findDaemon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String filterValue = v.getText().toString();
                    if (!CommonValidations.validateEmpty(filterValue)) {
                        drawData(originalData);
                    } else {
                        Map<String, List<TreeDataDto>> filteredList = new HashMap<String, List<TreeDataDto>>();
                        for (Map.Entry<String, List<TreeDataDto>> itemTree : originalData.entrySet()) {
                            List<TreeDataDto> filteredDataList = new ArrayList<TreeDataDto>();
                            for (TreeDataDto itemTreeDetail : itemTree.getValue()) {
                                String itemId = CommonValidations.validateEmpty(itemTreeDetail.getElementId()) ? itemTreeDetail.getElementId() : "";
                                String itemValue = CommonValidations.validateEmpty(itemTreeDetail.getElementName()) ? itemTreeDetail.getElementName() : "";
                                if (CommonValidations.validateContains(itemId, filterValue) || CommonValidations.validateContains(itemValue, filterValue)) {
                                    filteredDataList.add(itemTreeDetail);
                                }
                            }
                            filteredList.put(itemTree.getKey(), filteredDataList);
                        }
                        drawData(filteredList);
                    }
                }
                return false;
            }
        });
    }

    public void inidentsCallBack(RecordHeaderResponseDto responseDto) {

        if (CommonSharedData.TreeData == null) {
            CommonSharedData.TreeData = responseDto;
        }

        if (this.originalData == null) {
            this.originalData = responseDto.getDataList();
        }

        this.drawData(responseDto.getDataList());
    }

    private void drawData(Map<String, List<TreeDataDto>> dataList) {

        ListView lstvw_tree_detail = (ListView) this.findViewById(R.id.lstvw_tree_detail);

        if (!CommonValidations.validateEmpty(this.keyToSearch)) {
            this.keyToSearch = "-1";
        }

        PlantillaAdapter detailAdapter = new PlantillaAdapter(this,
                dataList,
                this.keyToSearch,
                CommonSharedData.TicketInfo,
                this.fieldKey);
        lstvw_tree_detail.setAdapter(detailAdapter);
    }
}
