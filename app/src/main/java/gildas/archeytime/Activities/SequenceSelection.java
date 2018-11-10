package gildas.archeytime.Activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import gildas.archeytime.Adapters.SequenceAdapter;
import gildas.archeytime.Business.Sequence;
import gildas.archeytime.Db.ArcheryTimeDb;
import gildas.archeytime.R;


public class SequenceSelection extends Activity {

    private ListView seqView;
    private ArcheryTimeDb db = new ArcheryTimeDb(this);
    private List<Sequence>sequences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_selection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        seqView = (ListView) findViewById(R.id.seqView);
        seqView.setItemsCanFocus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        this.sequences = db.selectAll();
        db.close();

        SequenceAdapter adapter = new SequenceAdapter(SequenceSelection.this, sequences);
        seqView.setAdapter(adapter);

    }
}
