package navigation.tw.com.twnavigation;

import android.app.Activity;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.net.wifi.WifiManager.RSSI_CHANGED_ACTION;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static navigation.tw.com.twnavigation.LocationDatabase.*;


public class WifiSignalReaderActivity extends Activity implements SensorEventListener {

    private static final String TAG = "WifiSingnalReader";
    private static final float MAGNETIC_FIELD_DELTA = 5;

    private LinearLayout networksView;
    private ScrollView scrollView;
    private TextView mfx;
    private TextView mfy;
    private SensorManager sensorManager;
    private Sensor magneticFieldSensor;
    private TextView mfz;

    private static final int MSG_FETCH_STRENGTH = 101;
    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FETCH_STRENGTH:
                    readSignalStrength();
                    break;
            }
        }
    };

    private Map<String, String> signalStrengths = new HashMap<>();

    private SensorEvent previousEvent;
    private float previousX;
    private float previousY;
    private float previousZ;

    private LocationDatabase locationDatabase;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_signal_reader);

        setupViews();

        setupWifiSignalReceivers();
        setupSensorReceivers();

        H.sendEmptyMessage(MSG_FETCH_STRENGTH);

        locationDatabase = new LocationDatabase(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupSensorReceivers() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void setupWifiSignalReceivers() {
        registerReceiver(wifiReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(RSSI_CHANGED_ACTION));
    }

    private void setupViews() {
        scrollView = (ScrollView) findViewById(R.id.network_scroll);
        networksView = (LinearLayout) findViewById(R.id.network_signals);
        mfx = (TextView) findViewById(R.id.mfx);
        mfy = (TextView) findViewById(R.id.mfy);
        mfz = (TextView) findViewById(R.id.mfz);

        final EditText locationNameView = (EditText) findViewById(R.id.enter_location_name);
        findViewById(R.id.record_location).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String locationName = locationNameView.getText().toString().trim();
                if (locationName.isEmpty()) {
                    return;
                }
                recordLocation(locationName);
            }
        });
    }

    private void recordLocation(String locationName) {
        SQLiteDatabase writableDatabase = locationDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, locationName);
        values.put(COLUMN_MFX, previousX);
        values.put(COLUMN_MFY, previousY);
        values.put(COLUMN_MFZ, previousZ);

        // i'm ashamed of this code
        int i = 1;
        for (String apName : signalStrengths.keySet()) {
            values.put("ap" + i + "name", apName);
            values.put("ap" + i + "value", signalStrengths.get(apName));
            i++;
            if (i > 5) {
                break;
            }
        }
        writableDatabase.insert(LOCATION_TABLE_NAME, COLUMN_NAME, values);
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSignalStrength();
        }
    };


    private void readSignalStrength() {
        networksView.removeAllViews();
        signalStrengths.clear();
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        List<ScanResult> aps = wifiManager.getScanResults();
        addTextViewToLayout("Found " + aps.size() + " APs");
        for (ScanResult ap : aps) {
            addNetwork(ap);
        }
        scrollView.fullScroll(View.FOCUS_DOWN);
        H.sendEmptyMessageDelayed(MSG_FETCH_STRENGTH, 500);
    }

    private void addNetwork(ScanResult ap) {
        if (ap.SSID.equalsIgnoreCase("twguest")) {
            int strength = WifiManager.calculateSignalLevel(ap.level, 50);
            addTextViewToLayout(ap.BSSID + " <-|-> " + strength);
            signalStrengths.put(ap.BSSID, String.valueOf(strength));
        }
    }

    private void addTextViewToLayout(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        networksView.addView(tv, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (shouldReplaceReadings(event)) {
            mfx.setText("x: " + event.values[0]);
            mfy.setText("y: " + event.values[1]);
            mfz.setText("z: " + event.values[2]);

            previousEvent = event;
            previousX = event.values[0];
            previousY = event.values[1];
            previousZ = event.values[2];
        }
    }

    private boolean shouldReplaceReadings(SensorEvent event) {
        if (previousEvent == null) {
            return true;
        }
        return Math.abs(event.values[0] - previousX) > MAGNETIC_FIELD_DELTA
                || Math.abs(event.values[1] - previousY) > MAGNETIC_FIELD_DELTA
                || Math.abs(event.values[2] - previousZ) > MAGNETIC_FIELD_DELTA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "WifiSignalReader Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://navigation.tw.com.twnavigation/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "WifiSignalReader Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://navigation.tw.com.twnavigation/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
