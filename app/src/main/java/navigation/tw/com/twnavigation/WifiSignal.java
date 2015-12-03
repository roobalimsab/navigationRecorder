package navigation.tw.com.twnavigation;

public class WifiSignal {
    public String id;
    public int level;

    public WifiSignal(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public String getId() {
        return id;
    }
}
