package test.java;

import static org.junit.Assert.*;

import org.ertancg.Database;
import org.ertancg.User;
import org.junit.Test;

import com.mongodb.client.MongoCollection;

public class DatabaseTest {
	Database databaseToBeTested = new Database(0,0);
	
	
	@Test
	public void testCreateCollection() {
		String collectionToBeCreated = "test";
		databaseToBeTested.createCollection(collectionToBeCreated);
		boolean expected = true;
		boolean value = databaseToBeTested.collectionExist(collectionToBeCreated);
		assertEquals(expected, value);
	}

	@Test
	public void testRetrieveCollection() {
		String collectionToBeTested = "test";
		databaseToBeTested.createCollection(collectionToBeTested);
		databaseToBeTested.upsertDocument(new User("test", 1, "TEST", "0:0:0"), collectionToBeTested);
		MongoCollection<User> value = databaseToBeTested.retrieveCollection(collectionToBeTested);
		assertNotEquals(null, value);
	}

	@Test
	public void testCollectionExist() {
		String collectionToBeTested = "test";
		databaseToBeTested.createCollection(collectionToBeTested);
		boolean expected = true;
		boolean value = databaseToBeTested.collectionExist(collectionToBeTested);
		assertEquals(expected,value);
	}


	@Test
	public void testGetDocument() {
		String collectionToBeTested = "test";
		databaseToBeTested.createCollection(collectionToBeTested);
		User expected = new User("testGetDocument", 1, "TEST", "1:2:3");
		databaseToBeTested.upsertDocument(expected, collectionToBeTested);
		User value = databaseToBeTested.getDocument(expected.getGuid(), collectionToBeTested);
		
		assertEquals(expected.getJson(), value.getJson());
	}

	@Test
	public void testUpsertDocument() {
		String collectionToBeTested = "test";
		databaseToBeTested.createCollection(collectionToBeTested);
		User expected = new User("testGetDocument", 1, "TEST", "1:2:3");
		databaseToBeTested.upsertDocument(expected, collectionToBeTested);
		User value = databaseToBeTested.getDocument(expected.getGuid(), collectionToBeTested);
		assertEquals(expected.getJson(), value.getJson());
	}

}
