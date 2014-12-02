import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Class used to test the FIND command of the app
 * Created by zewenfei on 12/1/14.
 */
public class FindTest {
    public static void main(String args[]) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        //test from Union Square to ATT park
        double slat = 37.788011, slon = -122.407433, dlat = 37.778736, dlon = -122.389292;
        String agencyTag = "sf-muni";
        FindStopRoute service = FindStopRouteManager.getInstance(agencyTag);
        Map<String, Map<String, String>> routeStopPairs = service.getAllStopRoutePair(slat, slon, dlat, dlon);
        System.out.println("----------------------------------------------");
        Map<String, String> agencyList = MockDB.getInstance().getAgencyList();
        System.out.print("Agency: ");
        outputInfo(agencyTag, agencyList.get(agencyTag));
        Map<String, String> routeList = MockDB.getInstance().getRouteList(agencyTag);
        for(String route : routeStopPairs.keySet()) {
            System.out.print("Route: ");
            outputInfo(route, routeList.get(route));
            Map<String, String> routeStops = routeStopPairs.get(route);
            Map<String,Stop> stopList = MockDB.getInstance().getRouteStops(agencyTag, route);
            System.out.println("Stops: ");
            for (String start: routeStops.keySet()) {
                System.out.print("From ");
                outputInfo(start, stopList.get(start).title);
                System.out.print("To ");
                String end = routeStops.get(start);
                outputInfo(end, stopList.get(end).title);
            }
            System.out.println("----------------------------------------------");
        }
    }
    public static void outputInfo(String key, String value) {
        System.out.println("(" + key + ")" + value);
    }
}
