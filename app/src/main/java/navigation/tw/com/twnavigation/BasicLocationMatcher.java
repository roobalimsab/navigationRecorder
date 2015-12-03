package navigation.tw.com.twnavigation;


import android.util.Log;

public class BasicLocationMatcher {

    private static final String TAG = "LocationMatcher";
    private static final int STRENGTH_DELTA = 2;
    private static final int MINIMUM_WIFI_AP_MATCHING_DELTA = 3;

    public boolean isMatch(WifiSignals wifiSignals, WifiSignals currentSignals) {
        int empties = 0;
        int matches = 0;

        Log.d(TAG, "currentSignal: " + currentSignals.size() + " db signals : " + wifiSignals.size());

        for (WifiSignal signal : currentSignals) {
            int strength = signal.getLevel();
            String id = signal.getId();
            if (wifiSignals.findById(id) == null) {
                empties++;
            } else {
                int oldStrength = wifiSignals.findById(id).getLevel();
                if (Math.abs(strength - oldStrength) <= STRENGTH_DELTA) {
                    matches++;
                }
            }
        }
        Log.d(TAG, " empties : " + empties + " matches: " + matches);
        return (empties <= 3 && Math.abs(wifiSignals.size() - matches)  <= MINIMUM_WIFI_AP_MATCHING_DELTA);
    }
}
