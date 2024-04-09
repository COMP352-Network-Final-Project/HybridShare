# HybridShare
Hybrid-P2P File Sharing Program

## Instructions:
To run the program first run the SuperPeer class and wait for the server to begin waiting for connections, Then you may run any number of BasicP2P class clients with the following syntax:
    BasicP2P {Port to run client on} {Port of SuperPeer}

any messages sent from any of the clients will be delivered to the server. To forward a message to another client a client can prefix their message with the client's port that they wish to message and a ';' character
    ex. "12346;Hello Peer"