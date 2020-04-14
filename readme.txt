JAVA VERSION:

java version "1.8.0_241"
Java(TM) SE Runtime Environment (build 1.8.0_241-b07)


To compile:
in the root folder:

javac ./Server/Peer.java
javac ./Client/TestApp.java

To run:

Start a Peer:
java Server.Peer 1.0 <peer_number> 1 224.0.0.0:24 224.0.0.0:25 224.0.0.0:26

example for Peer 1:
java Server.Peer 1.0 1 1 224.0.0.0:24 224.0.0.0:25 224.0.0.0:26


To the Client:
java Client.TestApp <peer_number> <sub_protocol> <opnd_1> <opnd_2> 

example:
java Client.TestApp 1 BACKUP test.jpg 2
