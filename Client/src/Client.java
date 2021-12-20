import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;



public class Client {
	private DateTimeFormatter dtf;  
	
	private String serverAddress;
	private int port;
	private User client;
	
	private Socket socket;
	private Socket socket2;
	
	private OutputStream out;
	private InputStream in;
	
	private BufferedReader br;
	private PrintWriter os;
	
	private HashMap<String, String> requestParameters;
	private int limit;
	
	private ArrayList<User> leaderboard;
	/**
	 * 
	 * @param serverAddress is public IPv4 address of the server.
	 * @param port is the port number.
	 * @param limit is the number of users to be displayed on the leaderboard.
	 */
	public Client(String serverAddress, int port, int limit){
		this.dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		this.serverAddress = serverAddress;
		this.port = port;
		this.requestParameters = new HashMap<String, String>();
		this.limit = limit;
	}
	/** This method starts a connection with the server.
	 * 
	 */
	public void startConnection(){
		try{
			this.socket = new Socket(this.serverAddress, this.port);
			this.socket2 = new Socket(this.serverAddress, this.port);
			this.out = this.socket.getOutputStream();
			this.in = this.socket2.getInputStream();
			this.br = new BufferedReader(new InputStreamReader(this.in));
			this.os = new PrintWriter(this.out, true);
		}catch (IOException e){
			System.out.println("Could not initialize I/O Streams or Sockets.");
			e.printStackTrace();
		}
	}
	/** This method sends a GET Request to the server and retrieves the 'Global' leaderboard from the server.
	 * 
	 */
	public void getLeaderboard(){
		String header = GET_REQUEST + "/leaderboard" + VERSION + USER_AGENT + HOST + this.socket.getInetAddress() + CONTENT_TYPE + CONNECTION;
		String body = "";
		os.write(header);
		os.write(body);
		os.flush();
		os.close();
		recieve(true);
	}
	/** This method sends a GET Request to the server and retrieves the 'Local' leaderboard according to the specified country.
	 * 
	 * @param country is the ISO code.
	 */
	public void getCountryLeaderboard(String country){
		String header = GET_REQUEST + "/leaderboard/" + country + VERSION + USER_AGENT + HOST + this.socket.getInetAddress() + CONTENT_TYPE + CONNECTION;
		String body = "";
		os.write(header);
		os.write(body);
		os.flush();
		os.close();
		recieve(true);
	}
	/** This method sends a GET Request to the server and retrieves the profile of the user.
	 * 
	 */
	public void getProfile(){
		String header = GET_REQUEST + "/user/profile/" + this.client.getGuid() + VERSION + USER_AGENT + HOST + this.socket.getInetAddress() + CONTENT_TYPE + CONNECTION;
		String body = "";
		os.write(header);
		os.write(body);
		os.close();
		recieve(true);
	}
	/** This method sends a POST Request to the server and updates the points of the User by desired points. 
	 * 
	 * @param points is the new point to be updated.
	 */
	public void updateScore(int points){
		String header = POST_REQUEST + "/score/submit" + VERSION + USER_AGENT + HOST + this.socket.getInetAddress() + CONTENT_TYPE + CONNECTION;
		Document d = new Document("score_worth", this.client.getPoints() + points).append("user_id", this.client.getGuid()).append("timestamp", this.dtf.format(LocalTime.now()));
		String body = d.toJson();
		os.write(header);
		os.write(body);
		os.flush();
		os.close();
		recieve(false);
	}
	/** This method sends a POST Request to the server and creates a User in the database.
	 * 
	 */
	public void createUser(){
		String header = POST_REQUEST + "/user/create" + VERSION + USER_AGENT + HOST + this.socket.getInetAddress() + CONTENT_TYPE + CONNECTION;
		String body = this.client.getJson();
		os.write(header);
		os.write(body);
		os.flush();
		os.close();
		recieve(false);
	}
	/** This method recieved the response from the server and @return's it.
	 * 
	 * @param check is the boolean value to determine if response will be contaion a User data.
	 * @return the response from the server.
	 */
	public String recieve(boolean check){
		String line;
		String response = "";
		
		try {
			while((line = this.br.readLine()) != null){
				if(line.length()<= 0) break;
				 response += line;	
				 System.out.println("Response from the server: " + line);
			}
			if(check){
				printLeaderboard(response.substring(response.indexOf('{')));
			}
		} catch (IOException e) {
			System.out.println("Could not read from the server.");
			e.printStackTrace();
		}finally{
			stop();
		}
		
		return response;
	}
	/** This method prints the leaderboard data with specified limit.
	 * 
	 * @param body is the body of the HTTP Response.
	 */
	public void printLeaderboard(String body){
		String[] data = body.split(",");
		this.leaderboard = new ArrayList<User>();
		int i = 0;
		System.out.println("---------------LEADERBOARD---------------");
		for(String s: data){
			if(!s.contains("}")){
				this.requestParameters.put(s.substring(s.indexOf("\"") + 1, s.indexOf(":") - 1).trim(), 
						s.substring(s.indexOf(":") + 1).trim());
			}else{
				this.requestParameters.put(s.substring(s.indexOf("\"") + 1, s.indexOf(":") - 1).trim(), 
						s.substring(s.indexOf(":") + 1, s.indexOf('}')).trim());
				User u = new User(this.requestParameters.get("display_name").replace('\"', ' ').trim(), Integer.parseInt(this.requestParameters.get("points")), this.requestParameters.get("country").replace('\"', ' ').trim(), this.dtf.format(LocalTime.now()));
				u.setRank(Integer.parseInt(this.requestParameters.get("rank")));
				this.leaderboard.add(u);
				if(i < this.limit){
					System.out.println(u.getLeaderboadrdJson());
				}
				this.requestParameters.clear();
				i++;
			}
		}
	}
	/** This method closes the connection to the server.
	 * 
	 */
	public void stop(){
		try {
			this.in.close();
			this.out.close();
			this.socket.close();
			this.socket2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** This method sets the User.
	 * 
	 * @param u is the User to be set.
	 */
	public void setUser(User u){
		this.client = u;
	}
	/** This method @return's the User.
	 * 
	 * @return The User.
	 */
	public User getUser(){
		return this.client;
	}
	private final String GET_REQUEST = "GET ";
	private final String POST_REQUEST = "POST ";
	private final String VERSION = " HTTP/1.1\n";
	private final String USER_AGENT = "User-Agent: REST/API/CLIENT\n";
	private final String HOST = "Host: ";
	private final String CONTENT_TYPE = "Content-type: application/json\n";
	private final String CONNECTION = "Connection: keep-alive\n";
}
