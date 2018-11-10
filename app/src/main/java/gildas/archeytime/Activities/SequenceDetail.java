package gildas.archeytime.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import gildas.archeytime.Business.Sequence;
import gildas.archeytime.R;

public class SequenceDetail extends Activity {
    private final static String SEQUENCE = "com.archerytime.SEQUENCE";
    private Sequence sequence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Intent intent = getIntent();
        sequence = (Sequence) intent.getSerializableExtra(SEQUENCE);
        TextView title = (TextView) findViewById(R.id.textView2);
        title.setText(sequence.getName());
    }
}
