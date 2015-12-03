package navigation.tw.com.twnavigation;


public class BuildingLocation {
    private static final String TAG = "BuildingLocation";
    private String name;
    private float[] mfValues;
    private WifiSignals wifiSignals = new WifiSignals();
    private BasicLocationMatcher matcher = new BasicLocationMatcher();

    BuildingLocation(String locationName, String signalString, float[] mfValues) {
        this.name = locationName;
        this.wifiSignals = WifiSignals.fromString(signalString);
        this.mfValues = mfValues;
    }

    BuildingLocation(String locationName, WifiSignals signals, float[] mfValues) {
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

    public boolean isMatching(WifiSignals currentSignals) {
        return matcher.isMatch(wifiSignals, currentSignals);
    }

    public String getWifiSignalsAsString() {
        return wifiSignals.toString();
    }




}
