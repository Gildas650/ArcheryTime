package gildas.archeytime.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

import gildas.archeytime.Business.Lights;
import gildas.archeytime.Business.Sequence;
import gildas.archeytime.Business.SequenceItem;
import gildas.archeytime.Business.Types;

public class ArcheryTimeDb {

    private static final int DATABASE_VERSION = 4;
    private static final String DBNAME = "archeryTime.db";

    //Sequence Table description : store sequence data
    private static final String SEQUENCE_TABLE_NAME = "sequence";
    private static final String SEQUENCE_ID = "id";
    private static final String NAME = "name";
    private static final String RANK = "rank";
    private static final String CUSTOM = "custom";

    //Session Table description : store sequence item data
    private static final String SEQUENCE_ITEM_TABLE_NAME = "sequenceitem";
    private static final String SEQUENCE_ITEM_ID = "id";
    private static final String TYPE = "type";
    private static final String PARENT_SEQUENCE = "sequenceid";
    private static final String ITEM_RANK = "rank";
    private static final String DURATION = "duration";
    private static final String LIGHT = "light";
    private static final String SOUND = "sound";

    private SQLiteDatabase bdd;
    private ArcheryTimeDBOpenHelper ArrowTimeSQLite;

    public ArcheryTimeDb(Context context) {
        ArrowTimeSQLite = new ArcheryTimeDBOpenHelper(context, DBNAME, null, DATABASE_VERSION);
    }

    public void open() {
        bdd = ArrowTimeSQLite.getWritableDatabase();
    }

    public void close() {
        bdd.close();
    }

    public long insert(Sequence s) {

        //Insert sequence and retreive Id
        ContentValues sequenceValues = new ContentValues();
        sequenceValues.put(NAME, s.getName());
        sequenceValues.put(RANK, s.getRank());
        sequenceValues.put(CUSTOM, booleanToInt(s.isCustom()));
        long id = bdd.insert(SEQUENCE_TABLE_NAME, null, sequenceValues);

        //Insert items
        if (!s.getListOfItems().isEmpty()) {
            ArrayList<SequenceItem> si = s.getListOfItems();
            ContentValues sequenceItemValues = new ContentValues();
            for (int i = 0; i < si.size(); i++) {
                sequenceItemValues.put(TYPE, si.get(i).getType().toString());
                sequenceItemValues.put(PARENT_SEQUENCE, id);
                sequenceItemValues.put(ITEM_RANK, i);
                sequenceItemValues.put(DURATION, si.get(i).getDuration());
                sequenceItemValues.put(LIGHT, si.get(i).getLight().toString());
                sequenceItemValues.put(SOUND, si.get(i).getSound());
                bdd.insert(SEQUENCE_ITEM_TABLE_NAME, null, sequenceItemValues);
            }
        }
        return id;
    }

    //Remove a session and all relative scores
    public int removeSequence(int id) {
        String[] args = {id + ""};
        Cursor cursor = bdd.rawQuery("SELECT * from " + SEQUENCE_ITEM_TABLE_NAME + " WHERE " + PARENT_SEQUENCE + " = ? ", args);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bdd.delete(SEQUENCE_ITEM_TABLE_NAME, SEQUENCE_ITEM_ID + " = " + cursor.getInt(0), null);
        }
        cursor.close();
        return bdd.delete(SEQUENCE_TABLE_NAME, SEQUENCE_ID + " = " + id, null);
    }

    //Fecth a sequence object from his ID in DB
    private Sequence selectSequenceWithID(String[] args) {
        Cursor sequenceCursor = bdd.rawQuery("SELECT * from " + SEQUENCE_TABLE_NAME + " WHERE id = ? ", args);
        Cursor sequenceItemCursor = bdd.rawQuery("SELECT * from " + SEQUENCE_ITEM_TABLE_NAME + " WHERE " + PARENT_SEQUENCE + " = ? ", args);
        Sequence s = cursorToSequence(sequenceCursor, sequenceItemCursor);
        sequenceCursor.close();
        sequenceItemCursor.close();
        return s;
    }

    //Select all session in DB (used for list of sessions)
    public LinkedList<Sequence> selectAll() {
        LinkedList<Sequence> sequences = new LinkedList<>();
        Cursor cursor = bdd.rawQuery("SELECT " + SEQUENCE_ID + " FROM " + SEQUENCE_TABLE_NAME + " ORDER BY " + RANK + " DESC", null);
        Sequence sequence;
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String[] tmp = {cursor.getInt(0) + ""};
                sequence = selectSequenceWithID(tmp);
                sequences.add(0, sequence);
            }
        }
        cursor.close();
        return sequences;
    }


    private Sequence cursorToSequence(Cursor se, Cursor sei) {
        se.moveToFirst();
        Sequence s = new Sequence();
        s.setDbId(se.getInt(0));
        s.setName(se.getString(1));
        s.setRank(se.getInt(2));
        s.setCustom(intToBoolean(se.getInt(3)));

        if (sei != null) {
            if (sei.getCount() > 0) {
                sei.moveToFirst();
                do {
                    if(sei.getString(1).equals(Types.Duration.toString())) {
                        SequenceItem du = new SequenceItem(Types.Duration, sei.getInt(4));
                        s.addItemToSequence(sei.getInt(3),du);
                    }
                    else if(sei.getString(1).equals(Types.Signal.toString())) {
                        String light = sei.getString(5);
                        Lights lightSelected = Lights.Red;
                        if (light.equals(Lights.Green.toString())){
                            lightSelected = Lights.Green;
                        }
                        else if (light.equals(Lights.Yellow.toString())){
                            lightSelected = Lights.Yellow;
                        }
                        SequenceItem si = new SequenceItem(Types.Signal,lightSelected, sei.getInt(6));
                        s.addItemToSequence(sei.getInt(3), si);
                    }
                } while (sei.moveToNext());
            }
        }

        return s;
    }

    //Transform a int into a boolean
    private boolean intToBoolean(int transform) {
        boolean ret = false;
        if (transform == 1) {
            ret = true;
        }
        return ret;
    }

    //Transform a int into a boolean
    private Integer booleanToInt(boolean transform) {
        Integer ret = 0;
        if (transform == true) {
            ret = 1;
        }
        return ret;
    }

}
