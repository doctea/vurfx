package vurfeclipse.connectors;

import java.awt.Window.Type;
import java.io.IOException;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vurfeclipse.Targetable;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import IceBreakRestServer.*; 

class RestMessage {
	String url;
	
	String value;
	//String action;
}

public class RestConnector implements Runnable {

	com.google.gson.Gson gson = new Gson();
	
	Project pr;
	
	HashMap<String,Targetable> targets;
	
	public RestConnector(Project pr) {
		this.pr = pr;
	}
	
	public void start() {
		System.out.println("RestConnector start()");
		targets = this.getURLs();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		IceBreakRestServer rest;
		
	    try { 	        // Instantiate it once 
	        rest  = new IceBreakRestServer();
	        rest.setPort(7777);
	        System.out.println(this + ": started server...");
	        //System.exit(1);

	        while (true) {
	          // Now wait for any HTTP request  
	          // the "config.properties" file contains the port we are listening on 
	          rest.getHttpRequest();
	          //System.out.println(this + ": got request");
	          //System.exit(1);
	                     
	          rest.write (this.processRequest(parseURL(rest.resource,0), rest.payload, rest.header).toString());
	          rest.flush();
	        }
	      }
	      catch (IOException ex) {
	        System.out.println(this + ": " + ex.getMessage());
	        System.exit(1);
	      }
	}
	
	public HashMap<String, Targetable> getURLs() {
		HashMap<String,Targetable> urls = new HashMap<String,Targetable> ();
		
		// get the Scenes from the Project
		Iterator it = pr.getScenes().iterator();
		while (it.hasNext()) {
			Scene s = (Scene) it.next();
			
			urls.putAll(s.getTargetURLs());
		}
		
		return urls;
	}
	
	public String parseURL (String url, int trim) {
		String path = "";
		
		String[] splits = url.split("/");
        for (int i = 0 ; i < splits.length-trim ; i++) {
      	  path += splits[i] + (i<splits.length-(1+trim)?"/":"");
        }
        path = path.replace("%20", " ");	// quick and dirty URL decoding
		return path;
	}
	
	public Object processRequest(String url, String payload, Map<String, String> header) {
		if (targets==null) targets = getURLs();
		
		if ("/multi".equals(url)) {
			java.lang.reflect.Type listType = new TypeToken<List<RestMessage>>() {}.getType();
			List<RestMessage> yourList = gson.fromJson(payload, listType);//mapping.get("servers"), listType);
			
			Map<String,String> header2 = new HashMap<String,String> ();
			header2.put("Content-Type", "Text");

			String output = "Multiple:";
		
			Iterator<RestMessage> it = yourList.iterator();			
			while (it.hasNext()) {
				RestMessage rm = it.next();
				output += processRequest(rm.url, rm.value, header2);
			}
			return output; //"Processed " + yourList.size();
		}
		
		if ("/urls".equals(url)) {
			return gson.toJson(targets.keySet().toArray());
		}
		
		//System.out.println("RestConnector: url is " + url + ", payload is " + payload + ", header is " + header);
		//System.exit(1);
		
		if (!header.containsKey("Content-Type") || header.get("Content-Type").contains("Text")) {
			// do nothing
		} else if (header.get("Content-Type").contains("JSON")) {
			payload = gson.fromJson(payload, RestMessage.class).value;
		}
		
		Targetable t = targets.get(url);
		if (t!=null) {
			System.out.println("RestConnector: processRequest for " + url + ", " + payload + ", " + t);
			try {
				return "Processed " + url + " : " + t.target(url, payload);
			} catch (Exception e) {
				return e.toString();
			}
		}
		return "url " + url + " not valid.";
	}
	
	public String ruff_processRequest(String url, String payload) {
        // If we reach this point, we have received a request
        // now we can pull out the parameters from the query-string
        //String name = rest.getQuery("name", "N/A");
        
        /*String url = rest.resource;
        String payload = rest.payload;*/
        
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
        return "Hello world - the url is " + url + " and the payload is " + payload;       
	}
}