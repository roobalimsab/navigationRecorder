package navigation.tw.com.twnavigation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationRecorder {

    private List<WifiSignals> snapshots = new ArrayList<>();

    public int record(WifiSignals wifiSignals) {
        snapshots.add(wifiSignals);
        return snapshots.size();
    }

    public WifiSignals stop() {
        return findAverageSignal(snapshots);
    }

    public WifiSignals findAverageSignal(List<WifiSignals> snapshots) {
        Map<String, List<Integer>> levelsMap = new HashMap<>();
        for (WifiSignals wifiSignals : snapshots) {
            for (WifiSignal signal : wifiSignals) {
                List<Integer> levels = levelsMap.get(signal.getId());
                if (levels == null) {
                    levels = new ArrayList<>();
                    levelsMap.put(signal.getId(), levels);
                }
                levels.add(signal.getLevel());
            }
        }

        WifiSignals averageSignals = new WifiSignals();
        for (String id : levelsMap.keySet()) {
            List<Integer> levels = levelsMap.get(id);
            if (levels.size() == snapshots.size()) {
                averageSignals.add(new WifiSignal(id, findAverage(levels)));
            }
        }
        return averageSignals;
    }

    private Integer findAverage(List<Integer> levels) {
        int levelSum = 0;
        for (Integer level : levels) {
            levelSum += level;
        }
        return levelSum / levels.size();
    }
}
