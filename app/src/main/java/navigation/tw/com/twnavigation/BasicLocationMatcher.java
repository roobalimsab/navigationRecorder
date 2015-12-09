package navigation.tw.com.twnavigation;


import android.util.Log;

public class BasicLocationMatcher {

    private static final String TAG = "LocationMatcher";
    private static final int STRENGTH_DELTA = 2;
    private static final int MINIMUM_LEVEL_MATCHES = 3;
    private static final int MAXIMUM_EMPTY_MATCHES = 4;

    public boolean isMatch(WifiSignals wifiSignals, WifiSignals currentSignals) {
        int empties = 0;
        int matches = 0;
        int deltaSum = 0;

        //System.out.println("currentSignal: " + currentSignals.size() + " db signals : " + wifiSignals.size());

        for (WifiSignal signal : currentSignals) {
            int strength = signal.getLevel();
            String id = signal.getId();
            if (wifiSignals.findById(id) == null) {
                empties++;
            } else {
                int oldStrength = wifiSignals.findById(id).getLevel();
//                System.out.println(" id: " + id + " saved : " + oldStrength + " current: " + strength);
                if (Math.abs(strength - oldStrength) <= STRENGTH_DELTA) {
                    matches++;
//                    System.out.println("delta " + Math.abs(strength - oldStrength));
                    deltaSum += oldStrength;
                }
            }
        }
        System.out.println("empties : " + empties + " matches: " + matches + " deltaSum: " + deltaSum);
        return (empties <= MAXIMUM_EMPTY_MATCHES && matches >= MINIMUM_LEVEL_MATCHES);
    }

    public int findWeight(WifiSignals wifiSignals, WifiSignals currentSignals) {
        int deltaSum = 0;
        for (WifiSignal signal : currentSignals) {
            int strength = signal.getLevel();
            String id = signal.getId();
            if (wifiSignals.findById(id) != null) {
                int oldStrength = wifiSignals.findById(id).getLevel();
                if (Math.abs(strength - oldStrength) <= STRENGTH_DELTA) {
                    deltaSum += oldStrength;
                }
            }
        }
        return deltaSum;
    }
}
