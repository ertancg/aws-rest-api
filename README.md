# aws-rest-api
REST API that handles some request. Implemented with standart java libraries.

This is a REST API Server that can create a user on the database, retrieve a user from the database, retrieve leaderboard data either Global or Local and update a score on the database.
To run the server make sure that mongodb is installed in your server and mongod service is running with 'aws-api' named database is created. 
Please refer to the following link if you are having trouble with mongoDB. -> https://docs.mongodb.com/manual/installation/ 


If you've setup the mongoDB you can build the Server with Maven. </br>
	1- In '/API' folder type 'mvn install' in your terminal to install the external libraries. </br>
	2- Then type 'mvn package' to test and compile a .jar file under '/target'. </br>
		2.a- 'mvn test' can be used to test independently. </br>
	3- After that go to 'target' folder and type 'java -jar aws-api-1.0.jar <Port>' </br>
		3.a- The jar file only takes one integer as a parameter and that is the Port that its listening to. </br>

After the server is online, you can build the Client with Maven. </br>
	1- In '/Client' folder type 'mvn install' in your terminal to install the external libraries. </br>
	2- Then type 'mvn package' to compile a .jar file under '/target'. </br>
	3- After that go to 'target' folder and type 'java -jar Client-1.0.jar <IP> <Port> <Limit>' </br>
		3.a- The jar file only takes 3 parameters as input. </br>
			First one is the IP address of the server. </br>
			Second one is the Port number of the server. </br>
			The third one is the Maximum number of Users to be displayed when asking for the leaderboard data. </br>
	4- After the client is run. The application asks for a mode to select. </br>
	5- Either 'M' or 'A' can be entered. </br>
		5.a- 'M' is for manual mode. User inputs a name, ISO code and a point value. Then the desired method to be executed is selected 1 to 5. </br>
		5.b- 'A' is for automatic mode. It asks for how many users to be created and then it generates random values for names, points and ISO codes. </br>
			Then it sends it to the server to be processed. </br>
