package org.openshift.webservice;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@RequestScoped
@Path("/parks")
public class ParkWS {
	
	
/****** Just for testing purposes ***********/	
	@GET()
	@Path("/test")
	@Produces("text/plain")
	public String sayHello() {
	    return "Hello World In Both Places";
	}

}
