Video Link : https://drive.google.com/file/d/1RZ-YE5jAcbWtMXqWYr5C2kMyhLl3h8Es/view?usp=sharing

Questions :

A. This project is to countinue our work on understanding sockets, client/server communication and understanding TCP connections.
We are to make a game, where users can make request and the server will fulfill them with repsonses. The user should be able to 
make guess on where an image is located/based out of and the server will keep track of the poaints and determine if the user is a
winner or not all within a time frame specified by the user. My project will establish a server and a client, allow them to connect using sockets to help facilitate communication
through JSON based object values and keys. Based upon those repsonses my program will determine how to handle the reponses such as 
disabling further interactions when teh client is done and displaying images when reponses indicate to do so. In addition I developed
a main menu for the client to select whether to quit, play game or view the leaderboard. 

B. 
Use a GUI ( x perfect implemenation or Attempted and might not be perfect [0] - tried but ran out of time [] blank - no attempt)
There are things that are not perfect -but I tried my best to at least attempt and try everything.

[x] Images are sent from the server to the client through the TCP connection. The client does not have access to the image directories, so the image needs to be sent directly (not just the path).

[x] When the client starts up, it should connect to the server. The server will reply by asking for the name of the player.

[x] The client should send its name, and the server should receive it and greet the client by name.

[x] The client should be presented with a choice between seeing a leaderboard, playing the game, or quitting the game. Make the interface easy so a user will know what to do.

[x] Evaluations of the input need to happen on the server side; the client will not know the images, locations, their corresponding answers, the points, or the leaderboard. The client will always ask the server for these and will get a message with an answer. No real points for this since if this is not done then you do not really use the client/server correctly. So this will lead to deductions on the parts where these things are used.

[x] The leaderboard will show all players that have played since the server started with their name and points. The server will maintain the leaderboard and send it to the client when requested. You can assume that the same name is the same player. Add on: 3 more points if the leaderboard is persistent even when the server is restarted (we did not cover this in class yet though).

[x] If the client chooses to start the game, the server will ask how long a game should last in seconds. The user enters a valid number, and a timer will start on the server side, and a location will be sent to the client and displayed in the UI. If you do not know how to do a timer, just ask for a time and then start the game without a timer (there will be points for the timer later). You can then just ask 4 locations to get things going. The server will send over an image of the first location. Always print the answer on the server side so we can guess easily while grading.

[x] The client can then make a guess, type "next", "left", or "right".

[x] Guess: The client enters a guess, and the server must check the guess and respond accordingly. If the answer is correct, then they will get a new location. If the answer is incorrect, they will be informed that the answer was incorrect and can try again.

[0] Next: Users can always type "next", which will make the server send a new location. If there are no more locations available, you can show one of the old ones. It is ok to show the same location again even if there are more to choose from, we are not too concerned with that.

[0] Left/Right: The Client can type "left" or "right", and this will lead the server to send over a new image from the same location but as if the viewer has turned 90 degrees. Three are a couple of images given. For the numbering: 1 is always the first image, 2 is turning to the right, while 4 will be left from number 1. So we always have 4 images per location. Feel free to add more locations to make it more interesting.

[x] As soon as the game starts a timer starts, the game lasts as long as was chosen by the person playing the game. The player tries to guess as many locations as possible in this time. If the timer ran out when the server receives an answer then this guess does not count anymore. For the timer you can check out the "Timer" class in Java or very simple just do System.currentTimeMillis() and calculate the start time vs current time to get the "timer". For the timer do not think too complicated here, you do not have to quit the game as soon as the timer ran out, just check on the server side whenever you receive a client message if the timer has not run out. If the timer ran out, then send a quit message with current points and what else might be needed to the client. If the timer is still running continue the game as explained.

