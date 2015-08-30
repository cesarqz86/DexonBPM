package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ListView;

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
import us.dexon.dexonbpm.model.RequestDTO.AllLayoutRequestDto;

public class PlantillaListViewActivity extends FragmentActivity {

    private String keyToSearch;
    private String fieldKey;

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
    }

    public void inidentsCallBack(RecordHeaderResponseDto responseDto) {

        if (CommonSharedData.TreeData == null) {
            CommonSharedData.TreeData = responseDto;
        }

        ListView lstvw_tree_detail = (ListView) this.findViewById(R.id.lstvw_tree_detail);

        if (!CommonValidations.validateEmpty(this.keyToSearch)) {
            this.keyToSearch = "-1";
        }

        PlantillaAdapter detailAdapter = new PlantillaAdapter(this,
                responseDto.getDataList(),
                this.keyToSearch,
                CommonSharedData.TicketInfo,
                this.fieldKey);
        lstvw_tree_detail.setAdapter(detailAdapter);
    }
}
