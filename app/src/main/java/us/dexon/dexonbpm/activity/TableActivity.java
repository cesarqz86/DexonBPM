package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.gson.JsonParser;

import inqbarna.tablefixheaders.TableFixHeaders;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.MatrixTableAdapter;
import us.dexon.dexonbpm.adapters.TableAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;

public class TableActivity extends FragmentActivity {

    private String keyToSearch;
    private String sonData;
    private TableAdapter matrixTableAdapter;
    private String fieldKey;
    private String incidentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        JsonParser gsonSerializer = new JsonParser();
        Intent currentIntent = this.getIntent();
        this.sonData = currentIntent.getStringExtra("sonData");
        this.keyToSearch = currentIntent.getStringExtra("keyToSearch");
        this.fieldKey = currentIntent.getStringExtra("fieldKey");
        this.incidentCode = CommonSharedData.TicketInfo.getTicketCode();

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        RecordHeaderResquestDto recordHeader = new RecordHeaderResquestDto();
        recordHeader.setLoggedUser(loggedUser);
        recordHeader.setFieldInformation(gsonSerializer.parse(this.sonData).getAsJsonObject());
        recordHeader.setIncidentCode(this.incidentCode);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteAllRecordHeaderTable getAllRecords = serviceExecuter.new ExecuteAllRecordHeaderTable(this);
        getAllRecords.execute(recordHeader);
    }

    public void inidentsCallBack(String[][] dataList) {
        if (CommonValidations.validateArrayNullOrEmpty(dataList)) {
            ITicketService ticketService = TicketService.getInstance();
            dataList = ticketService.getEmptyData("");
        }
        int indexColumnID = -1;
        if(dataList.length > 0)
        {
            int tempIndex = 0;
            for (String columnData: dataList[0])
            {
                if(CommonValidations.validateEmpty(columnData))
                {
                    indexColumnID = tempIndex;
                    break;
                }
                tempIndex++;
            }
        }
        TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table_detail);
        this.matrixTableAdapter = new TableAdapter(this, dataList, indexColumnID, this.fieldKey);
        tableFixHeaders.setAdapter(this.matrixTableAdapter);
    }
}
