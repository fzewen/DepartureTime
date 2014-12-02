import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/** A singleton class to retrieve FindStopRoute instance based on specific agency
 * Created by zewenfei on 11/30/14.
 */
public class FindStopRouteManager {
    // A map to hold all the FindStopRoute instance for different agency
    private static Map<String, FindStopRoute> stopRoutes;
    private static FindStopRouteManager instance;

    private FindStopRouteManager() {
        stopRoutes = new HashMap<String, FindStopRoute>();
    }

    /**
     * Method to retrieve FindStopRoute instance based on specific agency
     * @param agency agency tag
     * @return the FindStopRoute instance for this agency
     */
    public static FindStopRoute getInstance(String agency) throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        if(instance == null) {
            instance = new FindStopRouteManager();
        }
        FindStopRoute stopRoute = null;
        if (stopRoutes.containsKey(agency)) {
            stopRoute = stopRoutes.get(agency);
        } else {
            stopRoute = new FindStopRoute(agency);
            stopRoutes.put(agency, stopRoute);
        }
        return stopRoute;
    }

}