[x] We also want a leaderboard. The leaderboard should display how many points a player received. Since users can choose their game time, we will just use correct guesses/chosen time*100. E.g. if I got 4 locations correct in 60 seconds, my score would be 6.66. I know this is kinda arbitrary, you can come up with something else you like if you want, please explain in your README. The result should be saved on the leaderboard with the player's name. If there was already a score and it is lower, than it should be replaced. You can assume that the same name means the same player.

[x] At the end of a game, display the score of the player in the client UI, if you like you can also display if they have a new high score (the high score part is optional).

[x] Your protocol must be robust. If a command that is not understood is sent to the server or arguments are sent that the server does not know, then the protocol should define how these are indicated to the client (imagine someone else wants to implement a client for your server). Your protocol must have headers and optionally payloads. This means in the context of one logical message the receiver of the message has to be able to read a header, understand the metadata it provides, and use it to assist with processing a payload (if one is even present). This protocol needs to be described in detail in the README.md.

[x] Your programs must be robust. If errors occur on either the client or server or if there is a network problem, you have to consider how these should be handled in the most recoverable and informative way possible. Implement good general error handling and output. Your client/server should not crash even when invalid inputs are provided by the user. If we can crash things in normal play, we will remove points. We will not be crazy mean but we will test a couple of things.

[x] After the timer ran out the points the player received should be displayed and the game goes back to the main menu.



C. To start program you can use the simple (as descibed in document) : gradle runServer and gradle runClient. 

D. Below is my responses and Request (simplified a bit) :

[Connect Request]
[Description]: The client initiate a connection request to the server when starting.
[Possible Responses]: Success, Error

[Name Request]
[Description]: After connecting to the server, the client is prompted to provide its name.
[Possible Responses]: Success, Error

[Menu Request]
[Description]: The client is given a menu of options.
[Possible Responses]:Success, Error

[Game Start Request]
[Description]: New the game session.
[Possible Responses]: Success,Error

[Guess Request]
[Description]: Client submits a guess for the location.
[Possible Responses]: Correct Guess, Incorrect Guess, Error

[Leaderboard Request]
[Description]: Client requests to view the leaderboard.
[PossibleResponses]: Success, Error

[Quit Request]
[Description]: Client requests to quit game.
[PossibleResponses]: Success


[Success Response]
[Description]: Tells whether there was a successful completion of a request .
[Possible Triggers]: Various successful operations.
[Possible Causes]: Correct input, successful execution server logic.
[Actions]: Proceed to the next step or continue with the current operation.

[Error Response]
[Description]: There was an error  during processing of the request.
[Possible Triggers]: Invalid input, server-side errors,.
[Possible Causes]: Incorrect input, unplanned senireos. 
[Actions]: Prompt the user to reconnect or guide on input format.

[Leaderboard Response]
[Description]: Contains the requested leaderboard data.
[Possible Triggers]: Client requests to view the leaderboard.
[Possible Causes]: Successful view of leaderboard  from the serverr.
[Actions]: Display the leaderboard data to the client for viewing.

[GameOver Response]
[Description]: Indicates the end of the game session.
[Possible Triggers]: Timer is over or user did (-q) initiated quit.
[Possible Causes]: Timer reaching its limit or user deciding to end the game.
[Actions]: Display the final score to the client, prompt for further actions.

E. To help ensure a robust program I implement : error handling on the readings/writings of ints and strings, I checked that files exsited before attempting to open them, and implement try and catch blocks for JSON object creations/modifications as well as other general code block that needed extra error handling to ensure the server was to not crash. I also, attempted to create cases for certain behaviors such as quit game that would help protect my program from unexpected and unplanned error handling this protection was disabling the input text box and button when the user quit the program. This helps protect my program from unexpected interactions with the users. 

F. With TCP we are given realiable and garenteed packet deleiveries, however, with UDP that is not the case. I would have to implement reliablility mechanisms to help ensure all messages are being deleivered and in order. UDP also would require more error handling development as things such as checksums and again..message delivery would need to be handled. Overall, code would need to be in general refactored and much more error handling and checking will need to be done. 
