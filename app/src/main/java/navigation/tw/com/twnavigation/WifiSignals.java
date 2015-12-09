package navigation.tw.com.twnavigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    public WifiSignals sortByLevel() {
       Collections.sort(this, new Comparator<WifiSignal>() {
           @Override
           public int compare(WifiSignal lhs, WifiSignal rhs) {
               return Integer.compare(lhs.getLevel(), rhs.getLevel());
           }
       });
        return this;
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
        if (wifiSignalString.length() > 2) {
            wifiSignalString = wifiSignalString.substring(0, wifiSignalString.length() - 2);
        }
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
        return wifiSignals.sortByLevel();
    }
}
