package org.openshift.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.bson.types.ObjectId;
import org.openshift.data.DBConnection;
import org.openshift.data.Park;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;


@RequestScoped
@Path("/parks")
public class ParkWS {
	
	@Inject 
	private DBConnection dbConnection;
	
	//get all the parks
	@GET()
	@Produces("application/json")
	public List getAllParks(){
		ArrayList allParksList = new ArrayList();
		DB db = dbConnection.getDB();
		DBCollection parkListCollection = db.getCollection("parkpoints");
		DBCursor cursor = parkListCollection.find();
		try {
			while(cursor.hasNext()) {
				DBObject dataValue = cursor.next();
				HashMap holder = new HashMap<String, Object>();
				holder.put("name",dataValue.get("Name"));
				holder.put("position", dataValue.get("pos"));
				holder.put("id", dataValue.get("_id").toString());
				allParksList.add(holder);
				
            }
        } finally {
            cursor.close();
        }

		return allParksList;
	}
	
	
	
	//////////get a park given the id
	@GET()
	@Produces("application/json")
	@Path("park/{id}")
	public HashMap getAPark(@PathParam("id") String id){
		
		DB db = dbConnection.getDB();
		DBCollection parkListCollection = db.getCollection("parkpoints");
		
		DBObject park = parkListCollection.findOne(new BasicDBObject().append("_id",  new ObjectId(id)));
		HashMap holder = new HashMap<String, Object>();
		holder.put("name",park.get("Name"));
		holder.put("position", park.get("pos"));
		holder.put("id", park.get("_id").toString());
		return holder;
	}
	
	
	
	///////////get parks near a coord  ?lat=37.5&lon=-83.0
	@GET
	@Produces("application/json")
	@Path("near")
	public List findParksNear(@QueryParam("lat") float lat, @QueryParam("lon") float lon){
		ArrayList<Map> allParksList = new ArrayList<Map>();
		DB db = dbConnection.getDB();
		DBCollection parkListCollection = db.getCollection("parkpoints");
		
		//make the query object
		BasicDBObject spatialQuery = new BasicDBObject();
		ArrayList posList = new ArrayList();
		posList.add(new Float(lon));
		posList.add(new Float(lat));
		spatialQuery.put("pos", new BasicDBObject("$near", posList));
		
		System.out.println("spatial query: " + spatialQuery.toString());
		
		
		DBCursor cursor = parkListCollection.find(spatialQuery);
		try {
			while(cursor.hasNext()) {
				DBObject dataValue = cursor.next();
				HashMap holder = new HashMap<String, Object>();
				holder.put("name",dataValue.get("Name"));
				holder.put("position", dataValue.get("pos"));
				holder.put("id", dataValue.get("_id").toString());
				allParksList.add(holder);
            }
        } finally {
            cursor.close();
        }

		return allParksList;
		
	}
	
	
	////////get parks near a coord with a certain name
	@GET
	@Produces("application/json")
	@Path("name/near/{name}")
	public List findParksNearName(@PathParam("name") String name, @QueryParam("lat") float lat, @QueryParam("lon") float lon){
		ArrayList<Map> allParksList = new ArrayList<Map>();
		DB db = dbConnection.getDB();
		DBCollection parkListCollection = db.getCollection("parkpoints");
		
		//make the query object
		BasicDBObject wholeQuery = new BasicDBObject();
		//first the regex
		Pattern namePattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
		wholeQuery.put("Name", namePattern);
		
		/*then the spatial */
		ArrayList posList = new ArrayList();
		posList.add(new Float(lon));
		posList.add(new Float(lat));
		wholeQuery.put("pos", new BasicDBObject("$near", posList));
		
		
		System.out.println("whole query: " + wholeQuery.toString());
		
		
		DBCursor cursor = parkListCollection.find(wholeQuery);
		try {
			while(cursor.hasNext()) {
				DBObject dataValue = cursor.next();
				HashMap holder = new HashMap<String, Object>();
				holder.put("name",dataValue.get("Name"));
				holder.put("position", dataValue.get("pos"));
				holder.put("id", dataValue.get("_id").toString());
				allParksList.add(holder);
            }
        } finally {
            cursor.close();
        }

		return allParksList;
		
	}
	
	
	///////still need the insert
	@POST
	@Path("park")
	@Consumes("application/json")
	@Produces("application/json")
	public HashMap insertAPark(Park park){
		BasicDBObject parkObject = new BasicDBObject("Name",park.getName());
		parkObject.append("pos", park.getPos());
		
		DB db = dbConnection.getDB();
		DBCollection parkListCollection = db.getCollection("parkpoints");
		try{
			parkListCollection.insert(parkObject, WriteConcern.SAFE);
		} catch (Exception e) {
			System.out.println("threw an exception: " + e.getClass() + " :: " + e.getMessage());
		}
		
		//now make it look pretty
		HashMap holder = new HashMap<String, Object>();
		holder.put("name",parkObject.get("Name"));
		holder.put("position", parkObject.get("pos"));
		holder.put("id", parkObject.get("_id").toString());
		return holder;
		
	}
	
	
/****** Just for testing purposes ***********/	
	@GET()
	@Path("/test")
	@Produces("text/plain")
	public String sayHello() {
		System.out.println("Where is this getting written");
	    return "Hello World In Both Places";
	}

}
