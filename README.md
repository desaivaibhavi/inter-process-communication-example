inter-process-communication-example
===================================

I have implemented a simple broadcasting portal using the ipc concepts and sockets in java. I have also implemented the concept of multithreading. Here the authentication is also implemented.

There are three java files.
1. Server.java
2. Client.java
3. AcceptClient.java

Running the program:
Follow the following steps to execute the program
-->make

Open different terminals and run the server and client (multiple) codes.

to run the server : java Server 4119
to run the client : java Client localhost 4119

Here the server takes one parameter, the port number 
Here the client takes two parameters, the ip and the port number

When the server program is executed, acceptclient continuously listens to accept clients. Once the  client makes connection through socket, new thread is created.


There are mainly four commands on this program: 

0. log in with valid username and password. (A list is given in userlist.txt)
1. whoelse: displays the name of other connected users
2. wholasthr: displays the name of the user who are connected within last 10 mins 
3. broadcast "message"
