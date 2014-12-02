import net.sf.javaml.core.kdtree.KDTree;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Class used to find the nearest stop and the route from source to destination under the condition of using
 * service from a specific agency
 * Created by zewenfei on 11/28/14.
 */
public class FindStopRoute {

    private String agency;
    // Dimesion for the KD tree
    private int DIMENSION = 2;
    // The max number of stops we will return given a location
    private int MAX_NEAREST_STOPS = 20;
    // The max walking distance from the given location to stops
    private double MAX_WALK_DISTANCE = 0.5d;
    // The Kd tree instance used to calculate the nearest neighbor
    private KDTree kdTree;

    public FindStopRoute(String agency) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        this.agency = agency;
        kdTree = new KDTree(DIMENSION);
        initializeKdTree(kdTree);
    }

    /**
     * Method to get all stops info and used the info to build KD tree
     * @param kdTree the kdTree to store all the stops
     * @throws URISyntaxException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private void initializeKdTree(KDTree kdTree) throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        Collection<Stop> stops = MockDB.getInstance().getAgencyStops(agency).values();
        for(Stop stop : stops) {
            double[] location = {stop.lat, stop.lon};
            kdTree.insert(location, stop);
        }
    }

    /**
     * Method to find MAX_NEAREST_STOPS number of nearest stops give a  location
     * @param lat the latitude of a given location
     * @param lon the longitude of a given location
     * @return a list of stop instance of the nearest stop
     */
    public Collection<Stop> findNearestStops(double lat, double lon) {
        Collection<Stop> nearestStops = new ArrayList<Stop>();
        double[] point = {lat, lon};
        Object[] stops = kdTree.nearest(point, MAX_NEAREST_STOPS);
        for(Object ostop : stops) {
            // we calculate the distance from the location to all those stops retrieve above, discard those that exceed the max walking distance
            Stop stop = (Stop) ostop;
            if (distance(lat, lon, stop.lat, stop.lon) <= MAX_WALK_DISTANCE) {
                nearestStops.add(stop);
            }
        }
        return nearestStops;
    }

    /**
     * Method to calculate the distance in miles from the two locations
     * @param fromLat the latitude of the source
     * @param fromLon the longitude of the source
     * @param toLat the latitude of the destination
     * @param toLon the longitude of the destination
     * @return the distance in miles of the two points
     */
    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        GeodeticCalculator geoCalc = new GeodeticCalculator();
        Ellipsoid reference = Ellipsoid.WGS84;
        GlobalCoordinates from = new GlobalCoordinates(fromLat, fromLon);
        GlobalCoordinates to = new GlobalCoordinates(toLat, toLon);

        GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(reference, from, to);
        double ellipseKilometers = geoCurve.getEllipsoidalDistance() / 1000.0;
        double ellipseMiles = ellipseKilometers * 0.621371192;
        return ellipseMiles;
    }

    /**
     basically loop to get routes of the fromStops: stop_a [r1, r3, r5]
     loop to get routes of the toStop: stop_b [r1, r2]
     check whether the routes has intersection && [r1]
     for those common routes check whether the stops are included from one direction: r1[stop_a, stop_b]
     **/
    public Map<String, Map<String, String>> getAllStopRoutePair(double fromLat, double fromLon, double toLat, double toLon) throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        Map<String, Map<String, String>> routeStopPairs = new HashMap<String, Map<String, String>>();
        Collection<Stop> fromStops = findNearestStops(fromLat, fromLon);
        Collection<Stop> toStops = findNearestStops(toLat, toLon);

        for(Stop fromStop : fromStops) {
            Collection<String> fromStopRoutes = MockDB.getInstance().getStopRoutes(agency, fromStop.stopId);
            for(Stop toStop : toStops) {
                Collection<String> temp = new ArrayList<String>(fromStopRoutes);
                Collection<String> toStopRoutes = MockDB.getInstance().getStopRoutes(agency, toStop.stopId);
                temp.retainAll(toStopRoutes);
                    for(String route : temp) {
                        if(routeStopPairs.containsKey(route))
                            continue;
                        Map<String, String> oneStopRoute = new HashMap<String, String>();
                        Collection<Direction> directions = MockDB.getInstance().getRouteDirections(agency,route).values();
                        for(Direction direction : directions) {
                            Collection<String> stopIds = direction.stops.values();
                            if(stopIds.contains(fromStop.stopId) && stopIds.contains(toStop.stopId)) {
                                oneStopRoute.put(fromStop.stopTag, toStop.stopTag);
                            }
                        }
                        if(!oneStopRoute.isEmpty())
                            routeStopPairs.put(route, oneStopRoute);
                    }
                }
        }
        return routeStopPairs;
    }
}
