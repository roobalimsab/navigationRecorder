package navigation.tw.com.twnavigation;

import java.util.ArrayList;

public class WifiSignals extends ArrayList<WifiSignal> {
    public static final String SIGNAL_SEPERATOR = "__";
    public static final String VALUE_SEPERATOR = "--";

    public WifiSignal findById(String id) {
        for (WifiSignal signal : this) {
            if (signal.getId().equalsIgnoreCase(id)) {
                return signal;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return asString(this);
    }

    public static String asString(WifiSignals wifiSignals) {
        String wifiSignalString = "";
        for (WifiSignal signal : wifiSignals) {
            wifiSignalString = wifiSignalString + signal.getId() + VALUE_SEPERATOR + signal.getLevel() + SIGNAL_SEPERATOR;
        }
        wifiSignalString = wifiSignalString.substring(0, wifiSignalString.length() - 2);
        return wifiSignalString;
    }

    public static WifiSignals fromString(String wifiSignalString) {
//        Log.d(TAG, "wifiSignalStrengthAsString: " + wifiSignalString);
        WifiSignals wifiSignals = new WifiSignals();
        String[] values = wifiSignalString.split(SIGNAL_SEPERATOR);
        for(String value :  values) {
            String[] keyValue = value.split(VALUE_SEPERATOR);
            wifiSignals.add(new WifiSignal(keyValue[0], Integer.valueOf(keyValue[1])));
        }
        return wifiSignals;
    }
}
