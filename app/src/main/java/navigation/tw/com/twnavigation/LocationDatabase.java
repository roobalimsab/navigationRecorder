package navigation.tw.com.twnavigation;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDatabase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String LOCATION_TABLE_NAME = "location";
    public static final String DATABASE_NAME = "location_db";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MFX = "mfx";
    public static final String COLUMN_MFY = "mfy";
    public static final String COLUMN_MFZ = "mfz";
    public static final String COLUMN_AP1_NAME = "ap1name";
    public static final String COLUMN_AP2_NAME = "ap2name";
    public static final String COLUMN_AP3_NAME = "ap3name";
    public static final String COLUMN_AP4_NAME = "ap4name";
    public static final String COLUMN_AP5_NAME = "ap5name";
    public static final String COLUMN_AP6_NAME = "ap6name";
    public static final String COLUMN_AP1_VALUE = "ap1value";
    public static final String COLUMN_AP2_VALUE = "ap2value";
    public static final String COLUMN_AP3_VALUE = "ap3value";
    public static final String COLUMN_AP4_VALUE = "ap4value";
    public static final String COLUMN_AP5_VALUE = "ap5value";
    public static final String COLUMN_AP6_VALUE = "ap6value";

    private static final String LOCATION_TABLE_CREATE =
            "CREATE TABLE " + LOCATION_TABLE_NAME + " (" +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_MFX + " TEXT, " + COLUMN_MFY + " TEXT, " + COLUMN_MFZ + " TEXT, " +
                    COLUMN_AP1_NAME + " TEXT, " + COLUMN_AP1_VALUE + " TEXT, " +
                    COLUMN_AP2_NAME + " TEXT, " + COLUMN_AP2_VALUE + " TEXT, " +
                    COLUMN_AP3_NAME + " TEXT, " + COLUMN_AP3_VALUE + " TEXT, " +
                    COLUMN_AP4_NAME + " TEXT, " + COLUMN_AP4_VALUE + " TEXT, " +
                    COLUMN_AP5_NAME + " TEXT, " + COLUMN_AP5_VALUE + " TEXT, " +
                    COLUMN_AP6_NAME + " TEXT, " + COLUMN_AP6_VALUE + " TEXT " + ");";

    LocationDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
