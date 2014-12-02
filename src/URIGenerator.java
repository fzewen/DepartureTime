import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class used to generate the request URLs
 * Created by zewenfei on 11/28/14.
 */
public class URIGenerator {

    private static final String NEXT_BUS_ENDPOINT = "http://webservices.nextbus.com/service/publicXMLFeed";
    private static final String COMMAND = "command";
    private static final String PREDICTIONS = "predictions";
    private static final String AGENCY_LIST = "agencyList";
    private static final String ROUTE_LIST = "routeList";
    private static final String ROUTE_CONFIG = "routeConfig";
    private static final String AGENCY ="a";
    private static final String ROUTE = "r";
    private static final String STOP_TAG = "s";
    private static final String STOP_ID = "stopId";
    private static final String TERSE = "terse";

    /**
     * Method to generate prediction info URL
     * @param agency agency tag
     * @param route route tag
     * @param stopTag stop tag
     * @return URL to retrive the prediction info for this route at this stop
     * @throws URISyntaxException
     */
    public static URI getPredictionURI(String agency, String route, String stopTag) throws URISyntaxException {
        URIBuilder urlBuilder = new URIBuilder();
        urlBuilder.setPath(NEXT_BUS_ENDPOINT);
        urlBuilder.addParameter(COMMAND, PREDICTIONS);
        urlBuilder.addParameter(AGENCY, agency);
        urlBuilder.addParameter(ROUTE, route);
        urlBuilder.addParameter(STOP_TAG, stopTag);
        return urlBuilder.build();
    }

    /**
     * Method to generate agency list URL
     * @return URL to retrieve agency list
     * @throws URISyntaxException
     */
    public static URI getAgencyList() throws URISyntaxException {
        URIBuilder urlBuilder = new URIBuilder();
        urlBuilder.setPath(NEXT_BUS_ENDPOINT);
        urlBuilder.addParameter(COMMAND, AGENCY_LIST);
        return urlBuilder.build();
    }

    /**
     * Method to generate route list URL
     * @param agency agency tag
     * @return URL to retrieve route list info for this agency
     * @throws URISyntaxException
     */
    public static URI getRouteList(String agency) throws URISyntaxException {
        URIBuilder urlBuilder = new URIBuilder();
        urlBuilder.setPath(NEXT_BUS_ENDPOINT);
        urlBuilder.addParameter(COMMAND, ROUTE_LIST);
        urlBuilder.addParameter(AGENCY, agency);
        return urlBuilder.build();
    }

    /**
     * Method to generate route config URL
     * @param agency agency tag
     * @param route route tag
     * @param terse used to discard the path info from the xml retrieved
     * @return URL to retrieve route config info for a route
     * @throws URISyntaxException
     */
    public static URI getRouteConfig(String agency, String route, boolean terse) throws URISyntaxException {
        URIBuilder urlBuilder = new URIBuilder();
        urlBuilder.setPath(NEXT_BUS_ENDPOINT);
        urlBuilder.addParameter(COMMAND, ROUTE_CONFIG);
        urlBuilder.addParameter(AGENCY, agency);
        urlBuilder.addParameter(ROUTE, route);
        if (terse) {
            urlBuilder.addParameter(TERSE, "");
        }
        return urlBuilder.build();
    }

    /**
     * Method to generate stop routes URL
     * @param agency agency tag
     * @param stopId stopId for a physical stop
     * @return URL to retrieve all the routes for a stop
     * @throws URISyntaxException
     */
    public static URI getStopRoutes(String agency, String stopId) throws URISyntaxException {
        URIBuilder urlBuilder = new URIBuilder();
        urlBuilder.setPath(NEXT_BUS_ENDPOINT);
        urlBuilder.addParameter(COMMAND, PREDICTIONS);
        urlBuilder.addParameter(AGENCY, agency);
        urlBuilder.addParameter(STOP_ID, stopId);
        return urlBuilder.build();
    }
}
