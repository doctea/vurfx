package vurfeclipse.connectors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	HashMap<String,Targetable> exposed = new HashMap<String,Targetable> ();
	
	public RestConnector(Project pr) {
		this.pr = pr;
	}
	
	public void start() {
		System.out.println("RestConnector start()");
		//targets = this.getURLs();
	}
	
	public HashMap<String,Targetable> getTargets() {
		if (targets==null) targets = this.getURLs();
		return targets;
	}
	
	public RestConnector expose(String url) {
		System.out.println("RestConnector: adding '" + url + "' - " + getTargets().get(url));
		if (getTargets().get(url)==null)
			System.exit(0);
		exposed.put(url, getTargets().get(url));
		return this;
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
	        
	        //rest.header.put("Content-Type", "text/html");

	        while (true) {
	          // Now wait for any HTTP request  
	          // the "config.properties" file contains the port we are listening on 
	          rest.getHttpRequest();
	          //System.out.println(this + ": got request");
	          //System.exit(1);
	          rest.setContentType("text/html");
	                     
	          try {
	        	  rest.write (this.processRequest(parseURL(rest.resource,0), rest.payload, rest.header).toString());
	        	  rest.flush();
	          } catch (Exception e) {
	        	  System.out.println("RestConnector caught " + e);
	          }
	        }
	      }
	      catch (Exception ex) {
	        System.out.println(this + ": " + ex.getMessage());
	        //System.exit(1);
	      }
	}
	
	public HashMap<String, Targetable> getURLs() {
		HashMap<String,Targetable> urls = new HashMap<String,Targetable> ();
		
		// get the Scenes from the Project
		urls.putAll(pr.getTargetURLs());
		
		return urls;
	}
	
	public String parseURL (String url, int trim) {
		if (url==null) return "";
		String path = "";
		
		String[] splits = url.split("/");
    for (int i = 0 ; i < splits.length-trim ; i++) {
  	  path += splits[i] + (i<splits.length-(1+trim)?"/":"");
    }
    try {
			path = java.net.URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}
	
	public Object processRequest(String url, String payload, Map<String, String> header) {
		//if (targets==null) targets = getURLs();
		
		if ("/sendkey".equals(url)) {
			pr.println("RestConnector received sendkey '" + payload.charAt(0) + "'!");
			pr.sendKeyPressed(payload.charAt(0));
			header.put("Location","/interface");
			return "";
		}
		
		if (url.startsWith("/sendkey/")) {
			String keys = url.replace("/sendkey/", "");
			for (int i = 0 ; i < keys.length() ; i++) {
				pr.println(this.toString() + " sending " + keys.charAt(i));
				pr.sendKeyPressed(keys.charAt(i));
			}
			header.put("Location","/interface");
			return "<a href='/interface'>[back]</a> (send "+keys+")";
		}

		if ("/interface".equals(url)) {
			header.put("Content-Type", "text/html");
			
			return getInterfacePage();
		}
						
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
			return gson.toJson(getTargets().keySet().toArray());
		}
		
		//System.out.println("RestConnector: url is " + url + ", payload is " + payload + ", header is " + header);
		//System.exit(1);
		
		if (!header.containsKey("Content-Type") || header.get("Content-Type").contains("Text")) {
			// do nothing
		} else if (header.get("Content-Type").contains("JSON") && payload!=null && !payload.equals("")) {
			try { 
				payload = gson.fromJson(payload, RestMessage.class).value;			
			} catch (Exception e) {
				System.out.println("RESTCONNECTOR: caught exception " + e);
			}
		}
		
		Targetable t = getTargets().get(url);
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

	private String getInterfacePage() {
		String h = "<html>";
		h += "<h1><a href='/interface'>" + pr.toString()  + "</a></h1>";
	
		h += "<h2>Info</h2>";
		h += "<b>Current sequence:</b> " + pr.getSequenceName() + "<br>";
		h += pr.getSequencer().isLocked() ? "<b>Sequencer is locked</b> (ie sequencer preset won't change automatically)" : "Sequencer is not locked" + "<br>";
		h += !pr.isSequencerEnabled() ? "<b>Sequencer is disabled!</b> (ie paused)" : "Sequencer is enabled" + "<br>"; 
		
		h += "<hr>";
		
		h += "<h2>Exposed routes</h2>";
		//Iterator<Entry<String,Targetable>> tit = targets.entrySet().iterator();
		Iterator<Entry<String,Targetable>> tit = exposed.entrySet().iterator();
		if (!tit.hasNext()) h+= "<i>no routes exposed in this project</i>";
		while (tit.hasNext()) {
			Entry<String,Targetable> e = tit.next();
			//if (e.getKey() instanceof Sequence)
			String uri = e.getKey();
			Targetable target = e.getValue();
			if (uri.startsWith("/seq/changeTo")) {
				h += "<form action='" + uri + "' method='post'>";
				h += "<input type='submit' value='" + uri + "' />";
				h += "</form>";
				h += "<hr>";
			}
		}
		h += "<hr>";
		
		h += "<h2>send keys</h2>";
		String keys = ";-l/'";		///keys from project onkeypressed 
		for (char c : keys.toCharArray()) {
			try {
				h += "<a target='sendkeys' href='/sendkey/" + java.net.URLEncoder.encode(""+c,"UTF-8") +"'>" + "[ send "+c+" ]" + "</a>";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			h += " | ";
		}
		
		h += "<iframe name='sendkeys'>...sendkeys target</iframe>";
		
		h += "<hr>";
		
		h += "</html>";
		return h.toString();
	}
	
}