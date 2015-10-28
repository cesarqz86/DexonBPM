package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

//import com.joanzapata.pdfview.PDFView;

import java.io.File;

import us.dexon.dexonbpm.R;

public class WebViewActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent currentIntent = this.getIntent();
        String pdfData = currentIntent.getStringExtra("PdfUrl");
        File pdfFile = new File(pdfData);

        /*PDFView web_view = (PDFView) this.findViewById(R.id.web_view);
        web_view.fromFile(pdfFile)
                .showMinimap(true)
                .enableSwipe(true)
                .load();*/
    }
}
