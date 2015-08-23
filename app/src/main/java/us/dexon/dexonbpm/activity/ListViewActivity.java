package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.JsonParser;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TreeAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;

public class ListViewActivity extends FragmentActivity {

    private String keyToSearch;
    private String sonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        JsonParser gsonSerializer = new JsonParser();
        Intent currentIntent = this.getIntent();
        this.sonData = currentIntent.getStringExtra("sonData");
        this.keyToSearch = currentIntent.getStringExtra("keyToSearch");

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        RecordHeaderResquestDto recordHeader = new RecordHeaderResquestDto();
        recordHeader.setLoggedUser(loggedUser);
        recordHeader.setFieldInformation(gsonSerializer.parse(this.sonData).getAsJsonObject());

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteAllRecordHeaderTree getAllRecords = serviceExecuter.new ExecuteAllRecordHeaderTree(this);
        getAllRecords.execute(recordHeader);
    }

    public void inidentsCallBack(RecordHeaderResponseDto responseDto) {
        ListView lstvw_tree_detail = (ListView) this.findViewById(R.id.lstvw_tree_detail);

        if (!CommonValidations.validateEmpty(this.keyToSearch)) {
            this.keyToSearch = "-1";
        }

        TreeAdapter detailAdapter = new TreeAdapter(this, responseDto.getDataList(), this.keyToSearch, this.sonData);
        lstvw_tree_detail.setAdapter(detailAdapter);
    }
}
