# HybridShare
A Hybrid-P2P File Sharing Program

## Instructions:

### Docker:

This application is best demonstrated through Docker containers.
To build the image and run the demo move to the java directory and run 
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
'java BasicP2P <superPort> <superIP> <File Client Needs>'

for two example clients this would look like this:
java BasicP2P 12347 172.17.0.2 FileB.txt 
java BasicP2P 12347 172.17.0.2 FileA.txt 

The filepaths that are in the /share directory will automatically be forwarded 
to the Super Peer and matched accordingly. For demonstration purposes try 
removing some of the files and sending them from on peer to another.
