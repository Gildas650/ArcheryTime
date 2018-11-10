package gildas.archeytime.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import gildas.archeytime.Activities.SequenceDetail;
import gildas.archeytime.Business.Lights;
import gildas.archeytime.Business.Sequence;
import gildas.archeytime.Business.SequenceItem;
import gildas.archeytime.Business.Types;
import gildas.archeytime.Db.ArcheryTimeDb;
import gildas.archeytime.R;

/**
 * Adapter created to create line in list view
 * Created by Gildas on 30/12/2016.
 */



public class SequenceAdapter extends ArrayAdapter<Sequence> {

    private ArcheryTimeDb db;
    private List<Sequence> list;
    private final static String SEQUENCE = "com.archerytime.SEQUENCE";

    public SequenceAdapter(Context context, List<Sequence> sequences) {
        super(context, 0, sequences);
        this.list = sequences;
        db = new ArcheryTimeDb(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        DisplayMetrics metrics = parent.getContext().getResources().getDisplayMetrics();
        float wView = metrics.widthPixels;
        float hView = (float) (wView * 0.05);

        Paint rPaint = new Paint();
        rPaint.setColor(parent.getContext().getResources().getColor(R.color.red));
        Paint yPaint = new Paint();
        yPaint.setColor(parent.getContext().getResources().getColor(R.color.yellow));
        Paint gPaint = new Paint();
        gPaint.setColor(parent.getContext().getResources().getColor(R.color.green));

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_log, parent, false);
        }
        //Initialize the View if it's the first line
        SequenceViewHolder viewHolder = (SequenceViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SequenceViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.boldtext);
            viewHolder.timeline = (ImageView) convertView.findViewById(R.id.guideline);
            viewHolder.goDetail = (Button) convertView.findViewById(R.id.godetail);
            convertView.setTag(viewHolder);
        }

        //Define the text
        final Sequence sequence = getItem(position);
        String bold;
        bold = sequence.getName();
        if (!sequence.getListOfItems().isEmpty()) {


             Bitmap dstBitmap = Bitmap.createBitmap(
                     (int) wView, // Width
                     (int) hView, // Height
                    Bitmap.Config.ARGB_8888 // Config
            );
            Canvas can = new Canvas(dstBitmap);
            can.drawColor(Color.TRANSPARENT);
            float cx = hView / 2;
            float cy = hView / 2;
            float pointRadius = hView / 2;
            float ratio = (wView - pointRadius *2) / sequence.getTotalDuration(); //size in screen by seconds
            Lights lastColor = Lights.Red;


            for (int i = 0; i < sequence.getListOfItems().size(); i++){
                SequenceItem toRender = sequence.getItemOfSequence(i);
                if (toRender.getType().equals(Types.Signal)) {
                    lastColor = toRender.getLight();
                    if(lastColor.equals(Lights.Red)) {
                        can.drawCircle(cx, cy, pointRadius , rPaint);
                    }
                    if(lastColor.equals(Lights.Yellow)) {
                        can.drawCircle(cx, cy, pointRadius , yPaint);
                    }
                    if(lastColor.equals(Lights.Green)) {
                        can.drawCircle(cx, cy, pointRadius , gPaint);
                    }
                }
                if (toRender.getType().equals(Types.Duration)) {
                    if(lastColor.equals(Lights.Red)) {
                        can.drawRect(cx,cy - pointRadius / 2   ,cx + toRender.getDuration()*ratio, cy + pointRadius / 2, rPaint);
                    }
                    if(lastColor.equals(Lights.Yellow)) {
                        can.drawRect(cx,cy - pointRadius / 2 ,cx + toRender.getDuration()*ratio, cy + pointRadius / 2 , yPaint);
                    }
                    if(lastColor.equals(Lights.Green)) {
                        can.drawRect(cx,cy - pointRadius / 2 ,cx + toRender.getDuration()*ratio, cy + pointRadius / 2 , gPaint);
                    }

                    cx = cx + toRender.getDuration()*ratio;
                }
                }
            /* render items*/
            viewHolder.timeline.setImageBitmap(dstBitmap);
            }

            viewHolder.text.setText(bold);

            viewHolder.goDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(parent.getContext(), SequenceDetail.class);
                    i.putExtra(SEQUENCE, sequence);
                    parent.getContext().startActivity(i);
            }
            });
        return convertView;
 }


 private class SequenceViewHolder {
        public TextView text;
        public ImageView timeline;
        public Button goDetail;


    }
}