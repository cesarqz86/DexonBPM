package us.dexon.dexonbpm.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.AttachmentAdapter;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.model.ReponseDTO.AttachmentDto;
import us.dexon.dexonbpm.model.RequestDTO.CleanEntityRequestDto;

public class AttachmentActivity extends FragmentActivity {

    private ListView lstvw_attachmentdata;
    private TextView txt_attachment_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        this.lstvw_attachmentdata = (ListView) this.findViewById(R.id.lstvw_attachmentdata);
        this.txt_attachment_title = (TextView) this.findViewById(R.id.txt_attachment_title);

        if (CommonSharedData.TicketInfo != null) {
            JsonObject jsonTicket = CommonSharedData.TicketInfo.getTicketInfo();
            this.drawAttachmentData(jsonTicket);
        }
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    public void newAttachment(View view) {

    }

    public void drawAttachmentData(JsonObject ticketInfo) {

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
            }

            AttachmentAdapter detailAdapter = new AttachmentAdapter(this, attachmentList);
            this.lstvw_attachmentdata.setAdapter(detailAdapter);

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
                CommonService.ShowAlertDialog(this,
                        R.string.validation_attachment_success_title,
                        R.string.validation_no_app_for_attachment,
                        MessageTypeIcon.Error,
                        false);
            } catch (Exception ex) {
                Log.e("Downloading attachment", ex.getMessage());
            }
        }
    }
}
