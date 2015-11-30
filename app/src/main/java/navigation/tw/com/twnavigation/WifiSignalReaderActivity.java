package navigation.tw.com.twnavigation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.net.wifi.WifiManager.RSSI_CHANGED_ACTION;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class WifiSignalReaderActivity extends Activity {

    private static final String TAG = "WifiSingnalReader";
    private LinearLayout networksView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_signal_reader);

        scrollView = (ScrollView) findViewById(R.id.network_scroll);
        networksView = (LinearLayout) findViewById(R.id.network_signals);

        registerReceiver(wifiReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(RSSI_CHANGED_ACTION));
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readSignalStrength();
        }
    };

    private void readSignalStrength() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        List<ScanResult> aps = wifiManager.getScanResults();
        addTextViewToLayout("Found " + aps.size() + " APs");
        for (ScanResult ap: aps) {
            Log.d(TAG, "BSSID: " + ap.BSSID);
            Log.d(TAG, "level: " + ap.level);
            Log.d(TAG, "freequency: " +ap.frequency);
            addNetwork(ap);
        }
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

    private void addNetwork(ScanResult ap) {
        addTextViewToLayout("name: " + ap.SSID + " id: " + ap.BSSID + " f: " + ap.frequency + " l: " + ap.level);
    }

    private void addTextViewToLayout(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        networksView.addView(tv, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }

}
