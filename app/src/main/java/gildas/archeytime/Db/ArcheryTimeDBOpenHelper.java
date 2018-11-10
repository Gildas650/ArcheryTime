package gildas.archeytime.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import gildas.archeytime.Business.Lights;
import gildas.archeytime.Business.Types;

public class ArcheryTimeDBOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 4;

    //Sequence Table description : store sequence data
    private static final String SEQUENCE_TABLE_NAME = "sequence";
    private static final String SEQUENCE_ID = "id";
    private static final String NAME = "name";
    private static final String RANK = "rank";
    private static final String CUSTOM = "custom";

    //Session Table description : store sequence item data
    private static final String SEQUENCE_ITEM_TABLE_NAME = "sequenceitem";
    private static final String SEQUENCE_ITEM_TABLE_INDEX = "sequenceitemindex";
    private static final String SEQUENCE_ITEM_ID = "id";
    private static final String TYPE = "type";
    private static final String PARENT_SEQUENCE = "sequenceid";
    private static final String ITEM_RANK = "rank";
    private static final String DURATION = "duration";
    private static final String LIGHT = "light";
    private static final String SOUND = "sound";


    private static final String SEQUENCE_TABLE_CREATE =
            "CREATE TABLE " + SEQUENCE_TABLE_NAME + " (" + SEQUENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NAME + " TEXT, " + RANK + " INTEGER DEFAULT 0," + CUSTOM + " INTEGER DEFAULT 0);";

    private static final String SEQUENCE_ITEM_TABLE_CREATE =
            "CREATE TABLE " + SEQUENCE_ITEM_TABLE_NAME + " (" + SEQUENCE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TYPE + " TEXT, " + PARENT_SEQUENCE + " INTEGER , " + ITEM_RANK + " INTEGER, "
                    + DURATION + " INTEGER , " + LIGHT + " TEXT," + SOUND + " INTEGER);";

    private static final String SEQUENCE_ITEM_INDEX_CREATE =
            "CREATE INDEX " + SEQUENCE_ITEM_TABLE_INDEX + " ON " + SEQUENCE_ITEM_TABLE_NAME + " (" + SEQUENCE_ITEM_ID + ");";


    public ArcheryTimeDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SEQUENCE_TABLE_CREATE);
        db.execSQL(SEQUENCE_ITEM_TABLE_CREATE);
        db.execSQL(SEQUENCE_ITEM_INDEX_CREATE);
        initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SEQUENCE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + SEQUENCE_ITEM_TABLE_NAME + ";");
        onCreate(db);
    }

    public void initDb(SQLiteDatabase db){
        createSequenceInDb(db,"Salle - 3 flèches - 120 secondes", "0", 120, false, -1);
        createSequenceInDb(db,"Extérieur - 6 flèches - 240s secondes", "1", 240, false, -1);
        createSequenceInDb(db,"Duels Alternés - 1 flèche - 20 secondes", "2", 120, true, 6);
        createSequenceInDb(db,"Mixte/Réparation - 40s", "3", 40, false, -1);
        createSequenceInDb(db,"Mixte - 4 flèches - 80s", "4", 80, false, -1);
    }

    public void createSequenceInDb(SQLiteDatabase db,String name, String rank, Integer time, boolean isMatch, Integer nbOfArrows) {
        /* Create Sequence */
        ContentValues value = new ContentValues();
        value.put(NAME, name);
        value.put(RANK, rank);
        value.put(CUSTOM, "0");
        long sequence = db.insert(SEQUENCE_TABLE_NAME, null, value);

        /*Create SequenceItems
        /* Red light, 2 sound Signal*/
        value.clear();
        value.put(TYPE, Types.Signal.toString());
        value.put(PARENT_SEQUENCE, sequence);
        value.put(ITEM_RANK, 0);
        value.put(LIGHT, Lights.Red.toString());
        value.put(SOUND, 2);
        db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
        /* Wait for 10 sec*/
        value.clear();
        value.put(TYPE, Types.Duration.toString());
        value.put(PARENT_SEQUENCE, sequence);
        value.put(ITEM_RANK, 1);
        value.put(DURATION, 10);
        db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
        /* Green light, 1 sound Signal*/
        value.clear();
        value.put(TYPE, Types.Signal.toString());
        value.put(PARENT_SEQUENCE, sequence);
        value.put(ITEM_RANK, 2);
        value.put(LIGHT, Lights.Green.toString());
        value.put(SOUND, 1);
        db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);

        /* If match, split time by Arrow number and repeat until end*/

        int itemRank = 3;
        if (isMatch) {
            int timeToShoot = time / nbOfArrows;
            for (int i = 0; i < nbOfArrows; i++) {
                /* Wait for X sec*/
                value.clear();
                value.put(TYPE, Types.Duration.toString());
                value.put(PARENT_SEQUENCE, sequence);
                value.put(ITEM_RANK, itemRank);
                value.put(DURATION, timeToShoot);
                db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
                itemRank = itemRank + 1;
                /* Green light, 1 sound Signal*/
                value.clear();
                value.put(TYPE, Types.Signal.toString());
                value.put(PARENT_SEQUENCE, sequence);
                value.put(ITEM_RANK, itemRank);
                value.put(LIGHT, Lights.Green.toString());
                value.put(SOUND, 1);
                db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
                itemRank = itemRank + 1;
            }
        }
        else{
            int timeToShoot = time - 30;
            /* Wait for X sec - Yellow light*/
            value.clear();
            value.put(TYPE, Types.Duration.toString());
            value.put(PARENT_SEQUENCE, sequence);
            value.put(ITEM_RANK, itemRank);
            value.put(DURATION, timeToShoot);
            db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
            itemRank = itemRank + 1;
            /* Yellow light, No sound Signal*/
            value.clear();
            value.put(TYPE, Types.Signal.toString());
            value.put(PARENT_SEQUENCE, sequence);
            value.put(ITEM_RANK, itemRank);
            value.put(LIGHT, Lights.Yellow.toString());
            value.put(SOUND, 0);
            db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
            itemRank = itemRank + 1;
            /* Wait for 30 sec - Yellow light*/
            value.clear();
            value.put(TYPE, Types.Duration.toString());
            value.put(PARENT_SEQUENCE, sequence);
            value.put(ITEM_RANK, itemRank);
            value.put(DURATION, 30);
            db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
            itemRank = itemRank + 1;
        }
        value.clear();
        value.put(TYPE, Types.Signal.toString());
        value.put(PARENT_SEQUENCE, sequence);
        value.put(ITEM_RANK, itemRank);
        value.put(LIGHT, Lights.Red.toString());
        value.put(SOUND, 2);
        db.insert(SEQUENCE_ITEM_TABLE_NAME, null, value);
        itemRank = itemRank + 1;
    }
}
