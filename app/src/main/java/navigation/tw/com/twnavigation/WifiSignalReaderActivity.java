package navigation.tw.com.twnavigation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.net.wifi.WifiManager.RSSI_CHANGED_ACTION;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class WifiSignalReaderActivity extends Activity implements SensorEventListener {

    private static final String TAG = "WifiSingnalReader";
    private static final float MAGNETIC_FIELD_DELTA = 5;
    private static final long REFRESH_DURATION = 200;
    public static final int WIFI_SIGNAL_MAX_LEVELS = 25;

    private LinearLayout networksView;
    private ScrollView scrollView;
    private TextView mfx;
    private TextView mfy;
    private SensorManager sensorManager;
    private Sensor magneticFieldSensor;
    private TextView mfz;

    private static final int MSG_FETCH_WIFI_STRENGTH = 101;
    Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FETCH_WIFI_STRENGTH:
                    startScan();
                    break;
            }
        }
    };

    private void startScan() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
    }

    private WifiSignals signalStrengths = new WifiSignals();

    private float[] previousEvent;
    private LocationDatabase locationDatabase;
    private LinearLayout matchedLocationsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_signal_reader);
        setupViews();

        setupWifiSignalReceivers();
        setupSensorReceivers();

        locationDatabase = new LocationDatabase(this);
        H.sendEmptyMessage(MSG_FETCH_WIFI_STRENGTH);
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

        matchedLocationsView = (LinearLayout) findViewById(R.id.matched_locations);

        final EditText locationNameView = (EditText) findViewById(R.id.enter_location_name);
        findViewById(R.id.record_location).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String locationName = locationNameView.getText().toString().trim();
                if (locationName.isEmpty()) {
                    return;
                }
                locationDatabase.recordLocation(
                        new BuildingLocation(locationName, signalStrengths, previousEvent));
            }
        });

        findViewById(R.id.delete_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDatabase.removeAll();
            }
        });
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSignalStrength();
        }
    };


    private void readSignalStrength() {
        networksView.removeAllViews();
        matchedLocationsView.removeAllViews();
        signalStrengths.clear();

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        List<ScanResult> aps = wifiManager.getScanResults();
        addTextViewToLayout(networksView, "Found " + aps.size() + " APs");
        for (ScanResult ap : aps) {
            addNetwork(ap);
        }
        findOutCurrentLocation(signalStrengths);
        scrollView.fullScroll(View.FOCUS_DOWN);
        H.sendEmptyMessageDelayed(MSG_FETCH_WIFI_STRENGTH, REFRESH_DURATION);
    }

    private void addNetwork(ScanResult ap) {
        if (ap.SSID.equalsIgnoreCase("twguest")) {
            int level = WifiManager.calculateSignalLevel(ap.level, WIFI_SIGNAL_MAX_LEVELS);
            addTextViewToLayout(networksView, ap.BSSID + " <-|-> " + level);
            signalStrengths.add(new WifiSignal(ap.BSSID, level));
        }
    }

    private void findOutCurrentLocation(WifiSignals currentSignals) {
        List<BuildingLocation> buildingLocations = locationDatabase.fetchRecordedLocations();
        addTextViewToLayout(matchedLocationsView, "db has " + buildingLocations.size() + " locations");
        List<BuildingLocation> matchingLocations = findMatch(buildingLocations, currentSignals);
        addTextViewToLayout(matchedLocationsView, "only " + matchingLocations.size() + " locations matched");
        displayMatchedLocation(matchingLocations);
    }

    private void displayMatchedLocation(List<BuildingLocation> matchingLocations) {
        if (matchingLocations.size() == 0) {
            addTextViewToLayout(matchedLocationsView, "not found");
        }

        for (BuildingLocation location : matchingLocations) {
            addTextViewToLayout(matchedLocationsView, location.getName());
        }
    }

    private List<BuildingLocation> findMatch(
            List<BuildingLocation> buildingLocations,
            WifiSignals currentSignals) {
        Log.d(TAG, "currentSignals: " + BuildingLocation.getWifiSignalsAsString(currentSignals));
        List<BuildingLocation> matchedLocations = new ArrayList<>();
        for(BuildingLocation location : buildingLocations) {
            Log.d(TAG, location.getName() + " ->: " + location.getWifiSignalsAsString());
            if (location.isMatching(currentSignals)) {
                matchedLocations.add(location);
            }
        }
        return matchedLocations;
    }

    private void addTextViewToLayout(LinearLayout layout, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        layout.addView(tv, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (shouldReplaceReadings(event)) {
            mfx.setText("x: " + event.values[0]);
            mfy.setText("y: " + event.values[1]);
            mfz.setText("z: " + event.values[2]);

            previousEvent = event.values.clone();
        }
    }

    private boolean shouldReplaceReadings(SensorEvent event) {
        if (previousEvent == null) {
            return true;
        }
        return Math.abs(event.values[0] - previousEvent[0]) > MAGNETIC_FIELD_DELTA
                || Math.abs(event.values[1] - previousEvent[1]) > MAGNETIC_FIELD_DELTA
                || Math.abs(event.values[2] - previousEvent[2]) > MAGNETIC_FIELD_DELTA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
