package navigation.tw.com.twnavigation;

public class MatchedLocation {
    public BuildingLocation location;
    public int weight;

    public MatchedLocation(BuildingLocation location, int weight) {
        this.location = location;
        this.weight = weight;
    }
}
