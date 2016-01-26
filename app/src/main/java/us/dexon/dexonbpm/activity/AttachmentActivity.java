package us.dexon.dexonbpm.activity;

import android.app.ProgressDialog;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.model.ReponseDTO.AttachmentDto;
import us.dexon.dexonbpm.model.ReponseDTO.AttachmentItem;
import us.dexon.dexonbpm.utils.FileUtils;

public class AttachmentActivity extends FragmentActivity {

    private static final int FILE_SELECT_CODE = 0;
    private LinearLayout lstvw_attachmentdata;
    private LinearLayout lstvw_pending_attachmentdata;
    private TextView txt_attachment_title;
    private TextView txt_attachment_pending_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        this.lstvw_attachmentdata = (LinearLayout) this.findViewById(R.id.lstvw_attachmentdata);
        this.lstvw_pending_attachmentdata = (LinearLayout) this.findViewById(R.id.lstvw_pending_attachmentdata);
        this.txt_attachment_title = (TextView) this.findViewById(R.id.txt_attachment_title);
        this.txt_attachment_pending_title = (TextView) this.findViewById(R.id.txt_attachment_pending_title);

        if (CommonSharedData.TicketInfo != null) {
            JsonObject jsonTicket = CommonSharedData.TicketInfo.getTicketInfo();
            this.drawAttachmentData(jsonTicket);
            this.drawPendindAttachmentData();
        }

