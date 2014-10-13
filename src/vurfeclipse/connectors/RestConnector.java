package vurfeclipse.connectors;

import java.io.IOException;

import vurfeclipse.filters.Filter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Mutable;
import IceBreakRestServer.*; 

public class RestConnector extends Thread {

	Project pr;
	
	public RestConnector(Project pr) {
		this.pr = pr;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		IceBreakRestServer rest;
		
	    try { 	        // Instantiate it once 
	        rest  = new IceBreakRestServer();
	        rest.setPort(7777);

	        while (true) {

	          // Now wait for any HTTP request  
	          // the "config.properties" file contains the port we are listening on 
	          rest.getHttpRequest();
	          System.out.println(this + ": got request");
	                     
	          // If we reach this point, we have received a request
	          // now we can pull out the parameters from the query-string
	          String name = rest.getQuery("name", "N/A");
	          
	          String url = rest.resource;
	          String payload = rest.payload;
	          
	          System.out.println(this + ": got URL " + url + " with payload " + payload);
	          
	          String path = "";
	          
	          String[] splits = url.split("/");
	          System.out.println(splits.length);
	          for (int i = 0 ; i < splits.length-1 ; i++) {
	        	  path += splits[i] + (i<splits.length-2?"/":"");
	          }
	          //path = path.substring(1);	          
	          Object o = pr.getObjectForPath(path);
	          
	          System.out.println("toggling mute for " + o);
	          if (o instanceof Mutable)
	          	((Mutable)o).toggleMute();
	          else
	        	  System.out.println("unsure what to do for non-Mutable " + o + " (path was " + path + ")");
	          
	          //System.exit(0);
	          
	          // so then we decode the URL (if required?) and fire off a change event based on the payload.

	          // we can now produce the response back to the client.
	          // That might be XML, HTML, JSON or just plain text like here:
	          rest.write("Hello world - the 'name' parameter is: " + name + " and the url is " + url + " and the payload is " + payload);
	          
	          rest.flush();
	        }
	      }
	      catch (IOException ex) {
	        System.out.println(ex.getMessage());
	      }
	    }
}