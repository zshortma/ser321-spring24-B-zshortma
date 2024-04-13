Screencast : https://drive.google.com/file/d/1907KNs4GN42sJ9-__G3ix1qklKAEEzGV/view?usp=sharing

Project Description / Detailed Desciption :

This program allows clients to connect and play a word guessing game implementing client/server communication and threading principles. The user can enter the gaming system logging their timestamp to a log file, tell the program what they want to do whether it be looking at the leaderboard, starting the game or exiting. This program uses protobuf as a protocol of communication between client and server. 

How to run each :

To run the client : gradle runClient To run the first basic client/server : gradle runServer

What the program excepts :

clean and build using 7.4.2 gradle and java 18. Clients should connect to server then should then specify their name. The client should follow the prompts given by console. 

Part e of the requirment : designed output messages for client to help guide user and make it easy to read and follow. I added simple instructions to the console to help make this easier. 

Requirements completed :

The game/leaderboard isn't fully implemented. But I attempted (see code)

please note : Multiple clients can connect but they must dissconnect to allow the other to get a response. 
please note : I did the task for adding to AWS tmux session, however I noticed siginifacant lag with repsonses. 

[x] The project needs to run through Gradle (nothing really to do here, just keep the Gradle file as is)
[x] (6 points) You need to implement the given protocol (Protobuf) see the Protobuf files, the PROTOCOL.md and the Protobuf example in the examples repo (this is NOT an optional requirement). If you do not do this you will lose 15 points (instead of getting the 6 points) since then our client will not run with your server for testing and thus basically your interface is wrong and not what the customer asked for.
[x] (4 points) The main menu gives the user 3 options: 1: leaderboard, 2 play game, 3 quit. After a game is done the menu should pop up again. Implement the menu on the Client side (not optional) - the server never sends the menu.
[x] (3 points) Server asks for a name when client connects and the user can enter a name on the Client terminal and this will be send to the server. Server will greet the Client.
[x] (3 points) When the user chooses option 1, a leader board will be shown (does not have to be sorted or pretty).
[] (5 points) The leader board is the same for all clients, take care of multiple clients not overwriting it and the leader board persists even if the server crashes and is re-started.
[x] (4 points) Client chooses option 2 (the game) a new phrase and task is send to the client. Phrases are loaded randomly from the file and need to be shown "hidden" on the client. The tasks lets the user know what is expected of them.
[x] (6 points) Multiple clients can enter the SAME game and will thus guess the phrase faster.
[] (2 points) A client wins when they guess the last letter and return to the main menu.
[] (3 points) After they win they will be added to the leader board with 1 point.
[] (2 points) Multiple clients can win together. If more than one client participated in a game and one of them guesses the last letter, all of them win. (see video).
[x] (3 points) Clients guess a letter and the Server checks if that letter is in the phrase and reveals it. Server will send back the phrase with the letter revealed.
[x] (3 points) Server expects that a guess is one letter, if it is anything else the Server should send an error back with a good error message.
[x] (3 points) Client presents the information well.
[x] (3 point) Game quits gracefully when option 3 is chosen.
[x] (3 points) Server does not crash when the client just disconnects (without choosing option 3).
[x] (3 points) Phrases are randomly chosen and the same one does not come up all the time.
[x] (4 points) You need to run and keep your server running on your AWS instance (or somewhere where others can reach it and test it) – if you used the protocol correctly everyone should be able to connect with their client. Keep a log of who logs onto your server (this is already included). I will give it 3 tries over a couple of days, if I can make it through a game and have at least two clients running on it you will get these points (or if someone else was able to do it). You will need to post your exact gradle command we can use (including IP and port) on the channel on Slack #servers.
[x] (2 points) You test at least 3 other servers with your client and should show up on their log file. You should comment on their servers on Slack, this is how we will grade these two points.
[x] (3 points) If user types in "exit" while in the game instead of a letter guess, the client will exit gracefully (quit request will be sent).
NEEDED: On your server always print the answer phrase, so that we do not have to figure it out while grading. Make sure your program is robust with all possible inputs, we should not be able to break it and crash it. We will not be mean when running it but with using it to our best ability and basic "gaming" it should not crash
