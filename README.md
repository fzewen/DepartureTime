DepartureTime
=============

A real-time departure time app for public transportation also support find nearest route/stop
The solution mainly focus on the backend design and implementation

Technical choices:
API used: http://www.nextbus.com/xmlFeedDocs/NextBusXMLFeed.pdf 
It's more flexible than 511 API, besides I get all the routes for a stop through the predictions command which enable me to build the find nearest route/stop function

Library used:
EsperEval.jar   mainly use the geo function to calculate the distance on map
DOM parser      the reson to choose this xml parser is it's very easy to use and the memory use for this app is not that serious. May switch to Stax parser for future improment
KDTree          net.sf.javaml.core.kdtree.KDTree used to find K nearest neighbor given a location

Database used:
I mock a MockDB singlton class to hold the data for simplicity. we will retrieve the item through API call only when we can't find it in maps.

Architecture(classes):
-MockDB: singleton class serve as db using a group of HashMaps
-URIGenerator: class to generate url baded on command used to send the endpoint to retrieve the xml
-XMLParser: class to parse the xml returned and fill the db
-FindStopRouteManager: singletion class to privide FindStopRoute instance given a specifc agency.
                       We do all the operarions under the context of a specifc agency(get next bus departure time, find                              nearest route), that's coordinating the Nextbus API call
-FindStopRoute: class to build KD Tree for an agency and to find the nearst route
-DepartureTime: class to find the next bus departure time
-Stop:          class to hold info for a stop
-Direciton:     calss to hold info for a direction

I made assumption here(but according to the API call, it should be right) that the StopId under an agency is unique so that I 
can use http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=<agency_tag>&stopId=<stop id> link to retrieve all the routes for a stop. With that info I can simplify my findStopRoute function otherwise I need to build a reverse map from stopTag to route list, which will use a lot of memory.

Testing:
Only a FindTest class to do a intergration test for a special case: find route from Union Square to ATT Part
Should add more uinit tests if goes to production

UX: the focus of this app is not on the front end side, but you can use the MainTest to check whether it works or not
MainTest class serve as a simple interative terminal to type in command.
-QUIT: quit the app
-NEXT: find the departure time for a route given agency, route, direction, stop info
-FIND: find nearest routes and start stop and end stop associate with the route
You need to type in the string in bracket 
eg (in) inbound to caltrain, here "in" is the direction tag and you need to input in order  to select a direction
I didn't do input validation here so you need to follow the instruction from the terminal

Deployment:
You can compile the java file with the jar required and use MainTest.java to try it out

Improment:
Given the limited timing some improvement can be made mainly to the front end side and the find nearest route/stop algorithm, also The schedule/message/vehical location command from the API can be used to make the app more functional.
Another point is to enable findStopRoutes to support different agencies for an area at the same time to get the best route or use multi routes to get to a destination
