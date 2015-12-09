package navigation.tw.com.twnavigation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationRecorder {

    public static final int SNAPSHOT_RECORD_LIMIT = 25;
    private boolean isRecording = false;
    private List<WifiSignals> snapshots = new ArrayList<>();
    private String recordingLocationName;
    private LocationDatabase locationDatabase;

    public LocationRecorder(LocationDatabase locationDatabase) {
        this.locationDatabase = locationDatabase;
    }

    public void startRecording(String name) {
        isRecording = true;
        recordingLocationName = name;
    }

    public boolean record(WifiSignals wifiSignals) {
        if (isRecording) {
            if (snapshots.size() >= SNAPSHOT_RECORD_LIMIT) {
                WifiSignals avgSignals = findAverageSignal(snapshots);
                locationDatabase.recordLocation(
                        new BuildingLocation(recordingLocationName, avgSignals));
                recordingLocationName = null;
                snapshots.clear();
                isRecording = false;
                return true;
            } else {
                snapshots.add(wifiSignals);
                return false;
            }
        }
        return true;
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
            if (levels.size() >= SNAPSHOT_RECORD_LIMIT) {
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
