package us.dexon.dexonbpm.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.JsonObject;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.zip.Inflater;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.ProgressPagerAdapter;
import us.dexon.dexonbpm.adapters.TicketDetailAdapter;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.AttachmentItem;
import us.dexon.dexonbpm.model.ReponseDTO.DescendantResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.PrintTicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.DescendantRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.PrintTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecalculateSLARequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

/**
 * Created by androide on 27/05/15.
 */
public class TicketDetail extends FragmentActivity {

    private String ticketId = "";
    private Menu menu;
    private TicketResponseDto ticketData;
    private String activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
        this.registerForContextMenu(menuButton);

        Intent i = getIntent();
        ticketId = i.getExtras().getString("TICKET_ID");

        if (CommonValidations.validateEmpty(ticketId)) {
            IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
            dexonDatabase.setContext(this);

            LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

            TicketDetailRequestDto ticketData = new TicketDetailRequestDto();
            ticketData.setIncidentID(Integer.parseInt(ticketId));
            ticketData.setLoggedUser(loggedUser);

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteTicketDetailService ticketService = serviceExecuter.new ExecuteTicketDetailService(this);
            ticketService.execute(ticketData);
        }

        CommonSharedData.TicketActivity = this;

        this.SetConfiguredColors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ticket_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                CommonService.saveTicket(this);
                break;
            }
            case R.id.action_reopen: {
                IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                dexonDatabase.setContext(this);

                LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

                ReopenRequestDto ticketData = new ReopenRequestDto();
                ticketData.setLoggedUser(loggedUser);
                ticketData.setTicketInfo(this.ticketData.getTicketInfo());

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteReopenTicket reopenService = serviceExecuter.new ExecuteReopenTicket(this);
                reopenService.execute(ticketData);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Creamos el objeto que debe inflar la vista del menú en la pantalla.
        MenuInflater inflater = new MenuInflater(this);

        switch (v.getId()) {
            case R.id.menu_button:
                inflater.inflate(R.menu.menu_ticket_detail_submenu, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        ServiceExecuter serviceExecuter = new ServiceExecuter();

        // Previamente creamos el objeto TextView y lo inicializamos para poder
        // asignarle aquí el texto en función de la opción seleccionada.
        switch (item.getItemId()) {
            case R.id.button2_menu_opt1:
            case R.id.button2_menu_opt2:
                // Create son or
                // Create brother
                DescendantRequestDto ticketData = new DescendantRequestDto();
                ticketData.setLoggedUser(loggedUser);
                ticketData.setTicketInfo(this.ticketData.getTicketInfo());
                ticketData.setIsTicketSon(item.getItemId() == R.id.button2_menu_opt1);

                ServiceExecuter.ExecuteCreateDescendant createDescendantService = serviceExecuter.new ExecuteCreateDescendant(this);
                createDescendantService.execute(ticketData);
                break;
            case R.id.button2_menu_opt3:
                // Print ticket
                PrintTicketRequestDto printTicketData = new PrintTicketRequestDto();
                printTicketData.setLoggedUser(loggedUser);
                printTicketData.setTicketInfo(this.ticketData.getTicketInfo());

                ServiceExecuter.ExecutePrintTicket printTicketService = serviceExecuter.new ExecutePrintTicket(this);
                printTicketService.execute(printTicketData);
                break;
            case R.id.button2_menu_opt4:
                RecalculateSLARequestDto recalculateSLAData = new RecalculateSLARequestDto();
                recalculateSLAData.setTicketInfo(this.ticketData.getTicketInfo());

                ServiceExecuter.ExecuteRecalculateSLAService recalculateSLAService = serviceExecuter.new ExecuteRecalculateSLAService(this);
                recalculateSLAService.execute(recalculateSLAData);
                // Recalculate SLA
                break;
            case R.id.button2_menu_opt6:
                Intent historyIntent = new Intent(this, HistoryActivity.class);
                historyIntent.putExtra("ViewTitle", this.getTitle());
                CommonSharedData.TicketActivity.startActivityForResult(historyIntent, 0);
                CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                // See history
                break;
            case R.id.button2_menu_opt7:
                Intent attachmentIntent = new Intent(this, AttachmentActivity.class);
                CommonSharedData.TicketActivity.startActivityForResult(attachmentIntent, 0);
                CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                // Attachments
                break;
            case R.id.button2_menu_opt5:
                // Cancel does nothing
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonSharedData.TreeData = null;

        if (CommonSharedData.OriginalStatus != null && resultCode == 90) {
            JsonObject ticketJsonInfo = CommonSharedData.TicketInfo.getTicketInfo();
            JsonObject headerInfo = ticketJsonInfo.get("headerInfo").getAsJsonObject();
            headerInfo.add("status", CommonSharedData.OriginalStatus);
            ITicketService ticketService = TicketService.getInstance();
            CommonSharedData.TicketInfo = ticketService.convertToTicketData(ticketJsonInfo, R.id.btn_setmanual_technician, null);
            this.inidentsCallBack(CommonSharedData.TicketInfo);
            CommonSharedData.OriginalStatus = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonSharedData.OriginalTechnician = null;
        CommonSharedData.AttachmentList = null;
        CommonSharedData.ManualTechnician = null;
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    public void menuClick(View view) {
        this.openContextMenu(view);
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {

        if (responseDto != null) {
            CommonSharedData.TicketInfo = responseDto;
            CommonSharedData.AttachmentList = null;

            this.ticketData = responseDto;
            CommonService.drawTicket(responseDto, this);

            this.activityTitle = this.getString(R.string.app_name);

            if (responseDto.getDataList() != null) {
                for (TicketDetailDataDto ticketData : responseDto.getDataList()) {
                    if (ticketData.getFieldKey().equals("ticket")) {
                        this.activityTitle = ticketData.getFieldValue();
                        break;
                    }
                }
            }
            this.setTitle(this.activityTitle);

            // Menu options
            MenuItem action_reopen = this.menu.findItem(R.id.action_reopen);
            MenuItem action_save = this.menu.findItem(R.id.action_save);

            action_save.setVisible(false);
            action_reopen.setVisible(false);

            if (responseDto.getIsOpen()) {
                if (responseDto.getIsEditable()) {
                    action_save.setVisible(true);
                }
            } else {
                action_reopen.setVisible(true);
            }

            // Progress Pager
            ProgressPagerAdapter progressPagerAdapter = new ProgressPagerAdapter(
                    this.getSupportFragmentManager(), responseDto);
            ViewPager progress_pager = (ViewPager) this.findViewById(R.id.progress_pager);
            progress_pager.setAdapter(progressPagerAdapter);

            CirclePageIndicator progress_pager_indicator = (CirclePageIndicator) this.findViewById(R.id.progress_pager_indicator);
            progress_pager_indicator.setViewPager(progress_pager);

            if (responseDto.getBarPercentDone() == null) {
                progress_pager_indicator.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void reloadCallback(JsonObject ticketInfo) {

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        ReloadRequestDto reloadData = new ReloadRequestDto();
        reloadData.setTicketInfo(ticketInfo);
        reloadData.setLoggedUser(loggedUser);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteReloadTicket ticketService = serviceExecuter.new ExecuteReloadTicket(this);
        ticketService.execute(reloadData);
    }

    public void descendantCallback(DescendantResponseDto descendantResponseDto) {

        if (CommonValidations.validateEmpty(descendantResponseDto.getTicketID())) {
            IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
            dexonDatabase.setContext(this);

            LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

            TicketDetailRequestDto ticketData = new TicketDetailRequestDto();
            ticketData.setIncidentID(Integer.parseInt(descendantResponseDto.getTicketID()));
            ticketData.setLoggedUser(loggedUser);

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteTicketDetailService ticketService = serviceExecuter.new ExecuteTicketDetailService(this);
            ticketService.execute(ticketData);
        }
    }

    public void printCallback(PrintTicketResponseDto printTicketResponseDto) {

        if (CommonValidations.validateEmpty(printTicketResponseDto.getBufferData())) {
            try {
                byte[] pdfData = Base64.decode(printTicketResponseDto.getBufferData(), Base64.DEFAULT);
                File filePath = new File(Environment.getExternalStorageDirectory() + "/temp.pdf");
                if (filePath.exists()) {
                    filePath.delete();
                }
                FileOutputStream fileWriter = new FileOutputStream(filePath, true);
                fileWriter.write(pdfData);
                fileWriter.flush();
                fileWriter.close();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
                intent.setDataAndType(Uri.fromFile(filePath), mimeType);
                this.startActivityForResult(intent, 10);
                this.overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            } catch (ActivityNotFoundException ex) {
                AttachmentItem attachmentItem = new AttachmentItem();
                attachmentItem.setFileName(this.activityTitle + ".pdf");
                attachmentItem.setAttachmentData(printTicketResponseDto.getBufferData());

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteDownloadFile downloadFile = serviceExecuter.new ExecuteDownloadFile(this);
                downloadFile.execute(attachmentItem);
            } catch (Exception e) {
                Log.e("Error loading PDF data", e.getMessage());
            }
        }
    }

    private void SetConfiguredColors() {

        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        ViewPager progress_pager = (ViewPager) this.findViewById(R.id.progress_pager);
        Drawable logo_mini = this.getResources().getDrawable(R.drawable.logo_mini);
        Drawable ic_action_ticket_menu = this.getResources().getDrawable(R.drawable.ic_action_ticket_menu);
        Drawable progress_pager_background = progress_pager.getBackground();
        View related_data_separator = this.findViewById(R.id.related_data_separator);
        View related_data_detail_separator = this.findViewById(R.id.related_data_detail_separator);

        ImageButton menu_button = (ImageButton) this.findViewById(R.id.menu_button);
        ImageButton dexon_logo = (ImageButton) this.findViewById(R.id.dexon_logo);

        logo_mini.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        ic_action_ticket_menu.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        related_data_separator.setBackgroundColor(primaryColor);
        related_data_detail_separator.setBackgroundColor(secondaryColor);

        dexon_logo.setBackground(logo_mini);
        menu_button.setBackground(ic_action_ticket_menu);

        if (progress_pager_background instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progress_pager_background;
            Drawable firstDrawable = layerDrawable.getDrawable(0);
            firstDrawable.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
