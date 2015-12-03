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
    public static final String COLUMN_WIFI_SIGNALS = "wifiSignals";

    private static final String LOCATION_TABLE_CREATE =
            "CREATE TABLE " + LOCATION_TABLE_NAME + " (" +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_MFX + " TEXT, " + COLUMN_MFY + " TEXT, " + COLUMN_MFZ + " TEXT, " +
                    COLUMN_WIFI_SIGNALS + " TEXT" + ");";

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
        values.put(COLUMN_WIFI_SIGNALS, location.getWifiSignalsAsString());
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
        String wifiSignalString = c.getString(c.getColumnIndex(COLUMN_WIFI_SIGNALS));

        return new BuildingLocation(
                name,
                wifiSignalString,
                new float[]{Float.valueOf(mfx), Float.valueOf(mfy), Float.valueOf(mfz)});
    }

    private Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                COLUMN_NAME,
                COLUMN_MFX,
                COLUMN_MFY,
                COLUMN_MFZ,
                COLUMN_WIFI_SIGNALS,
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

    public void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LOCATION_TABLE_NAME, null, null);
    }
}
