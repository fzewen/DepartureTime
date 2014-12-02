import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.Object;
import java.util.*;

/**
 * Class used to parse the response get form the server using different API calls
 * Created by zewenfei on 11/28/14.
 */
public class XMLParser {

    public static final String DIRECTION = "direction";
    public static final String AGENCY = "agency";
    public static final String ROUTE = "route";
    public static final String ROUTE_TAG = "routeTag";
    public static final String TITLE = "title";
    public static final String TAG = "tag";
    public static final String DIR_TAG = "dirTag";
    public static final String PREDICTION = "prediction";
    public static final String PREDICTIONS = "predictions";
    public static final String IS_DEPARTURE = "isDeparture";
    public static final String FALSE = "false";
    public static final String MINUTES = "minutes";
    public static final String STOP = "stop";
    public static final String STOP_ID = "stopId";
    public static final String LAT = "lat";
    public static final String LON = "lon";

    /**
     * Method to pre set up the environment for the DOM parser
     * @param url the url for the API call
     * @return the Document used to transverse this XML tree
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document setUpDOM(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(url);
        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * Method used to extract the tag and title info from the attributes
     * @param list a list of element node from where to retrieve the info
     * @return a map from the tag to the title
     */
    public static Map<String, String> parseTagTile(NodeList list) {
        Map<String, String> map = new HashMap<String, String>();
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String title = eElement.getAttribute(TITLE);
                String tag = eElement.getAttribute(TAG);
                map.put(tag, title);
            }
        }
        return map;
    }

    /**
     * Method used to extract the agency list from the response of the API call
     * @param url url of the API call using command: agencyList
     * @return a map from agency tag to agency title
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Map<String, String> parseAgency(String url) throws IOException, SAXException, ParserConfigurationException {
        Document doc = setUpDOM(url);
        NodeList agencyList = doc.getElementsByTagName(AGENCY);
        Map<String, String> agencyMap = parseTagTile(agencyList);
        return agencyMap;
    }

    /**
     * Method used to extract the route list from the response of the API call
     * @param url url of the API call using command: routeList
     * @return a map from agency tag to agency title
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Map<String, String> parseRouteList(String url) throws IOException, SAXException, ParserConfigurationException {
        Document doc = setUpDOM(url);
        NodeList routeList = doc.getElementsByTagName(ROUTE);
        Map<String, String> routeMap = parseTagTile(routeList);
        return routeMap;
    }

    /**
     * Method used to extract the route config info from the response of the API call
     * We will extract a stop list for this route and also a direction list for this route from one parse
     * @param url url of the API call using command: routeConfig
     * @return a stop list and a direction list wrapped in a Object array
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Object[] parseRouteInfo(String url) throws IOException, SAXException, ParserConfigurationException {
        Document doc = setUpDOM(url);
        Object[] routeInfo = new Object[2];
        NodeList route = doc.getElementsByTagName(ROUTE);
        Element rElement = (Element)route.item(0);
        String routeTag = rElement.getAttribute(TAG);
        NodeList stopList = rElement.getElementsByTagName(STOP);
        Map<String, Stop> routeStopList = new HashMap<String, Stop>();
//        // we will always use stop Id for simplicity. The direction element will however return stop tags. So will
//        // need this map to do the conversion
        Map<String, String> stopTagToStopIdMap = new HashMap<String, String>();
        Map<String, String> stopTagToStopTitleMap = new HashMap<String, String>();
        for (int index = 0; index < stopList.getLength(); index++) {
            Node sNode = stopList.item(index);
            if (sNode.getNodeType() == Node.ELEMENT_NODE) {
                Element sElement = (Element) sNode;
                String stopTag = sElement.getAttribute(TAG);
                String title = sElement.getAttribute(TITLE);
                if(title.isEmpty())
                    continue;
                String stopId = sElement.getAttribute(STOP_ID);
                double lat = Double.parseDouble(sElement.getAttribute(LAT));
                double lon = Double.parseDouble(sElement.getAttribute(LON));
                Stop stop = new Stop(routeTag, stopTag, stopId, title, lat, lon);
                routeStopList.put(stopTag, stop);
                stopTagToStopTitleMap.put(stopTag, title);
                stopTagToStopIdMap.put(stopTag, stopId);
            }
        }
        NodeList directionList = doc.getElementsByTagName(DIRECTION);
        Map<String, Direction> routeDirectionList = new HashMap<String, Direction>();
        for (int index = 0; index < directionList.getLength(); index++) {
            Node dNode = directionList.item(index);
            if (dNode.getNodeType() == Node.ELEMENT_NODE) {
                Element dElement = (Element) dNode;
                String dTag = dElement.getAttribute(TAG);
                String dTitle = dElement.getAttribute(TITLE);
                NodeList dStops = dElement.getElementsByTagName(STOP);
                Map<String, String> dStopSet = new HashMap<String, String>();
                Map<String, String> dStopTitles = new HashMap<String, String>();
                for (int i = 0; i < dStops.getLength(); i++) {
                    Node dsNode = dStops.item(i);
                    if (dsNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element sElement = (Element) dsNode;
                        String dsTag = sElement.getAttribute(TAG);
                        String dsTitle = stopTagToStopTitleMap.get(dsTag);
                        String dstopId = stopTagToStopIdMap.get(dsTag);
                        dStopSet.put(dsTag, dstopId);
                        dStopTitles.put(dsTag, dsTitle);
                    }
                }
                Direction direction = new Direction(routeTag, dTitle, dStopSet, dStopTitles);
                routeDirectionList.put(dTag, direction);
            }
        }
        routeInfo[0] = routeStopList;
        routeInfo[1] = routeDirectionList;
        return routeInfo;
    }

    /**
     * Method used to extract the next bus prediction from the response of the API call
     * @param url url of the API call using command: prediction
     * @return a map from the direction title to the Collection of next bus departure time for this route
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Map<String, Collection<String>> parsePrediction(String url) throws ParserConfigurationException, IOException, SAXException {
        Document doc = setUpDOM(url);
        //we may have skip predictions element in a wrong way, please check how DOM works
        NodeList directionList = doc.getElementsByTagName(DIRECTION);
        Map<String, Collection<String>> predictionMap = new HashMap<String, Collection<String>>();
        for (int index = 0; index < directionList.getLength(); index++) {
            Node dNode = directionList.item(index);
            if (dNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) dNode;
                NodeList predictionList = eElement.getElementsByTagName(PREDICTION);
                for(int i = 0; i < predictionList.getLength(); i++) {
                    Node pNode = predictionList.item(i);
                    if (pNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element pElement = (Element) pNode;
                        if (pElement.getAttribute(IS_DEPARTURE).equals(FALSE)) {
                            String epochTime = pElement.getAttribute(MINUTES);
                            String dirTag = pElement.getAttribute(DIR_TAG);
                            Collection list = null;
                            if (predictionMap.containsKey(dirTag)) {
                                list = predictionMap.get(dirTag);
                            }  else {
                                list = new ArrayList<String>();
                            }
                            list.add(epochTime);
                            predictionMap.put(dirTag, list);
                        }
                    }
                }
            }
        }
        return predictionMap;
    }

    /**
     * Method used to extract the routes for a stop from the response of the API call
     * @param url url of the API call using command: prediction
     * @return a
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static Collection<String> parseStopRoutes(String url) throws IOException, SAXException, ParserConfigurationException {
        Document doc = setUpDOM(url);
        NodeList routeList = doc.getElementsByTagName(PREDICTIONS);
        List<String> stopRoutes = new ArrayList<String>();
        for (int index = 0; index < routeList.getLength(); index++) {
            Node rNode = routeList.item(index);
            if (rNode.getNodeType() == Node.ELEMENT_NODE) {
                Element rElement = (Element) rNode;
                String routeTag = rElement.getAttribute(ROUTE_TAG);
                stopRoutes.add(routeTag);
            }
        }
        return stopRoutes;
    }
}
