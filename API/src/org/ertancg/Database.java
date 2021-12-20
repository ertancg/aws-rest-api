package org.ertancg;


import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.FindOneAndReplaceOptions;

public class Database {
	private DateTimeFormatter dtf;  
	
	private MongoClient mongo;
	private MongoDatabase db;
	private CodecRegistry pojo;
	
	private int requestHandlerID;
	private int threadID;
	
	/** This constructor assigns fields for logging and initializes client connection with correct codec to 
	 * 	interact with the database. 
	 * 
	 * @param threadID is the ServerThread ObjectID for logging purposes.
	 * @param requestHandlerID is the RequestHandler ObjectID for logging purposes.
	 */
	public Database(int threadID, int requestHandlerID){
		this.threadID = threadID;
		this.requestHandlerID = requestHandlerID;
		this.dtf = DateTimeFormatter.ofPattern("HH:mm:ss:ms");
		this.mongo = MongoClients.create("mongodb://localhost:27017");
		this.db = this.mongo.getDatabase("aws-api");
		if(!collectionExist("Global")) {
			printLog("Collection Global doesn't exist.", -1);
			this.db.createCollection("Global");
			printLog("Created 'Global'", 1);
			
		}
		this.pojo = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	}
	
	/** This method creates a collection in the database with the desired name.
	 * 
	 * @param collectionName is the name of the created collection.
	 */
	public void createCollection(String collectionName){
		if(!collectionExist(collectionName)){
			this.db.createCollection(collectionName);
			printLog("Collection with name " + collectionName + " created.", 1);
		}
	}
	
	/** This method extracts the collection from the database.
	 * 
	 * @param collectionName is the collection to be extracted.
	 * @return A MongoCollection object that stores User.
	 */
	public MongoCollection<User> retrieveCollection(String collectionName){
		printLog("Retrived collection named " + collectionName + ".", 1);
		return this.db.getCollection(collectionName, User.class).withCodecRegistry(pojo);
	}
	
	/** This method check whether a collection exists or not.
	 * 
	 * @param collectionName the collection name to check.
	 * @return true if the collection exists, false if not.
	 */
	public boolean collectionExist(String collectionName){
		boolean check = false;
		MongoIterable<String> curretDatabases = this.db.listCollectionNames();
		for(String s: curretDatabases){
			if(s.equals(collectionName)) check = true;
		}
		printLog(check ? collectionName + " exists." : collectionName + " does not exist.", 1);
		return check;
	}
	
	/** This method @return's the leaderboard from the desired collection.
	 * 
	 * @param collectionName is the name of the collection.
	 * @return The leaderboard ranking from highest to lowest from the desired collection.
	 */
	public String getLeaderboard(String collectionName){
		String leaderboard = "";
		MongoCollection<User> col = retrieveCollection(collectionName);
		FindIterable<User> iter = col.find();
		iter.sort(new Document("points", -1));
		int i = 1;
		for(User u : iter){
			u.setRank(i);
			i++;
			leaderboard += u.getJson() + ",";
		}
		return leaderboard;
	}
	
	/** This methdod @return's a specific User with a guid from the desired collection.
	 * 
	 * @param userGUID is the User's special id.
	 * @param collectionName is the name of the collection.
	 * @return desired User object.
	 */
	public User getDocument(String userGUID, String collectionName){
		MongoCollection<User> col = retrieveCollection(collectionName);
		Bson query = new Document("user_id", userGUID);
		User u = col.find(query).iterator().tryNext();
		printLog("Document with id: " + userGUID + " retrieved.", 1);
		return u;
	}
	
	/** This method update-or-insert or upsert's the User to the desired collection.
	 * 
	 * @param u is the desired User object to be 'upsert'ed.
	 * @param collectionName is the collection name.
	 */
	public void upsertDocument(User u, String collectionName){
		MongoCollection<User> col = retrieveCollection(collectionName);
		
		if(getDocument(u.getGuid(), u.getCountry()) != null){
			Bson query = new Document("user_id", u.getGuid());
			FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
			options.upsert(true);
			col.findOneAndReplace(query, u);
			printLog("Document updated with the id of " + u.getGuid() + ".", 1);
		}else{
			col.insertOne(u);
			printLog("Inserted document with id of " + u.getGuid() + ".", 1);
		}
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
			System.out.println("["+ time +"][ServerThread-" + threadID + "][RequestHandler-" + requestHandlerID + "][Database-" + getDatabaseID() + "][ERROR]: " + msg);
		}else{
			System.out.println("["+ time +"][ServerThread-" + threadID + "][RequestHandler-" + requestHandlerID + "][Database-" + getDatabaseID() + "][LOG]: " + msg);
		}
		
	}
	
	/** This method @return's the ObjectID of this Object.
	 * 
	 * @return The ObjectID of the Object.
	 */
	public int getDatabaseID(){
		return System.identityHashCode(this) ;
	}
	
	/** This method is for Garbage Collection. It closes the connection with the database and deconstructs the object.
	 * 
	 */
	protected void finalize(){
		this.mongo.close();
	}
	
	
}
