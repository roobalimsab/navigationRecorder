package navigation.tw.com.twnavigation;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.*;

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
    public static final String COLUMN_AP7_NAME = "ap7name";
    public static final String COLUMN_AP8_NAME = "ap8name";
    public static final String COLUMN_AP9_NAME = "ap9name";
    public static final String COLUMN_AP10_NAME = "ap10name";
    public static final String COLUMN_AP1_VALUE = "ap1value";
    public static final String COLUMN_AP2_VALUE = "ap2value";
    public static final String COLUMN_AP3_VALUE = "ap3value";
    public static final String COLUMN_AP4_VALUE = "ap4value";
    public static final String COLUMN_AP5_VALUE = "ap5value";
    public static final String COLUMN_AP6_VALUE = "ap6value";
    public static final String COLUMN_AP7_VALUE = "ap7value";
    public static final String COLUMN_AP8_VALUE = "ap8value";
    public static final String COLUMN_AP9_VALUE = "ap9value";
    public static final String COLUMN_AP10_VALUE = "ap10value";

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

    public void recordLocation(BuildingLocation location) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(
                LOCATION_TABLE_NAME,
                COLUMN_NAME,
                constructLocationValues(location));
    }

    @NonNull
    private ContentValues constructLocationValues(BuildingLocation location) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, location.getName());
        values.put(COLUMN_MFX, location.getMfx());
        values.put(COLUMN_MFY, location.getMfy());
        values.put(COLUMN_MFZ, location.getMfz());

        // Ugly temporary code
        int i = 1;
        for (String apName : location.getWifiSignals().keySet()) {
            values.put("ap" + i + "name", apName);
            values.put("ap" + i + "value", location.getWifiSignals().get(apName));
            i++;
            if (i > 5) {
                break;
            }
        }
        return values;
    }

    public List<BuildingLocation> fetchRecordedLocations() {
        List<BuildingLocation> locations = new ArrayList<>();
        Cursor c = getCursor();
        if (c == null) {
            return locations;
        }

        if (c.moveToFirst()) {
            locations.add(getCurrentLocaiton(c));
        }
        while(c.moveToNext()) {
            locations.add(getCurrentLocaiton(c));
        }
        c.close();
        return locations;
    }

    private BuildingLocation getCurrentLocaiton(Cursor c) {
        String name = c.getString(c.getColumnIndex(COLUMN_NAME));
        String mfx = c.getString(c.getColumnIndex(COLUMN_MFX));
        String mfy = c.getString(c.getColumnIndex(COLUMN_MFY));
        String mfz = c.getString(c.getColumnIndex(COLUMN_MFZ));
//        String ap1Name = c.getString(c.getColumnIndex(COLUMN_AP1_NAME));
//        String ap2Name = c.getString(c.getColumnIndex(COLUMN_AP2_NAME));
//        String ap3Name = c.getString(c.getColumnIndex(COLUMN_AP3_NAME));
//        String ap4Name = c.getString(c.getColumnIndex(COLUMN_AP4_NAME));
//        String ap5Name = c.getString(c.getColumnIndex(COLUMN_AP5_NAME));
//        String ap6Name = c.getString(c.getColumnIndex(COLUMN_AP6_NAME));
//        String ap1Value = c.getString(c.getColumnIndex(COLUMN_AP1_VALUE));
//        String ap2Value = c.getString(c.getColumnIndex(COLUMN_AP2_VALUE));
//        String ap3Value = c.getString(c.getColumnIndex(COLUMN_AP3_VALUE));
//        String ap4Value = c.getString(c.getColumnIndex(COLUMN_AP4_VALUE));
//        String ap5Value = c.getString(c.getColumnIndex(COLUMN_AP5_VALUE));
//        String ap6Value = c.getString(c.getColumnIndex(COLUMN_AP6_VALUE));

        Map<String, String> wifiSignals = new HashMap<>();
        for (int i = 1; i <= 6; i++) {
            String apName = c.getString(c.getColumnIndex("ap" + i + "name"));
            String apValue = c.getString(c.getColumnIndex("ap" + i + "value"));

            if (apName != null && apValue != null) {
                wifiSignals.put(apName, apValue);
            }
        }

        return new BuildingLocation(
                name,
                wifiSignals,
                new float[]{Float.valueOf(mfx), Float.valueOf(mfy), Float.valueOf(mfz)});
    }

    private Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_NAME,
                COLUMN_MFX,
                COLUMN_MFY,
                COLUMN_MFZ,
                COLUMN_AP1_NAME,
                COLUMN_AP2_NAME,
                COLUMN_AP3_NAME,
                COLUMN_AP4_NAME,
                COLUMN_AP5_NAME,
                COLUMN_AP6_NAME,
                COLUMN_AP1_VALUE,
                COLUMN_AP2_VALUE,
                COLUMN_AP3_VALUE,
                COLUMN_AP4_VALUE,
                COLUMN_AP5_VALUE,
                COLUMN_AP6_VALUE,
        };

        return db.query(
                LOCATION_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }
}
