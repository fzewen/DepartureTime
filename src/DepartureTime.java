import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

/**
 * Class used to get the departure times give agency, route, direction and stop
 * Created by zewenfei on 11/30/14.
 */
public class DepartureTime {

    private String agency;
    private String route;
    private String direction;
    private String stop;

    public DepartureTime(String agency, String route, String direction, String stop) {
        this.agency = agency;
        this.route = route;
        this.direction = direction;
        this.stop = stop;
    }

    /**
     * Method used to retrieve a collection of departure time
     * @return a collection of departure time
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Collection<String> getDepartureTime() throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        String url = URIGenerator.getPredictionURI(agency, route, stop).toString();
        Map<String, Collection<String>> directionDepartureTime = XMLParser.parsePrediction(url);
        Collection<String> departureTimes = directionDepartureTime.get(direction);
        return departureTimes;
    }
}
