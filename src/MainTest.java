import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

/**
 * Class to serve as a terminal to the app
 * Support following Command:
 * QUIT quit the app
 * NEXT find the next arrival time for a route line
 * FIND find the route and its start stop and end stop from location a to b
 * Created by zewenfei on 11/27/14.
 */
public class MainTest {
    public static final String QUIT= "QUIT";
    public static final String NEXT= "NEXT";
    public static final String FIND= "FIND";
    public static void main(String args[]) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        //  prompt the user to enter command
        String command = null;
        while(command == null || !command.equals(QUIT)) {
            System.out.print("Enter a command: ");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
                command = br.readLine();
            } catch (IOException ioe) {
                System.out.println("IO error trying to read your name!");
                System.exit(1);
            }
            if (command.equals(NEXT)) {
                Map<String, String> agencyList = MockDB.getInstance().getAgencyList();
                System.out.println("Here's the list of Agency");
                System.out.println("----------------------------------------------");
                for (String agency : agencyList.keySet()) {
                    outputInfo(agency, agencyList.get(agency));
                }
                System.out.println("----------------------------------------------");
                System.out.println("Please choose one agency");
                String agencyTag = br.readLine();
                System.out.println("Here's the list of Routes for agency: " + agencyTag);
                System.out.println("----------------------------------------------");
                Map<String, String> routeList = MockDB.getInstance().getRouteList(agencyTag);
                for (String route : routeList.keySet()) {
                    outputInfo(route, routeList.get(route));
                }
                System.out.println("----------------------------------------------");
                System.out.println("Please choose one route");
                String routeTag = br.readLine();
                System.out.println("Here's the list of Directions for route: " + routeTag);
                System.out.println("----------------------------------------------");
                Map<String, Direction> directions = MockDB.getInstance().getRouteDirections(agencyTag, routeTag);
                for (String direction : directions.keySet()) {
                    outputInfo(direction, directions.get(direction).title);
                }
                System.out.println("----------------------------------------------");
                System.out.println("Please choose one direction");
                String directionTag = br.readLine();
                System.out.println("Here's the list of Stops for direction: " + directionTag);
                Map<String, String> stops = directions.get(directionTag).stopTitles;
                for (String stop : stops.keySet()) {
                    outputInfo(stop, stops.get(stop));
                }
                System.out.println("Please choose one stop");
                String stopTag = br.readLine();
                DepartureTime departureTimet = new DepartureTime(agencyTag, routeTag, directionTag, stopTag);
                Collection<String> dtimes = departureTimet.getDepartureTime();
                System.out.println("The departure time for\nagency " + agencyList.get(agencyTag) + "\nroute " + routeList.get(routeTag) + "\ndirection " + directions.get(directionTag) + "\nstop " +stops.get(stopTag) + " are :");
                if(dtimes == null || dtimes.size() == 0) {
                    System.out.println("There is no route for this stop at the moment, please check the schedule");
                } else {
                    for (String dtime : dtimes) {
                        System.out.print(dtime + "|");
                    }
                    System.out.print(" minutes");
                }
                System.out.println();
            } else if (command.equals(FIND)) {
                System.out.println("Please enter your current geo-location");
                System.out.println("Your latitude: ");
                double slat = Double.parseDouble(br.readLine());
                System.out.println("Your longitude: ");
                double slon = Double.parseDouble(br.readLine());
                System.out.println("Here is your location: " + slat + "," + slon);
                System.out.println("Please enter your destination's geo-location");
                System.out.println("Destination latitude: ");
                double dlat = Double.parseDouble(br.readLine());
                System.out.println("Destination: ");
                double dlon = Double.parseDouble(br.readLine());
                System.out.println("Here is your destination location: "+ dlat + "," +dlon );
                System.out.println("Please choose an agency that you would like for this travel");
                Map<String, String> agencyList = MockDB.getInstance().getAgencyList();
                System.out.println("Here's the list of Agency");
                System.out.println("----------------------------------------------");
                for (String agency : agencyList.keySet()) {
                    outputInfo(agency, agencyList.get(agency));
                }
                System.out.println("----------------------------------------------");
                System.out.println("Please choose one agency");
                String agencyTag = br.readLine();
                FindStopRoute service = FindStopRouteManager.getInstance(agencyTag);
                Map<String, Map<String, String>> routeStopPairs = service.getAllStopRoutePair(slat, slon, dlat, dlon);
                System.out.println("----------------------------------------------");
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
        }
    }
    public static void outputInfo(String key, String value) {
        System.out.println("(" + key + ")" + value);
    }
}
