Module Five : Distributed Algorithms
Task 1 : Peer to Peer messaging

How to run : 

gradle runPeer --args "Zoe 8999" --console=plain -q
gradle runPeer --args "Gigi 8080" --console=plain -q

For this project I used the simple peer to peer starter code. The goal of the program 
was to allow peers to join the network without having to specify the port/server they want 
to connect to -it should be done automatically after the first peer has joined. 

To start I created a gradle task that would require a name and a port of the joining peer. This
would connect to the first peer in the systems by grabbing its host and port number. A problem I 
ran into was that the nodes after the initial establishing of a connection would not reach out to collect
the newer peers so to fix this I had an algorithm that would be triggered when a new node grabbed the 
existing peers and would then reconnect all the current peers to the newest one. This allowed the connection
to be triggered when needed to keep all peers in the loop. This would also assist if a peer was to disconnect,
the disconnection would be triggered and all the peers would still be able to communicate with the newer 
updated list of peers.

I added some additional functionality as well such as: 
1. if the first peer is connected it would output it is the first connected and waiting for another to join.
It would then connect and work as usual allowing the peers to communicate. 

2. If a connected peer is to disconnect and no other peers exists it would notify the 
peer that the other peer disconnect and it is waiting for another connection. This logic allows the peers
to stay working since we are not to use a leader and disconnect once the "leader" is gone. 