package eccomerce.com;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import us.feras.mdv.MarkdownView;


public class Track2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSupportActionBar().setTitle("Track Delivery");
        MarkdownView markdownView = (MarkdownView) findViewById(R.id.markdownView);
        markdownView.loadMarkdown("### May 25th 9:00 AM \n" +
                "\n" +
                "Request received\n" +
                "\n" +
                "### May 25th 9:10 AM \n" +
                "\n" +
                "Packaging your order\n" +
                "\n" +
                "### May 25th 9:30 AM\n" +
                "\n" +
                "Order dispatched\n");


    }

}
