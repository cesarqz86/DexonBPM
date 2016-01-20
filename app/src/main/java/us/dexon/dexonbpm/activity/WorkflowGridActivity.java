package us.dexon.dexonbpm.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import inqbarna.tablefixheaders.TableFixHeaders;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.WorkflowMatrixTableAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.RequestDTO.WorkflowRequestDto;

public class WorkflowGridActivity extends FragmentActivity {

    private WorkflowMatrixTableAdapter matrixTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workflow_grid);

        WorkflowRequestDto recordHeader = new WorkflowRequestDto();
        recordHeader.setTicketInfo(CommonSharedData.TicketInfoUpdated.getTicketInfo());

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteLoadWorkflowTable getAllRecords = serviceExecuter.new ExecuteLoadWorkflowTable(this);
        getAllRecords.execute(recordHeader);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.setResult(90, this.getIntent());
        }

        return super.onKeyDown(keyCode, event);
    }

    public void inidentsCallBack(String[][] dataList) {
        if (CommonValidations.validateArrayNullOrEmpty(dataList)) {
            ITicketService ticketService = TicketService.getInstance();
            dataList = ticketService.getEmptyData("");
        }
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
        this.matrixTableAdapter = new WorkflowMatrixTableAdapter(this, dataList, indexColumnID);
        tableFixHeaders.setAdapter(this.matrixTableAdapter);
    }
}
