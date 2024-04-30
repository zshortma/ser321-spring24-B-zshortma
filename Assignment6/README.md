Screen Cast : https://drive.google.com/file/d/1Dz47HQx-oTR3N6X-c4jET_KINmO_wgHq/view?usp=sharing

Running Test (forgot to show in the first one) : https://drive.google.com/file/d/14X09I5ozwGCjiQZzQGlLzGMc2KtV7BUa/view?usp=sharing

Desciption of project and requirments :

This project was to practice working with gRPC, distributed systems and services. We were to implement a few different services such as a Q and A service and following service and my personal implementation of a Flight service. The client should be able to make request to a server where a reponse is built and retured to the client. In addition we were to implement a test class to test out our newly implmented functionality. 

The first task requirments were to create a structure what includes a README to track and explain our project. I implemented that here and added the screencast as usual. The other part of task one was to allow the commands gradle runNode and runClient to be run with deafult values, this is working. The two services I chose to implement was QandA as well as follow. I added the required easy to understand terminal output to help guide the user with a menu with the offered services. Apart of the fourth requriment for task one I opted for the unit testing method, you can see in the second screencast this is working and output to the report file. In addition, I added exception handling to prevent the server and client from crashing. 

For the second task I implemented my own service called Flight. I allowed two services of flight tracker and flight search request each requiring at least one input from the user. One request return a list of applicable flights with the number of flights and the second returns all flight details for the flight number entered by the user. I didn't exactly know what was meant by returns a duplicated feild but multiple flights could be retruned to the user. Overall, I implemented four of requirments, more detail in the protocol design below. 

For the third task I implemented the new class Clienty2.java, created a new gradle command and showed the interactions in the screencast. 

How to run the program :
The default gradle commands given in the desciption plus the one I used for test running : 
gradle runNode, gradle runClient, gradle runServer, gradle runClient2, gradle runRegistryServer, ./gradlew test --tests ServerTest, gradle runNode -PregOn=true .

How To work Program :
I tried to make it as easy to understand but you can see more details in the screencast. After you exit each service you will need to restart the client!

Select a number to indicat the service you want to use.
You will then be promted with more options service based, enter a number of what you would like to do.

For the flight service there is an output of options that you can copy/paste when being asked. Flight number 'ABC123' and flight origin 'MCI'. 

For Q and A again just follow the pormpts, you will add a question and be retruned an integer, when you go to add an answer you will first need to add the interger referring to the question and then enter your answer. View question does not require anything specail. 

For follow you will need to add two users, then specify with the service follow which user you want to follow who. In view Followers you need to specify who follwer list you want to see. 

Task Two :

Protocol :

message TrackFlightRequest { string flight_number = 1; }

message SearchFlightsRequest {
  string origin = 1;
  string destination = 2;
}

message FlightDetails {
  string flight_number = 1;
  string origin = 2;
  string destination = 3;
  string departure_time = 4;
  string arrival_time = 5;
  int32 duration_minutes = 6;
}

message FlightSearchResult { repeated FlightDetails flights = 1; }

service Flight {
  rpc trackFlight (TrackFlightRequest) returns (FlightDetails) {}
  rpc searchFlights (SearchFlightsRequest) returns (FlightSearchResult) {}
}


FlightDetails:
This message type represents the details of a flight.
It contains the following fields:
flightNumber: A string representing the flight number.
origin: A string representing the origin airport code.
destination: A string representing the destination airport code.
departureTime: A string representing the departure time of the flight.
arrivalTime: A string representing the arrival time of the flight.
durationMinutes: An integer representing the duration of the flight in minutes.
TrackFlightRequest:
This message type is used to request information about a specific flight.
It contains a single field:
flightNumber: A string representing the flight number of the flight to be tracked.
SearchFlightsRequest:
This message type is used to request a search for flights based on the origin airport.
It contains a single field:
origin: A string representing the origin airport code from which to search for flights.
FlightSearchResult:
This message type represents the result of a flight search operation.
It contains a repeated field:
flights: A list of FlightDetails objects representing the flights matching the search wants.

