# HybridShare
A Hybrid-P2P File Sharing Program

## Instructions:

### Docker:

This application is best run through Docker containers.
To run the demo move to the java directory and run 
'docker build -t hybridshare .' 
to create the image used for running the SuperPeer and Peers

### SuperPeer:

Next, to run the SuperPeer run the command:
'docker run -it --name Server hybridshare /bin/bash'
this will create a container named Server and place you in a terminal running on the container Server.
From inside the Server container terminal, run:
'java SuperPeer' to start the SuperPeer Server.

### Clients:

Then, in a new terminal for each client, run
'docker run -it --name CLIENTNAME hybridshare /bin/bash' where CLIENTNAME is replaced with the name you want to give this client container. 

The Demo works best with 2 clients
For each client container, run the command:
'java BasicP2P <p2pPort> <superPort> <superIP> <File Client Owns> <File Client Needs>'

for two example clients this would look like this:
java BasicP2P 12345 12347 172.17.0.2 FileB.txt FileA.txt
java BasicP2P 12346 12347 172.17.0.2 FileA.txt FileB.txt
