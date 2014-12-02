DepartureTime
=============

A real-time departure time app for public transportation also support find nearest route/stop

API used: http://www.nextbus.com/xmlFeedDocs/NextBusXMLFeed.pdf 
It's more flexible than 511 API, besides I get all the routes for a stop through the predictions command which enable me to build the find nearest route/stop function

Library used:
EsperEval.jar   mainly use the geo function to calculate the distance on map
DOM parser      the reson to choose this xml parser is it's very easy to use and the memory use for this app is not that serious
KDTree          net.sf.javaml.core.kdtree.KDTree used to find K nearest neighbor given a location

MainTest class serve as a simple interative terminal to type in command.
QUIT: quit the app
NEXT: find the departure time for a route given agency, route, direction, stop info
FIND: find nearest routes and start stop and end stop associate with the route
You need to type in the string in bracket 
eg (in) inbound to caltrain, here "in" is the direction tag and you need to input in order  to select a direction
I didn't do input validation here so you need to follow the instruction from the terminal

Given the limited timing some improvement can be made mainly to the front end side and the find nearest route/stop algorithm, also The schedule/message/vehical location command from the API can be used to make the app more functional.
