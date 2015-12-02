package navigation.tw.com.twnavigation;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class BuildingLocation {
    private static final int STRENGTH_DELTA = 4;
    private static final String TAG = "BuildingLocation";
    String name;
    float[] mfValues;
    Map<String, String > wifiSignals = new HashMap<>();

    BuildingLocation(String locationName, Map<String, String> signals, float[] mfValues) {
        this.name = locationName;
        this.wifiSignals = signals;
        this.mfValues = mfValues;
    }

    public String getName() {
        return name;
    }

    public float getMfx() {
        return mfValues[0];
    }

    public float getMfy() {
        return mfValues[1];
    }

    public float getMfz() {
        return mfValues[2];
    }

    public Map<String, String> getWifiSignals() {
        return wifiSignals;
    }

    public boolean isMatching(Map<String, String> currentSignals) {
        int empties = 0;
        int matches = 0;

        Log.d(TAG, "currentSignal: " + currentSignals.size() + " db signals : " + wifiSignals.size());

        for (String apName : currentSignals.keySet()) {
            int strength = Integer.valueOf(currentSignals.get(apName));
            if (this.wifiSignals.get(apName) == null) {
                Log.d(TAG, "empty for " + apName);
                empties++;
            } else {
                int oldStrength = Integer.valueOf(this.wifiSignals.get(apName));
                if (Math.abs(strength - oldStrength) <= STRENGTH_DELTA) {
                    matches++;
                }
                Log.d(TAG, "id: " + apName + " old: " + oldStrength + " new : " + strength);
            }
        }
        Log.d(TAG, " empties : " + empties + " matches: " + matches);
        return (/*empties <= 2 && */ matches >=3);
    }
}
