package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import service.*;
import com.google.protobuf.Empty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.protobuf.Empty; // needed to use Empty
import com.google.protobuf.ProtocolStringList;



/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class Client2 {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  private final RegistryGrpc.RegistryBlockingStub blockingStub4;
  private final QandaGrpc.QandaBlockingStub blockingStub5;
  private final FollowGrpc.FollowBlockingStub blockingStub6;
  private final FlightGrpc.FlightBlockingStub blockingStub7;
  
  private Map<String, List<String>> followedUsers = new HashMap<>();

  /** Construct client for accessing server using the existing channel. */
  public Client2(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    blockingStub4 = RegistryGrpc.newBlockingStub(channel);
    blockingStub5 = QandaGrpc.newBlockingStub(channel);
    blockingStub6 = FollowGrpc.newBlockingStub(channel);
    blockingStub7 = FlightGrpc.newBlockingStub(channel);
  }

  /** Construct client for accessing server using the existing channel. */
  public Client2(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = null;
    blockingStub4 = null;
    blockingStub5 = QandaGrpc.newBlockingStub(channel);
    blockingStub6 = FollowGrpc.newBlockingStub(channel);
    blockingStub7 = FlightGrpc.newBlockingStub(channel);
  }

  public void askServerToParrot(String message) {

    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;


    Empty empt = Empty.newBuilder().build();

    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2.setJoke(request);
      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getNodeServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub4.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }
  
  public void addQuestion(String questionText) {
      QuestionReq request = QuestionReq.newBuilder()
          .setQuestion(Question.newBuilder().setQuestion(questionText).build())
          .build();

      try {
          QuestionRes response = blockingStub5.addQuestion(request);
          System.out.println("Question added successfully: " + response.getMessage());
      } catch (Exception e) {
   
          System.err.println("Error adding question: " + e.getMessage());
      }
  }
  
  public void addAnswer(String answerText, int questionId) {

      AnswerReq request = AnswerReq.newBuilder()
          .setId(questionId)
          .setAnswer(answerText)
          .build();

      try {
          AnswerRes response = blockingStub5.addAnswer(request);
          System.out.println("Answer added successfully to question ID: " + questionId);
      } catch (Exception e) {
   
          System.err.println("Error adding answer: " + e.getMessage());
      }
  }
  
  
  public void viewQuestions() {

      Empty request = Empty.getDefaultInstance();

      try {
 
          AllQuestionsRes response = blockingStub5.viewQuestions(request);
          List<Question> questionsList = response.getQuestionsList();

          for (Question question : questionsList) {
              System.out.println("Question ID: " + question.getId());
              System.out.println("Question Text: " + question.getQuestion());
              System.out.println("Answers:");
              for (String answer : question.getAnswersList()) {
                  System.out.println("- " + answer);
              }
              System.out.println();
          }
      } catch (Exception e) {
 
          System.err.println("Error viewing questions: " + e.getMessage());
      }

  }

  
  
  public void addUser(String userName) {
      UserReq request = UserReq.newBuilder().setName(userName).build();
      UserRes response;

      try {
          response = blockingStub6.addUser(request);
          followedUsers.put(userName, new ArrayList<>());
          System.out.println(response.getIsSuccess() ? "User added successfully." : "Failed to add user: " + response.getError());
      } catch (Exception e) {
          System.err.println("RPC failed: " + e);
      }
  }

  public void follow(String userName, String followUserName) {
      UserReq request = UserReq.newBuilder().setName(userName).setFollowName(followUserName).build();
      followedUsers.computeIfAbsent(userName, k -> new ArrayList<>()).add(followUserName);
      UserRes response;

      try {
          response = blockingStub6.follow(request);
          System.out.println(response.getIsSuccess() ? "User followed successfully." : "Failed to follow user: " + response.getError());
      } catch (Exception e) {
          System.err.println("RPC failed: " + e);
      }
  }
  
  private void trackFlight(BufferedReader reader) throws IOException {
      
      System.out.println("Enter the flight number to track:");
      String flightNumber = reader.readLine();
      
              TrackFlightRequest trackRequest = TrackFlightRequest.newBuilder()
              .setFlightNumber(flightNumber)
              .build();
      
      try {
          FlightDetails flightDetails = blockingStub7.trackFlight(trackRequest);
          System.out.println("This flight's tracking details  : ");
          System.out.println(" ");
          System.out.println("Flight Number: " + flightDetails.getFlightNumber());
          System.out.println("Origin: " + flightDetails.getOrigin());
          System.out.println("Destination: " + flightDetails.getDestination());
          System.out.println("Departure Time: " + flightDetails.getDepartureTime());
          System.out.println("Arrival Time: " + flightDetails.getArrivalTime());
          System.out.println("Duration (minutes): " + flightDetails.getDurationMinutes());
      } catch (Exception e) {
          System.err.println("This flight does not exist.");
          System.err.println("Error tracking flight: " + e.getMessage());
         
      }
  }
  
  public void searchFlights(BufferedReader reader) throws IOException {
    
      System.out.println("Origins we cater too : MCI / DCF / WITA");
      System.out.println("Enter the departing origin to search for flights:");
      String departingOrigin = reader.readLine();

    
      SearchFlightsRequest searchRequest = SearchFlightsRequest.newBuilder()
              .setOrigin(departingOrigin)
              .build();

      try {
        
          FlightSearchResult searchResponse = blockingStub7.searchFlights(searchRequest);
          List<FlightDetails> matchingFlights = searchResponse.getFlightsList();

          int numOfFlights = matchingFlights.size();
          System.out.println("Number of flights matching departing origin '" + departingOrigin + "': " + numOfFlights);
          System.out.println(" ");

          for (FlightDetails flight : matchingFlights) {
              System.out.println("Flight Number: " + flight.getFlightNumber());
              System.out.println("Origin: " + flight.getOrigin());
              System.out.println("Destination: " + flight.getDestination());
              System.out.println(" ");
          }
      } catch (Exception e) {
  
          System.err.println("Error searching for flights: " + e.getMessage());
      }
  }
  
  public void viewFollowing(String userName) {
      List<String> followed = followedUsers.getOrDefault(userName, new ArrayList<>());
      UserReq request = UserReq.newBuilder().setName(userName).build();
      UserRes response;

      try {
          response = blockingStub6.viewFollowing(request);
          if (response.getIsSuccess()) {
              System.out.println("Followed users for " + userName + ": " + response.toString());
          } else {
              System.out.println("Failed to view followed users: " + response.getError());
          }
      } catch (Exception e) {
          System.err.println("RPC failed: " + e);
      }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 6) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    
  

    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    // Create a communication channel to the server (Node), known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {

      // ##############################################################################
      // ## Assume we know the port here from the service node it is basically set through Gradle
      // here.
      // In your version you should first contact the registry to check which services
      // are available and what the port
      // etc is.

      /**
       * Your client should start off with 
       * 1. contacting the Registry to check for the available services
       * 2. List the services in the terminal and the client can
       *    choose one (preferably through numbering) 
       * 3. Based on what the client chooses
       *    the terminal should ask for input, eg. a new sentence, a sorting array or
       *    whatever the request needs 
       * 4. The request should be sent to one of the
       *    available services (client should call the registry again and ask for a
       *    Server providing the chosen service) should send the request to this service and
       *    return the response in a good way to the client
       * 
       * You should make sure your client does not crash in case the service node
       * crashes or went offline.
       */

      // Just doing some hard coded calls to the service node without using the
      // registry
      // create client
      Client2 client = new Client2(channel, regChannel);

      // call the parrot service on the server
      client.askServerToParrot(message);

      // ask the user for input how many jokes the user wants
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String num = "";

      while (true) {
          System.out.println("Please indicate which service you would like");
          System.out.println("1-Joke 2-Echo 3-Qanda 4-Follow 5-Flight");

          try {
              num = reader.readLine();
              int choice = Integer.parseInt(num);

              if (choice < 1 || choice > 5) {
                  throw new NumberFormatException();
              } else {
                  
                  System.out.println("You have chosen service " + num);

                  switch (choice) {
                  case 1:
                      System.out.println("You chose service Joke. How many jokes do you want to see?");

                      num = reader.readLine();
                      int jokeNum = Integer.parseInt(num);
                      
                      client.askForJokes(Integer.valueOf(jokeNum));
                      client.setJoke("I made a pencil with two erasers. It was pointless.");
                      client.askForJokes(Integer.valueOf(6));
                      break;
                      
                  case 2:
                      System.out.println("You chose service Echo");
                      
                      String messageToEcho = reader.readLine();
                      
                      client.askServerToParrot(messageToEcho);
                      break;
                  case 3:
                      while(true) {
                          System.out.println("You chose service Qanda");
                          System.out.println("1. Add a question");
                          System.out.println("2. View all questions");
                          System.out.println("3. Add an answer to a question");
                          System.out.println("4. Exit Qanda");
                          try {
                              num = reader.readLine();
                              int qandaChoice = Integer.parseInt(num);

                              switch (qandaChoice) {
                              case 1:
                                  System.out.println("Enter your question:");
                                  String questionText = reader.readLine();
                                  client.addQuestion(questionText);
                                  break;

                              case 2:
                                  System.out.println("Viewing all questions...");
                                  client.viewQuestions();
                                  break;

                              case 3:
                                  System.out.println("Enter the ID of the question you want to answer:");
                                  int questionId = Integer.parseInt(reader.readLine());
                                  System.out.println("Enter your answer:");
                                  String answerText = reader.readLine();
                                  client.addAnswer(answerText, questionId);
                                  break;

                              case 4:
                                  System.out.println("Exiting service Qanda");
                                  break;

                              default:
                                  System.out.println("Invalid option.");
                                  break;
                          }
                                            
                          if (qandaChoice == 4) {
                              break;
                          }
                      } catch (NumberFormatException | IOException e) {
                          System.out.println("Invalid input. Please enter a number.");
                      }
                  }
                  break;
                  case 4:
                      
                      while(true) {
                      System.out.println("You chose service Follow");
                      System.out.println("1. Follow a user");
                      System.out.println("2. View followed users");
                      System.out.println("3. Add users");
                      System.out.println("4. Exit");
                      try {
                          num = reader.readLine();
                          int followChoice = Integer.parseInt(num);

                          switch (followChoice) {
                              case 1:
                                  System.out.println("Enter the username you want to follow:");
                                  String followUserName = reader.readLine();
                                  System.out.println("Enter your username:");
                                  String userName = reader.readLine();
                                  client.follow(userName, followUserName);
                                  break;

                              case 2:
                                  System.out.println("Enter name of person's following you want to see:");
                                  String viewPerson = reader.readLine();
                                  client.viewFollowing(viewPerson);
                                  break;
                              case 3:
                                  System.out.println("Enter the username of the new user:");
                                  String newUser = reader.readLine();
                                  client.addUser(newUser);
                              
                                  break;
                                  
                              case 4:
                                  System.out.println("Exiting service Follow");
                                  break;
                              default:
                                  System.out.println("Invalid option.");
                                  break;
                          }
                          
                          if (followChoice == 4) {
                              break;
                          }
                          
                      } catch (NumberFormatException | IOException e) {
                          System.out.println("Invalid input. Please enter a number.");
                      }
                      }
                      
                     break;
                  case 5 : 
                      
                      while (true) {
                          System.out.println("You chose service Flight");
                          System.out.println("1. Track a flight");
                          System.out.println("2. Search for flights");
                          System.out.println("3. Exit Flight service");
                          try {
                              num = reader.readLine();
                              int flightChoice = Integer.parseInt(num);


                     
                              switch (flightChoice) {
                              case 1:
                                  System.out.println("Flight Board");
                                  System.out.println("ABC123");
                                  System.out.println("XYZ456");
                                  System.out.println("DEF789");
                                  client.trackFlight(reader);
                                  break;

                              case 2:
                                  client.searchFlights(reader);
                                  break;


                                  case 3:
                                      System.out.println("Exiting service Flight");
                                      break;

                                  default:
                                      System.out.println("Invalid option.");
                                      break;
                              }


                              if (flightChoice == 3) {
                                  break;
                              }

                          } catch (NumberFormatException | IOException e) {
                              System.out.println("Invalid input. Please enter a number.");
                          }
                      }
              }
                  break; // Exit the loop
              }
          } catch (NumberFormatException | IOException e) {
       
              System.out.println("Invalid input. Please enter a number between 1 and 4.");
          }
      }


      // list all the services that are implemented on the node that this client is connected to

      System.out.println("Services on the connected node. (without registry)");
      client.getNodeServices(); // get all registered services 

      // ############### Contacting the registry just so you see how it can be done

      if (args[5].equals("true")) { 
        // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
        // get thread's services
        client.getServices(); // get all registered services 

        // get parrot
        client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service
        
        // get all setJoke
        client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service

        // get getJoke
        client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service

        client.findServer("services.Qanda/addQuestion");
        client.findServer("services.Flight/trackFlight");
        client.findServer("services.Flight/searchFlights");
        // does not exist
        client.findServer("random"); // shows the output if the server does not find a given service
      }

    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent
      // leaking these
      // resources the channel should be shut down when it will no longer be used. If
      // it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
