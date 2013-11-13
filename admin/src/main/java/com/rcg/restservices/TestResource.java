package com.rcg.restservices;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class TestResource {

	@GET
	@Path("/")
	public String test() {
		long time = System.currentTimeMillis();
		return "Test result:" + Long.toString(time);
	}
}
