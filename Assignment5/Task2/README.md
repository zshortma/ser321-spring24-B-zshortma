# Important
Please exectute the program as specified below. There is a delay from starting to connecting please give it ~8-10 seconds for things to connect and process. 

# Screencast
https://drive.google.com/file/d/13EqCl_rehh36lqzkTZrhx24dJ_JENFZH/view?usp=sharing

# Step One : Run Client
gradle runClient  -PpeerName=Client -Ppeer="localhost:8000" -Pleader="localhost:8000" -PisLeader=true -q --console=plain

# Step Two : Run Leader
gradle runLeader  -PpeerName=Leader -Ppeer="localhost:8000" -Pleader="localhost:8000" -PisLeader=true -q --console=plain

# Step Three : Run Nodes
gradle runNode -PnodeName=Zoe -Pnodes="localhost:8383" -Pleader="localhost:8000" -PisLeader=false -q --console=plain

gradle runNode -PnodeName=Sandy -Pnodes="localhost:8777" -Pleader="localhost:8000" -PisLeader=false -q --console=plain

gradle runNode -PnodeName=Jimmy -Pnodes="localhost:8666" -Pleader="localhost:8000" -PisLeader=false -q --console=plain

# Explain Program
This program implements a client / server / node system that intergrates messaging communication, threading and distributed systems. This is done through starting a client specifiying a string and a letter to send to a leader which will then distribute to different nodes for processing. The nodes would then communicate back to the leader indicating issues or matches and finally send the results from the leader to the client indicating the results. 

# Explain my protocol
I implement a simple socket based communication. The sockets were used to connect and communicate the nodes and leader in the system allowing the sending of messages.  The messages exchanged were through the use of formatted JSON strings that would allow for easy serilization of the messages. Within the string the message type, name and message were included in each JSON request. I also utlized message handling to assist in helping the distributive system like behavior of evenly giving different task to each node on the network.

# Workflow
I did my best to break up this program as reccomeneded by first doing the leader/nodes getting that working correctly and then adding the client once those were working. However, the introduction of the client made some problems.

A client is started.
A leader is started.
At least three nodes are started. 

Client enters a string plus a letter.

The leader will take this word, divide it up and send to the avalible connected nodes. 
This is shown on the leader output : the letter of the word and the one that needs matched to and the node "port" it is sent to. 

The nodes receieve the two letters such as "lo" or "oo" and will run it through a matching algorithm. It will return a 0 or 1 indicating a match or not. This will then be sent to the leader for adding all recieved messages. 

The leader will then run a checking algorithm to see if the nodes correctly calculated. The results then if good will be displayed to the client, if not the error is sent to the cleint and the program will have concluded. 

# Requirements Fulfilled 

Gradle task for nodes, leader and client. I completed this (1,2,3). 

Three nodes are required and can be started. I completed this(4,5). 

Client connects to leader, messaging allowed. I completed this (6).

Leader splits up the message and sends evenely. I completed this (7).

Leader sends each node a specific protion. I completed this, I added output in leader to help visualize where each was sent since the nodes started having threading problems after I started adding the client features. (8).

I tried threading the nodes, it made it messy even when I tried adding resource locking and syncs.(9).

Nodes do receieve and process their portion. (10).

Node is sent back to leader, but with threading I couldnt get this corrected and ran out of time so it is probably incorrect number count. Leader is shown a count see output on leader -again probably not the right count.(11/12)

Double checking. I implemented double checking, just not the complete same as in the doc. (13)

Added handling but the program is having threading issues. (14).






