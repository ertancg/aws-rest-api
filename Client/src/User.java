


import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class User {
	@BsonProperty("user_id")
	private String guid;
	@BsonProperty("display_name")
	private String displayName;
	@BsonProperty("rank")
	private int rank;
	@BsonProperty("points")
	private int points;
	@BsonProperty("country")
	private String country;
	@BsonProperty("timestamp")
	private String lastUpdated;
	/** This empty constructor is for the CodeRegistry.POJO.
	 * 
	 */
	public User(){
		
	}
	/** This constructor constructs the User object with provided @param's.
	 * 
	 * @param displayName is the Display Name of the User.
	 * @param points is the points of the User.
	 * @param country is the ISO code of the User.
	 * @param lastUpdated is the time the User last got updated.
	 */
	public User(String displayName, int points, String country, String lastUpdated) {
		if(displayName != null) this.guid = UUID.nameUUIDFromBytes(displayName.getBytes()).toString();
		this.displayName = displayName;
		this.points = points;
		this.country = country;
		this.lastUpdated = lastUpdated;
	}
	/** This method @return's the Document format of the User.
	 * 
	 * @return The Document format of the User.
	 */
	public Document getDocument(){
		Document d = new Document();
		return d.append("user_id", getGuid()).append("display_name", getDisplayName())
				.append("rank", getRank()).append("points", getPoints())
				.append("country", getCountry())
				.append("timestamp", getLastUpdated());
	}
	/** This method @return's the a simple format of the User.
	 * 
	 * @return The simple format of the User.
	 */
	public String getLeaderboadrdJson(){
		Document d = new Document();
		return d.append("rank", getRank()).append("points", getPoints())
				.append("display_name", getDisplayName())
				.append("country", getCountry()).toJson();
	}
	/** This method @return's the JSON format of the User.
	 * 
	 * @return The JSON format of the User.
	 */
	public String getJson(){
		return this.getDocument().toJson();
	}
	/** This method @return's the Special ID of the User.
	 * 
	 * @return The Special ID of the User.
	 */
	public String getGuid() {
		return guid.trim();
	}
	/** This method sets the Special ID of the User.
	 * 
	 * @param guid the Special ID to be set.
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	/** This method @return's the Display Name of the User.
	 * 
	 * @return The Display Name of the User.
	 */
	public String getDisplayName() {
		return displayName.trim();
	}
	/** This method sets the Display Name of the User.
	 * 
	 * @param displayName is the Display Name to be set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/** This method @return's the rank of the User.
	 * 
	 * @return The rank of the User.
	 */
	public int getRank() {
		return rank;
	}
	/** This method sets the rank of the User.
	 * 
	 * @param rank is rank to be set.
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	/** This method @return's the points of the User.
	 * 
	 * @return Points of the User.
	 */
	public int getPoints() {
		return points;
	}
	/** This method sets the points of the User.
	 * 
	 * @param points is points to be set.
	 */
	public void setPoints(int points) {
		this.points = points;
	}
	/** This method @return's the ISO code of the User.
	 * 
	 * @return The ISO code of the User.
	 */
	public String getCountry() {
		return country.trim();
	}
	/** This method sets the ISO code of the User.
	 * 
	 * @param country is the ISO code to be set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/** This method @return's the time this User has updated.
	 * 
	 * @return The time this User last updated.
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}
	/** This method set the time this User last edited.
	 * 
	 * @param lastUpdated is the time to be set.
	 */
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	/** This method @return's a string representation of the Object.
	 * 
	 */
	@Override
	public String toString() {
		return "User [guid=" + guid + ", displayName=" + displayName + ", rank=" + rank + ", points=" + points
				+ ", country=" + country + ", lastUpdated=" + lastUpdated + "]";
	}
	
}
