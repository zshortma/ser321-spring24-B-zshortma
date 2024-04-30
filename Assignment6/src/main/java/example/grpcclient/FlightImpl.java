package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;
import com.google.protobuf.ProtocolStringList;


class Flight extends FlightGrpc.FlightImplBase {
    
 // Store the flight details in a list
    private final List<FlightDetails> flightDetailsList = new ArrayList<>();

    //Initialize flight details
    public Flight() {
        initializeFlightDetails();
    }

 
    private void initializeFlightDetails() {
        flightDetailsList.add(FlightDetails.newBuilder()
                .setFlightNumber("ABC123")
                .setOrigin("MCI")
                .setDestination("MCO")
                .setDepartureTime("2024-05-01 08:00")
                .setArrivalTime("2024-05-01 10:00")
                .setDurationMinutes(120)
                .build());
        
        flightDetailsList.add(FlightDetails.newBuilder()
                .setFlightNumber("ABC222")
                .setOrigin("MCI")
                .setDestination("COL")
                .setDepartureTime("2024-05-01 08:00")
                .setArrivalTime("2024-05-01 10:00")
                .setDurationMinutes(120)
                .build());

        flightDetailsList.add(FlightDetails.newBuilder()
                .setFlightNumber("XYZ456")
                .setOrigin("DCF")
                .setDestination("MCI")
                .setDepartureTime("2024-05-01 10:00")
                .setArrivalTime("2024-05-01 12:00")
                .setDurationMinutes(120)
                .build());

        flightDetailsList.add(FlightDetails.newBuilder()
                .setFlightNumber("DEF789")
                .setOrigin("WITA")
                .setDestination("MCI")
                .setDepartureTime("2024-05-01 12:00")
                .setArrivalTime("2024-05-01 14:00")
                .setDurationMinutes(120)
                .build());
    }
    
    @Override
    public void trackFlight(TrackFlightRequest request, StreamObserver<FlightDetails> responseObserver) {
        // Extract flight number from the request
        String requestedFlightNumber = request.getFlightNumber();

        // Find the requested flight details from the stored list
        FlightDetails requestedFlightDetails = null;
        for (FlightDetails flightDetails : flightDetailsList) {
            if (flightDetails.getFlightNumber().equals(requestedFlightNumber)) {
                requestedFlightDetails = flightDetails;
                break;
            }
        }

        // If the requested flight details are found, send them!!
        if (requestedFlightDetails != null) {
            responseObserver.onNext(requestedFlightDetails);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new IllegalArgumentException("Flight details not found for flight number: " + requestedFlightNumber));
        }
    }

    
    @Override
    public void searchFlights(SearchFlightsRequest request, StreamObserver<FlightSearchResult> responseObserver) {
        // Get origin airport from the request
        String origin = request.getOrigin();

        // Go through flight details list based on the origin airport
        List<FlightDetails> matchingFlights = new ArrayList<>();
        for (FlightDetails flightDetails : flightDetailsList) {
            if (flightDetails.getOrigin().equals(origin)) {
                matchingFlights.add(flightDetails);
            }
        }

        // Create FlightSearchResult message with needed details
        FlightSearchResult flightSearchResult = FlightSearchResult.newBuilder()
                .addAllFlights(matchingFlights)
                .build();

        // Send the flight search results to the client
        responseObserver.onNext(flightSearchResult);
        responseObserver.onCompleted();
    }
}