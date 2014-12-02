import java.util.Map;
/**
 * Class used to hold the direction info
 * Created by zewenfei on 11/29/14.
 */
public class Direction {
    public String route;
    // title of this direction
    public String title;
    // map from stop tag to stopId
    public Map<String, String> stops;
    // map from stop tag to stop title
    public Map<String, String> stopTitles;
    public Direction(String route, String title, Map<String, String> stops, Map<String, String> stopTitles) {
        this.route = route;
        this.title = title;
        this.stops = stops;
        this.stopTitles = stopTitles;
    }
}
