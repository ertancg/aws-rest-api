package org.ertancg;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class RequestHandler {
	private DateTimeFormatter dtf;  
	
	private HashMap<String, String> requestParam;
	private Database db;
	
	private boolean get = false, post = false;
	private int threadID;
	
	/** This method creates a Database object and parses the 'Header', 'Body' parts of the HTTP Request. 
	 * 
	 * @param header is the Header of the HTTP Request.
	 * @param body is the Body of the HTTP Request.
	 * @param threadID is ServerThread ObjectID for logging purposes.
	 */
	public RequestHandler(String header, String body,int threadID){
		
		this.threadID = threadID;
		this.dtf = DateTimeFormatter.ofPattern("HH:mm:ss:ms");
		this.db = new Database(threadID, getRequestHandlerID());
		
		this.requestParam = new HashMap<String, String>();
		body = body.replace('}', ' ').trim();
		String[] headerArray = header.split(" ");
		String[] bodyArray = body.split(",", 0);
		
		
		if(headerArray[0].equals("GET")){
			this.get = true;
			this.requestParam.put("Method", headerArray[0]);
			this.requestParam.put("Location", headerArray[1].substring(1));
			
		}else if(headerArray[0].equals("POST")){
			
			this.post = true;
			this.requestParam.put("Method", headerArray[0]);
			this.requestParam.put("Location", headerArray[1].substring(1));
			for(String s: bodyArray){
				this.requestParam.put(s.substring(s.indexOf("\"") + 1, s.indexOf(":") - 1).trim(), 
										s.substring(s.indexOf(":") + 1).trim());
			}
		}
	}
	
	/** This method executes the request from the HTTP Request then @return's a HTTP Response according to operation. 
	 * 
	 * @return The HTTP Response.
	 */
	public String Handle(){
		User u;
		String response = "";
		if(get){
			if(this.requestParam.get("Location").contains("leaderboard") && this.requestParam.get("Location").indexOf("/") < 0){
				response = respond(this.db.getLeaderboard("Global"), "200 OK");
			}else if(this.requestParam.get("Location").contains("leaderboard") && this.requestParam.get("Location").indexOf("/") >= 0){ 
				response = respond(this.db.getLeaderboard(this.requestParam.get("Location").substring(this.requestParam.get("Location").indexOf("/", 2) + 1)), "200 OK");
			}else if(this.requestParam.get("Location").contains("profile")){
				u = this.db.getDocument(this.requestParam.get("Location").substring(this.requestParam.get("Location").indexOf("/", 11) + 1), "Global");
				if(u == null){
					printLog("Could not get the user data from the server", -1);
					response = respond(null, "500 Internal Server Error.");
				}else if(!u.getGuid().equals(this.requestParam.get("Location").substring(this.requestParam.get("Location").indexOf("/", 11) + 1))){
					printLog("Could not get the 'right' user data from the server", -1);
					response = respond(null, "500 Internal Server Error.");
				}else{
					response = respond(u.getDocument().toJson(), "200 OK");
				}
			}
		}else if(post){
			if(this.requestParam.get("Location").contains("submit")){
				u = this.db.getDocument(this.requestParam.get("user_id").replace('\"', ' ').trim(), "Global");
				if(u == null){
					printLog("Could not get the user data from the server", -1);
					response = respond(null, "500 Internal Server Error.");
				}else{
					u.setPoints(Integer.parseInt(this.requestParam.get("score_worth")));
					u.setLastUpdated(this.dtf.format(LocalTime.now()));
					this.db.upsertDocument(u, "Global");
					this.db.upsertDocument(u, u.getCountry());
					response = respond(null, "200 OK");
				}
				
			}else if(this.requestParam.get("Location").contains("create")){
				u = new User(this.requestParam.get("display_name").replace('\"', ' ').trim(), Integer.parseInt(this.requestParam.get("points")), this.requestParam.get("country").replace('\"', ' ').trim(), this.dtf.format(LocalTime.now()));
				this.db.upsertDocument(u, "Global");
				response = respond(null, "200 OK");
				
				if(this.db.collectionExist(u.getCountry())){
					this.db.upsertDocument(u, u.getCountry());
				}else{
					this.db.createCollection(u.getCountry());
					this.db.upsertDocument(u, u.getCountry());
					
				}
			}
		}else{
			response = respond(null, "400 Bad Request");
		}
		return response;
	}
	
	/** This method generates a HTTP Response by adding given @param body and @param code.
	 * 
	 * @param body is the body of the HTTP Response.
	 * @param code is the status code message of the HTTP Response.
	 * @return HTTP Response.
	 */
	public String respond(String body, String code){
		String Status = "HTTP/1.1 "+ code +"\n";
		String Host = "Server: HTTP server/1.1\n";
		String Content_Type = "Content-type: application/json\n" ;
		String Length = "Content-Length: " + ((body == null) ? 0 : (body.length()))+ "\n";
		return Status + Host + Content_Type + Length + body + "\n\n";
	}
	
	/** This method prints the desired logging messages.
	 * 
	 * @param msg is the message to be logged.
	 * @param error is the value to determine the type of the log. -1 for error, 1 for normal logs.
	 */
	public void printLog(String msg, int error){
		LocalTime now = LocalTime.now();
		String time = this.dtf.format(now);
		if(error < 0){
			System.out.println("["+ time +"][ServerThread-"+ this.threadID +"][RequestHandler-" + getRequestHandlerID() + "][ERROR]: " + msg);
		}else{
			System.out.println("["+ time +"][ServerThread-"+ this.threadID +"][RequestHandler-" + getRequestHandlerID() + "][LOG]: " + msg);
		}	
	}
	
	/** This method @return's the ObjectID of this Object.
	 * 
	 * @return The ObjectID of the Object.
	 */
	public int getRequestHandlerID(){
		return System.identityHashCode(this) ;
	}
	/** This method is for Garbage Collection. It closes the connection with the database and deconstructs the object.
	 * 
	 */
	protected void finalize(){
		this.db.finalize();
	}
}