        this.SetConfiguredColors();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE: {
                if (resultCode == RESULT_OK) {

                    try {
                        if (CommonSharedData.AttachmentList == null) {
                            CommonSharedData.AttachmentList = new ArrayList<AttachmentItem>();
                        }
                        Uri uri = data.getData();
                        String path = null;

                        path = FileUtils.getPath(this, uri);
                        InputStream fileData = new FileInputStream(path);
                        byte[] bytes;
                        byte[] bufferRead = new byte[8192];
                        int bytesRead;
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        while ((bytesRead = fileData.read(bufferRead)) != -1) {
                            output.write(bufferRead, 0, bytesRead);
                        }
                        bytes = output.toByteArray();
                        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                        String fileName = path.substring(path.lastIndexOf("/") + 1);

                        AttachmentItem newAttachment = new AttachmentItem();
                        newAttachment.setAttachmentName(path);
                        newAttachment.setAttachmentData(encodedString);
                        newAttachment.setFileName(fileName);

                        CommonSharedData.AttachmentList.add(newAttachment);
                        this.drawPendindAttachmentData();
                    } catch (Exception ex) {

                    }
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    public void newAttachment(View view) {

        Intent newAttachmentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        newAttachmentIntent.setType("*/*");
        newAttachmentIntent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            this.startActivityForResult(
                    Intent.createChooser(newAttachmentIntent, this.getString(R.string.activity_newattachment_title)),
                    FILE_SELECT_CODE);
        } catch (Exception ex) {
            Log.e("Add Attachment", ex.getMessage(), ex);
        }
    }

    public void drawAttachmentData(JsonObject ticketInfo) {

        LayoutInflater inflater = this.getLayoutInflater();
        this.txt_attachment_title.setVisibility(View.GONE);

        if (ticketInfo != null && ticketInfo.has("attachedDocuments") && !ticketInfo.get("attachedDocuments").isJsonNull()) {

            JsonArray ticketAttachments = ticketInfo.get("attachedDocuments").getAsJsonArray();

            List<AttachmentDto> attachmentList = new ArrayList<>(ticketAttachments.size());

            for (int index = 0; index < ticketAttachments.size(); index++) {
                JsonObject attchamentItem = ticketAttachments.get(index).getAsJsonObject();
                String fieldName = attchamentItem.get("documentName").isJsonNull() ? "" : attchamentItem.get("documentName").getAsString();
                int attachmentId = attchamentItem.get("tmpId").isJsonNull() ? 0 : attchamentItem.get("tmpId").getAsInt();
                AttachmentDto tempItem = new AttachmentDto();
                tempItem.setAttachmentName(fieldName);
                tempItem.setDocumentId(attachmentId);
                tempItem.setAttachmentObject(attchamentItem);
                attachmentList.add(tempItem);
            }

            if (attachmentList.size() > 0) {
                this.txt_attachment_title.setVisibility(View.VISIBLE);
                for (int index = 0; index < attachmentList.size(); index++) {
                    AttachmentDto tempObject = attachmentList.get(index);
                    View rowView = inflater.inflate(R.layout.item_tree_regular, null);
                    rowView.setOnClickListener(new DexonListeners.DocumentClickListener(
                            rowView.getContext(),
                            tempObject));
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                    txt_fieldvalue.setText(tempObject.getAttachmentName());
                    txt_fieldvalue.setTag(tempObject.getAttachmentObject());
                    this.lstvw_attachmentdata.addView(rowView);
                }
            }
        }

    }

    public void drawPendindAttachmentData() {
        LayoutInflater inflater = this.getLayoutInflater();
        this.txt_attachment_pending_title.setVisibility(View.GONE);
        this.lstvw_pending_attachmentdata.removeAllViews();

        if (CommonSharedData.AttachmentList != null && CommonSharedData.AttachmentList.size() > 0) {
            this.txt_attachment_pending_title.setVisibility(View.VISIBLE);
            for (int index = 0; index < CommonSharedData.AttachmentList.size(); index++) {
                AttachmentItem tempObject = CommonSharedData.AttachmentList.get(index);
                View rowView = inflater.inflate(R.layout.item_tree_regular, null);
                rowView.setOnClickListener(new DexonListeners.PendingDocumentClickListener(
                        rowView.getContext(),
                        tempObject));
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                txt_fieldvalue.setText(tempObject.getFileName());
                this.lstvw_pending_attachmentdata.addView(rowView);
            }
        }
    }

    public void attachmentServiceCallback(JsonObject documentData) {

        String finalExtension = documentData.get("fileExtension").getAsString();
        finalExtension = CommonValidations.validateEmpty(finalExtension) ? finalExtension.replace(".", "") : "";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(finalExtension);

        if (CommonValidations.validateEmpty(mimeType)) {
            try {
                String bufferData = documentData.get("_buffer").getAsString();
                String fileName = documentData.get("documentName").getAsString();
                this.showDownloadedFile(bufferData, fileName, mimeType);
            } catch (Exception ex) {
                Log.e("Downloading attachment", ex.getMessage());
            }
        }
    }

    public void showDownloadedFile(String bufferData, String fileName, String mimeType) {
        try {
            byte[] pdfData = Base64.decode(bufferData, Base64.DEFAULT);
            String filePathString = Environment.getExternalStorageDirectory() + "/" + fileName;
            File filePath = new File(filePathString);
            if (filePath.exists()) {
                filePath.delete();
            }
            FileOutputStream fileWriter = new FileOutputStream(filePath, true);
            fileWriter.write(pdfData);
            fileWriter.flush();
            fileWriter.close();

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(filePath), mimeType);
            this.startActivityForResult(intent, 10);
            this.overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        } catch (ActivityNotFoundException ex) {
            AttachmentItem attachmentItem = new AttachmentItem();
            attachmentItem.setFileName(fileName);
            attachmentItem.setAttachmentData(bufferData);

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteDownloadFile downloadFile = serviceExecuter.new ExecuteDownloadFile(this);
            downloadFile.execute(attachmentItem);
        } catch (Exception ex) {
            Log.e("Downloading attachment", ex.getMessage());
        }
    }

    private void SetConfiguredColors() {

        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        Drawable logo_mini = this.getResources().getDrawable(R.drawable.logo_mini);
        Drawable ic_plus = this.getResources().getDrawable(R.drawable.ic_plus);

        ImageButton plus_button = (ImageButton) this.findViewById(R.id.plus_button);
        ImageButton dexon_logo = (ImageButton) this.findViewById(R.id.dexon_logo);

        logo_mini.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        ic_plus.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);

        dexon_logo.setBackground(logo_mini);
        plus_button.setBackground(ic_plus);
    }
}
