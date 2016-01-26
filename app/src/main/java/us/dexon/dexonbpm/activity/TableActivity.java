package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inqbarna.tablefixheaders.TableFixHeaders;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.MatrixTableAdapter;
import us.dexon.dexonbpm.adapters.TableAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;

public class TableActivity extends FragmentActivity {

    private String keyToSearch;
    private String sonData;
    private TableAdapter matrixTableAdapter;
    private String fieldKey;
    private String incidentCode;
    private String[][] originalData;

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

        this.SetConfiguredColors();
    }

    public void inidentsCallBack(String[][] dataList) {
        if (CommonValidations.validateArrayNullOrEmpty(dataList)) {
            ITicketService ticketService = TicketService.getInstance();
            dataList = ticketService.getEmptyData("");
        }
        if (this.originalData == null) {
            this.originalData = dataList;
        }
        this.drawData(dataList);
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
        TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table_detail);
        this.matrixTableAdapter = new TableAdapter(this, dataList, indexColumnID, this.fieldKey);
        tableFixHeaders.setAdapter(this.matrixTableAdapter);
    }

    private void SetConfiguredColors() {

        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        LinearLayout search_container = (LinearLayout) this.findViewById(R.id.search_container);

        search_container.setBackgroundColor(secondaryColor);
    }
}
