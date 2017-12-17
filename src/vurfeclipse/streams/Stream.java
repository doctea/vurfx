package vurfeclipse.streams;
//import java.util.List;
//import java.util.LinkedList;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class Stream implements Serializable {
	boolean debug = false;
	String streamName = "Unnamed";

	Stream () {

	}
	public Stream (String streamName) {
		this.streamName = streamName;
	}
	// Message >- [targetId] -< Callback

	ConcurrentHashMap<String, List<ParameterCallback>> listeners = new ConcurrentHashMap<String, List<ParameterCallback>>();
	// targetId -< Callback

	ConcurrentHashMap<String,Collection> messages = new ConcurrentHashMap<String, Collection>();
	// targetId -< Message

	/*public void finish () {
    this.listeners = null;//.dispose();
    this.messages = null;//.dispose();
  }*/

	synchronized public void registerEventListener (String paramName, ParameterCallback callback) {
		//List dis = this.getOrAddParameter(paramName);  //this.dispatchers.get(paramName);
		//TODO - store callbacks with a copy of the object that they belong to so they can be deleted upon request. possibly store them in lists of the hash of the object...
		//or use a string to group them + mark groups for deletion during message processing

		System.out.println("in " + this + " - registerEventListener(" + paramName + ", " + callback + ")");
		List<ParameterCallback> dis = this.getListenerList(paramName);
		dis.add(callback);
		dis = this.getListenerList(paramName);
		System.out.println("listsize is now " + dis.size());
		System.out.println("listener size is " + listeners.size());
	}

	synchronized public List<ParameterCallback> getListenerList(String paramName) {
		List<ParameterCallback> l;
		if (!listeners.containsKey(paramName)) {
			System.out.println("getListenerList(" + paramName + ") adding new list because isn't set");
			l = new LinkedList<ParameterCallback>();
			this.listeners.put(paramName, l);
		}

		return this.listeners.get(paramName);
	}
	synchronized public List<Object> getMessagesList(String paramName) {
		List<?> l;
		if (!messages.containsKey(paramName)) {
			l = new LinkedList<Object>();
			this.messages.put(paramName, l);
		}
		return (List<Object>) this.messages.get(paramName);
	}

	synchronized public void addEvent(String paramName, Object value) {
		if (debug) System.out.println("add event to " + paramName + "'" + value + "'");
		this.getMessagesList(paramName).add(value);

		if (debug) System.out.println("events for " + paramName + " now " + this.getMessagesList(paramName));

	}

	synchronized public void clearEvents(String paramName) {
		this.getMessagesList(paramName).clear();
	}

	public void processEvents (int time) {
		// get new parameter values... add them to the list and then call their dispatcher
		processEventMeat(time);
	}

	public void processEventMeat(int time) {
		if (debug) System.out.println("EventProcessor processEventMeat(" + time + ")");
		addEvent("test", new Integer(time));
		/*messages.get("test").put(
      new Integer(random(255))
    );*/
	}

	synchronized public void deliverEvents () {
		Iterator<Entry<String, List<ParameterCallback>>> p = listeners.entrySet().iterator();

		if (debug) System.out.println("deliverEvents() in " + this);
		if (debug) System.out.println("there are " + listeners.size() + " feeds .. ");

		////for each listener section "test" "sine" (A)  HashMap   - p
		////  for each listener callback (B) HashMap - b
		////    for each message (C) HashMap
		////       call B(C)
		while (p.hasNext()) {
			Map.Entry<String,List<ParameterCallback>> e_l = (Map.Entry<String,List<ParameterCallback>>) p.next();
			String tagName = (String)e_l.getKey();
			if (debug) System.out.println("For listeners with " + e_l.getKey() + " got " + e_l.getValue());

			//List sub_l = e_l.getValue();

			//ArrayList<ParameterCallback> toDeleteList = new ArrayList<ParameterCallback> ();

			/// now loop over all the messages
			//List mess = (List)messages.get(tagName);
			List<Object> mess = getMessagesList(tagName);
			if (mess!=null) {
				Iterator<Object> m = mess.iterator();
				while (m.hasNext()) {
					//Map.Entry in = (Map.Entry)m.next();
					//Object v = in.getValue();
					Object v = m.next();

					Iterator<?> callbacks = ((List<?>)e_l.getValue()).iterator();
					while (callbacks.hasNext()) {
						//Map.Entry e_b = (Map.Entry) b.next();
						ParameterCallback callback = (ParameterCallback) callbacks.next();
						if (debug) System.out.println("got callback " + callback);

						if (debug) System.out.println("Delivering " + v + " to " + callback + "...");
						if (callback.shouldDie) {
							callbacks.remove();
							//((List)e_l.getValue()).remove(callback);
							//toDeleteList.add(callbacks);
						} else {
							try {
								callback.call(v);
							} catch (Exception e) {
								System.out.println("Stream " + this + " caught " + e.toString() + " while attempting to process callback!");
							}
						}
						if (debug) System.out.println("Delivered " + v + " to " + callback + ".");

						//((List)messages.get(tagName)).remove(v);
					}
					if (debug) System.out.println("Removing delivered messages?");
					m.remove();
					//mess.remove(v);

					//System.out.println("--got " + e_b.getKey() + " : " + e_b.getValue());

					/*Iterator m = ((List)messages.get(e_b.getKey())).iterator();
          while (m.hasNext()) {
            Map.Entry in = (Map.Entry)m.next();
            Object v = in.getValue();
            System.out.println("Delivering " + v + " to " + ec);
            ec.call(v);
            ((List)messages.get(me.getKey())).remove(v);
          }*/

				}
			} else {
				System.out.println ("Could't find tagName " + tagName + "?!");
			}
		}
	}
	
	public HashMap<String, Object> collectParameters() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("class",  this.getClass().getName());
		params.put("name", this.streamName);
		
		HashMap<String, Object> callbacks = new HashMap<String, Object> ();
		for (Entry<String,List<ParameterCallback>> l : this.listeners.entrySet()) {
			callbacks.put(l.getKey(), l.getValue());
		}
		params.put("callbacks", callbacks);
		
		return params;
	}


	/*
    while (p.hasNext()) {
      Map.Entry me = (Map.Entry)p.next();

      List list = (List)me.getValue();
      Iterator i = list.iterator();
      while (i.hasNext()) {
        Map.Entry me2 = (Map.Entry)i.next();
        ParameterCallback ec = (ParameterCallback) me2.getValue(); //p.next();

        System.out.println("got ParameterCallback " + ec);

        Iterator m = ((List)messages.get(me.getKey())).iterator();
        while (m.hasNext()) {
          Map.Entry in = (Map.Entry)m.next();
          Object v = in.getValue();
          System.out.println("Delivering " + v + " to " + ec);
          ec.call(v);
          ((List)messages.get(me.getKey())).remove(v);
        }
      }
    }
  }  */

}

