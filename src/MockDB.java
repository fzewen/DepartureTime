import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * A singleton class used to mock a database for now, should replace the logic with real MySQL and Hibernate later
 * Created by zewenfei on 11/28/14.
 */
public class MockDB {

    // A map from agency tag to agency title
    private Map<String, String> agencyList;
    // A map whose key are agency tag and whose values are map from route tag to route title
    private Map<String, Map<String, String>> routeList;
    // A map from agency tag, route tag, direction tag to Direction instances
    private Map<String, Map<String, Map<String, Direction>>> directionList;
    // A map from agency tag, route tag, direction tag to Stop instances
    private Map<String, Map<String, Map<String, Stop>>> stopList;
    // A map from agency tag, route tag to visited tag, used to check whether we've retrieved the info from the server before
    private Map<String, Map<String, Boolean>> routeVisitList;
    // A map from agency tag, stopId to a collection of route tag for this stop
    private Map<String, Map<String, Collection<String>>> stopRoutesList;
    private static MockDB instance;

    private MockDB() {
        agencyList = new HashMap<String, String>();
        routeList = new HashMap<String, Map<String, String>>();
        directionList = new HashMap<String, Map<String, Map<String, Direction>>>();
        stopList = new HashMap<String, Map<String, Map<String, Stop>>>();
        routeVisitList = new HashMap<String, Map<String, Boolean>>();
        stopRoutesList = new HashMap<String, Map<String, Collection<String>>>();
    }

    public static MockDB getInstance() {
        if(instance == null)
            instance = new MockDB();
        return instance;
    }

    /**
     * Method to retrieve a list of agency
     * @return  a list of agency tag
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Map<String, String> getAgencyList() throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        if(agencyList.isEmpty()) {
           String url = URIGenerator.getAgencyList().toString();
           agencyList = XMLParser.parseAgency(url);
        }
        return agencyList;
    }

    /**
     * Method to retrieve a list of routes given an agency
     * @param agency agency tag
     * @return a list from route tags to route title
     */
    public Map<String, String> getRouteList(String agency) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        Map<String, String> route = null;
        if (!routeList.containsKey(agency)) {
            String url = URIGenerator.getRouteList(agency).toString();
            route = XMLParser.parseRouteList(url);
        } else {
            route = routeList.get(agency);
        }
        return route;
    }

    /**
     * Method to fill out the route info given an agency and a route
     * Thie method is used after checkRouteVisit to assure one-time info fill-out
     * @param agency agency tag
     * @param route route tag
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void getRouteInfo(String agency, String route) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        String url = URIGenerator.getRouteConfig(agency, route, true).toString();
        Object[] ans = XMLParser.parseRouteInfo(url);
        Map<String, Stop> routeStopList = (Map<String, Stop>) ans[0];
        Map<String, Direction> routeDirectionList = (Map<String, Direction>) ans[1];
        Map<String, Map<String, Stop>> stops = stopList.get(agency);
        stops.put(route, routeStopList);
        Map<String, Map<String, Direction>> routes = directionList.get(agency);
        routes.put(route, routeDirectionList);
    }

    /**
     * Method used to check whether we have retrieve the route info.
     * Need to use in sequence with getRouteInfo. Since we will set the entry to "visited"
     * @param agency agency tag
     * @param route route tag
     * @return
     */
    public boolean checkRouteVisit(String agency, String route) {
        boolean visited = false;
        Map<String, Boolean> routes = null;
        if (!routeVisitList.containsKey(agency)) {
            routes = new HashMap<String, Boolean>();
            directionList.put(agency, new HashMap<String, Map<String, Direction>>());
            stopList.put(agency, new HashMap<String, Map<String, Stop>>());
        } else {
            routes = routeVisitList.get(agency);
        }
        if(routes.containsKey(route)) {
            visited = true;
        } else {
            routes.put(route, true);
        }
        return visited;
    }

    /**
     * Method to retrieve a collection of routes for a stopId given its agency
     * @param agency agency tag
     * @param stopId stopId for the physical stop
     * @return a collection of route tag for this stopId
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Collection<String> getStopRoutes(String agency, String stopId) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        Map<String, Collection<String>> stopRoutes = null;
        if(!stopRoutesList.containsKey(agency)) {
            stopRoutes = new HashMap<String, Collection<String>>();
            stopRoutesList.put(agency, stopRoutes);
        } else {
            stopRoutes = stopRoutesList.get(agency);
        }
        Collection<String> routes = null;
        if(stopRoutes.containsKey(stopId)) {
            routes = stopRoutes.get(stopId);
        } else {
            String url = URIGenerator.getStopRoutes(agency, stopId).toString();
            routes = XMLParser.parseStopRoutes(url);
            stopRoutes.put(stopId, routes);
        }
        return routes;
    }

    /**
     * Method to retrieve all the direction info for a route given its agency
     * @param agency agency tag
     * @param route route tag
     * @return a collection of Direction instances for this route
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws URISyntaxException
     */
    public Map<String, Direction> getRouteDirections(String agency, String route) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        boolean visit = checkRouteVisit(agency, route);
        if(!visit) {
            getRouteInfo(agency, route);
        }
        Map<String, Direction> directions = directionList.get(agency).get(route);
        return directions;
    }

    /**
     * Method to retrieve all the stops for a route given its agency
     * @param agency agency tag
     * @param route route tag
     * @return a collection of Stop instances for this route
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws URISyntaxException
     */
    public Map<String, Stop> getRouteStops(String agency, String route) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        boolean visit = checkRouteVisit(agency, route);
        if(!visit) {
            getRouteInfo(agency, route);
        }
        Map<String, Stop> stops = stopList.get(agency).get(route);
        return stops;
    }

    /**
     * Method to retrieve all the stops for a given agency
     * @param agency agency tag
     * @return a collection of Stop instances
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws URISyntaxException
     */
    public Map<String, Stop> getAgencyStops(String agency) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        Map<String, String> routeList = getRouteList(agency);
        Set<String> routeTags = routeList.keySet();
        Map<String, Stop> stops = new HashMap<String, Stop>();
        for(String routeTag : routeTags) {
            Collection<Stop> routeStop = getRouteStops(agency, routeTag).values();
            for(Stop stop : routeStop) {
                if(!stops.containsKey(stop.stopId)) {
                    stops.put(stop.stopId, stop);
                }
            }
        }
        return stops;
    }
}
