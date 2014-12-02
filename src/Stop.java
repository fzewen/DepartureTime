/**
 * Class used to hold stop info
 * Created by zewenfei on 11/28/14.
 */
public class Stop {
    public String route;
    public String stopTag;
    public String stopId;
    public String title;
    public double lat;
    public double lon;
    public Stop (String route, String stopTag, String stopId, String title, double lat, double lon) {
        this.route = route;
        this.stopTag = stopTag;
        this.stopId = stopId;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
    }
}
