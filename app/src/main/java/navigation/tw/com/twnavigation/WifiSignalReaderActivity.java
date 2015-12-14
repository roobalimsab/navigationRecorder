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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.net.wifi.WifiManager.RSSI_CHANGED_ACTION;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class WifiSignalReaderActivity extends Activity implements SensorEventListener {

    private static final String TAG = "WifiSingnalReader";
    private static final float MAGNETIC_FIELD_DELTA = 5;
    private static final long REFRESH_DURATION = 100;
    public static final int WIFI_SIGNAL_MAX_LEVELS = 25;
    public static final int MIN_WIFI_LEVEL = 10;
    public static final int SNAPSHOT_RECORD_LIMIT = 25;

    private LinearLayout networksView;
    private ScrollView scrollView;
    private TextView mfx;
    private TextView mfy;
    private SensorManager sensorManager;
    private Sensor magneticFieldSensor;
    private TextView mfz;

    private static final int MSG_FETCH_WIFI_STRENGTH = 5;
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

    private WifiSignals signalStrengths = new WifiSignals();

    private float[] previousMfValues;
    private LocationDatabase locationDatabase;
    private LinearLayout matchedLocationsView;
    private TextView recordLocationView;
    private LocationRecorder locationRecorder;
    private String recordingLocationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_signal_reader);
        setupViews();

        setupSensorReceivers();

        locationDatabase = new LocationDatabase(this);
        H.sendEmptyMessage(MSG_FETCH_WIFI_STRENGTH);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupWifiSignalReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceivers();
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

    private void unregisterReceivers() {
        unregisterReceiver(wifiReceiver);
    }

    private void setupViews() {
        scrollView = (ScrollView) findViewById(R.id.network_scroll);
        networksView = (LinearLayout) findViewById(R.id.network_signals);
        mfx = (TextView) findViewById(R.id.mfx);
        mfy = (TextView) findViewById(R.id.mfy);
        mfz = (TextView) findViewById(R.id.mfz);

        matchedLocationsView = (LinearLayout) findViewById(R.id.matched_locations);

        final EditText locationNameView = (EditText) findViewById(R.id.enter_location_name);
        recordLocationView = (TextView) findViewById(R.id.record_location);
        recordLocationView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                recordLocation(locationNameView.getText().toString().trim());
                recordLocationView.setText("Recording..");
            }
        });

        findViewById(R.id.delete_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationDatabase.removeAll();
            }
        });
    }

    private void recordLocation(String locationName) {
        if (locationName.isEmpty()) {
            return;
        }
        recordingLocationName = locationName;
        locationRecorder = new LocationRecorder();
        recordLocationView.setText("recording...");
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSignalStrength();
        }
    };

    private void startScan() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
    }

    private void readSignalStrength() {
        networksView.removeAllViews();
        matchedLocationsView.removeAllViews();
        signalStrengths.clear();

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        List<ScanResult> aps = wifiManager.getScanResults();
        addTextViewToLayout(networksView, "Found " + aps.size() + " APs");
        for (ScanResult ap : aps) {
            addSignal(ap);
        }

        signalStrengths.sortByLevel();
        findOutCurrentLocation(signalStrengths);
        recordFingerPrint();
        scrollView.fullScroll(View.FOCUS_DOWN);
        H.sendEmptyMessageDelayed(MSG_FETCH_WIFI_STRENGTH, REFRESH_DURATION);
    }

    private void recordFingerPrint() {
        if (locationRecorder != null) {
            int recordCount = locationRecorder.record(signalStrengths);
            if (recordCount >= SNAPSHOT_RECORD_LIMIT) {
                recordLocationView.setText("saving...");
                locationDatabase.recordLocation(new BuildingLocation(recordingLocationName, locationRecorder.stop()));
                recordLocationView.setText("Record");
                locationRecorder = null;
            } else {
                recordLocationView.setText("Recording..." + recordCount);
            }
        }
    }

    private void addSignal(ScanResult ap) {
        int level = WifiManager.calculateSignalLevel(ap.level, WIFI_SIGNAL_MAX_LEVELS);
        if (ap.SSID.equalsIgnoreCase("twguest") && level > MIN_WIFI_LEVEL) {
            addTextViewToLayout(networksView, ap.BSSID + " <-|-> " + level);
            signalStrengths.add(new WifiSignal(ap.BSSID, level));
        }
    }

    private void findOutCurrentLocation(WifiSignals currentSignals) {
        List<BuildingLocation> buildingLocations = locationDatabase.fetchRecordedLocations();
        addTextViewToLayout(matchedLocationsView, "db has " + buildingLocations.size() + " locations");
        List<MatchedLocation> matchingLocations = findMatch(buildingLocations, currentSignals);
        addTextViewToLayout(matchedLocationsView, "only " + matchingLocations.size() + " locations matched");
        displayMatchedLocation(matchingLocations);
    }

    private void displayMatchedLocation(List<MatchedLocation> matchingLocations) {
        if (matchingLocations.size() == 0) {
            addTextViewToLayout(matchedLocationsView, "not found");
        }

        Collections.sort(matchingLocations, new Comparator<MatchedLocation>() {
            @Override
            public int compare(MatchedLocation lhs, MatchedLocation rhs) {
                return Integer.compare(lhs.weight, rhs.weight);
            }
        });

        for (MatchedLocation matchedLocation : matchingLocations) {
            addTextViewToLayout(matchedLocationsView,
                    matchedLocation.location.getName() + " - " + matchedLocation.weight);
        }
    }

    private List<MatchedLocation> findMatch(
            List<BuildingLocation> buildingLocations,
            WifiSignals currentSignals) {
        Log.d(TAG, "currentSignals: " + currentSignals.toString());
        List<MatchedLocation> matchedLocations = new ArrayList<>();
        for (BuildingLocation location : buildingLocations) {
            Log.d(TAG, location.getName() + " ->: " + location.getWifiSignalsAsString());
            if (location.isMatching(currentSignals)) {
                matchedLocations.add(new MatchedLocation(location, location.getMatchWeight(currentSignals)));
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

            previousMfValues = event.values.clone();
        }
    }

    private boolean shouldReplaceReadings(SensorEvent event) {
        if (previousMfValues == null) {
            return true;
        }
        return Math.abs(event.values[0] - previousMfValues[0]) > MAGNETIC_FIELD_DELTA
                || Math.abs(event.values[1] - previousMfValues[1]) > MAGNETIC_FIELD_DELTA
                || Math.abs(event.values[2] - previousMfValues[2]) > MAGNETIC_FIELD_DELTA;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
