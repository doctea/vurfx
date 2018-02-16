package vurfeclipse.streams;
//import java.util.List;
//import java.util.LinkedList;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Toggle;
import vurfeclipse.ui.ControlFrame;

abstract public class Stream implements Serializable {
	boolean debug = false;
	String streamName = "Unnamed";

	Stream () {

	}
	public Stream (String streamName) {
		this.streamName = streamName;
	}
	// Message >- [targetId] -< Callback

	List<ParameterCallback> listeners = Collections.synchronizedList(new LinkedList<ParameterCallback>());
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
		List<ParameterCallback> dis = this.getListenerList(); //paramName);
		callback.setStreamSource(paramName);
		dis.add(callback);
		//dis = this.getListenerList(paramName);
		System.out.println("listsize is now " + dis.size());
		System.out.println("listener size is " + listeners.size());
	}

	synchronized public List<ParameterCallback> getListenerList() {
		/*List<ParameterCallback> l;
		if (!listeners.containsKey(paramName)) {
			System.out.println("getListenerList(" + paramName + ") adding new list because isn't set");
			l = new LinkedList<ParameterCallback>();
			this.listeners.put(paramName, l);
		}

		return this.listeners.get(paramName);*/
		return this.listeners;
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
		if (debug) 
			System.out.println("add event to " + paramName + "'" + value + "'");
		
		this.getMessagesList(paramName).add(value);

		if (debug) 
			System.out.println("events for " + paramName + " now " + this.getMessagesList(paramName));

	}

	synchronized public void clearEvents(String paramName) {
		this.getMessagesList(paramName).clear();
	}

	public void processEvents (int time) {
		// get new parameter values... add them to the list and then call their dispatcher
		processEventMeat(time);
	}

	abstract public void processEventMeat(int time);
	/*public void processEventMeat(int time) {
		if (debug) System.out.println("EventProcessor processEventMeat(" + time + ")");
		addEvent("test", new Integer(time));
		//messages.get("test").put(
		//new Integer(random(255))
		//);
	}*/

	synchronized public void deliverEvents () {
		boolean debug = this.debug = false;
		//LinkedList<ParameterCallback> emitters = (LinkedList<ParameterCallback>) listeners.iterator(); //iterator();

		if (debug) System.out.println("--------------- deliverEvents() in " + this + "------------------------");
		if (debug) System.out.println("there are " + listeners.size() + " feeds .. ");
		

		////for each listener section "test" "sine" (A)  HashMap   - p
		////  for each listener callback (B) HashMap - b
		////    for each message (C) HashMap
		////       call B(C)
		//while (emitters.hasNext()) {
		
		for (ParameterCallback callback : listeners) {
			//Map.Entry<String,List<ParameterCallback>> e_l = (Map.Entry<String,List<ParameterCallback>>) callback; //emitters.next();
			if (debug) { println ("--");
				println("stream source is '" + callback.getStreamSource() + "'");
			}
			String tagName = this.getMessageNameForStreamSource(callback.getStreamSource()); //(String)e_l.getKey();
			//if (debug) System.out.println("For listeners with " + e_l.getKey() + " got " + e_l.getValue());

			//debug = false;
			if (debug) 
				//println ("got callback listener " + callback + " for stream tagname '" + tagName +"'"); //got stream source to deliver to ")
				println ("stream tagname '" + tagName +"'"); //got stream source to deliver to ")
			//List sub_l = e_l.getValue();

			//ArrayList<ParameterCallback> toDeleteList = new ArrayList<ParameterCallback> ();

			/// now loop over all the messages
			//List mess = (List)messages.get(tagName);
							
			List<Object> mess = getMessagesList(tagName);
			

			if (debug && this instanceof OscStream) {
				println("hello OscStream world with " + listeners.size() + " listeners");
				println("got " + mess.size() + " messages for '" + tagName + "'");
				if (mess.size()==0) {
					for (String k : messages.keySet()) {
						println("there are messages for '" + k + "'");
					}
				}
			}
			
			if (debug)
				println("got " + mess.size() + " messages for " + tagName);

			if (mess!=null) {
				Iterator<Object> m = mess.iterator();
				while (m.hasNext()) {
					//Map.Entry in = (Map.Entry)m.next();
					//Object v = in.getValue();
					Object v = m.next();

					//Iterator<?> callbacks = ((List<?>)e_l.getValue()).iterator();
					//while (callbacks.hasNext()) {
					//Map.Entry e_b = (Map.Entry) b.next();
					//ParameterCallback callback = (ParameterCallback) callbacks.next();
					//if (debug) System.out.println("got callback " + callback);

					if (debug) System.out.println("Delivering " + v + " to " + callback + "...");
					/*if (callback.shouldDie) {
							callbacks.remove();
							//((List)e_l.getValue()).remove(callback);
							//toDeleteList.add(callbacks);
						} else {*/
					try {
						callback.__call(v);
					} catch (Exception e) {
						System.out.println("Stream " + this + " caught " + e.toString() + " while attempting to process callback for " + v + " on " + callback + "!");
						e.printStackTrace();
					}
					//}
					if (debug) System.out.println("Delivered " + v + " to " + callback + ".");

					//((List)messages.get(tagName)).remove(v);
				}
				if (debug) System.out.println("Removing delivered messages?");
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

				//}
			} else {
				System.out.println ("Could't find tagName " + tagName + "?!");
			}
		}

		this.messages.clear();
	}

	private String getMessageNameForStreamSource(String streamSource) {
		//println("!!! streamSource is " + streamSource);
		if (streamSource.split("/").length>1)
			return streamSource.split("/")[1];
		else 
			return streamSource;
	}
	public HashMap<String, Object> collectParameters() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("class",  this.getClass().getName());
		params.put("name", this.streamName);

		params.put("callbacks", this.collectLinkParameters());

		return params;
	}

	public LinkedList<HashMap<String,Object>> collectLinkParameters() {
		LinkedList<HashMap<String,Object>> callbacks = new LinkedList<HashMap<String,Object>> ();
		/*int index = 0;
		for (ParameterCallback l : this.listeners) {
			//callbacks.put(l.getKey(), l.getValue());
			HashMap<String,Object> links = new HashMap<String,Object> ();
			//for (ParameterCallback pc : l.getValue()) {
				links.put(l.getStreamSource() + "_" + (index++), l.collectParameters());
			//}
			callbacks.put(l.getStreamSource(), links);
		}*/

		for (ParameterCallback l : this.listeners) {
			callbacks.add(l.collectParameters());
		}

		return callbacks;
	}

	public void readParameters(HashMap<String, Object> input) {
		this.streamName = (String) input.get("name");
		if (input.get("callbacks") instanceof HashMap) {
			HashMap<String, HashMap<String,Object>> callbacks = (HashMap<String, HashMap<String,Object>>) input.get("callbacks");
			for (Entry<String, HashMap<String, Object>> i : callbacks.entrySet()) {
				//this.registerEventListener(paramName, ParameterCallback.createParameterCallback(i.getValue().get("class")));
				for (Entry<String, Object> p : ((HashMap<String,Object>)i.getValue()).entrySet()) {
					this.registerEventListener(this.streamName + "/" + i.getKey(), ParameterCallback.makeParameterCallback((HashMap<String, Object>) p.getValue()));
				}
			}
		} else {
			// new List style
			for (HashMap<String, Object> params : (LinkedList<HashMap<String,Object>>)input.get("callbacks")) {
				this.registerEventListener((String)params.get("streamSource"), ParameterCallback.makeParameterCallback(params));
			}
		}

		//callbacks = input.
	}
	public static Stream makeStream(Object payload) {
		// TODO Auto-generated method stub		
		System.out.println ("makeStream() " + payload);
		HashMap<String,Object> input = (HashMap<String,Object>)payload;

		String classname = (String) input.get("class");
		try {
			Class<?> clazz = Class.forName(classname);
			//System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			Constructor<?> ctor = clazz.getConstructor(); //Scene.class,Integer.TYPE);
			Stream stream = (Stream) ctor.newInstance(); //(Scene)null, 0);
			stream.readParameters(input);
			return stream;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e + ": Didn't manage to instantiate " + classname + " might be missing constructor?");
			e.printStackTrace();
		}

		//streamName = (String) input.get("name");
		//String paramName = (String) input.get("paramName");

		return null;
	}
	synchronized public void setupControls(ControlFrame cf, Group g) {
		//cf.control().addScrollableList(this.streamName);

		int n = 0;
		int margin_y = 20, gap_y = 5, margin_x = 80;

		int pos_y = 10;


		final Stream self = this;

		g.add(cf.control().addButton(this.toString() + "_add").setLabel("ADD")
			.setPosition(margin_x, pos_y)
			.moveTo(g)
			.addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
				@Override
				public void controlEvent(CallbackEvent theEvent) {
					// add a new callback 
					String emitterName = self.getEmitterNames()==null?"":self.getEmitterNames()[0];
					ParameterCallback callback = makeCallback(emitterName);					
					self.registerEventListener(emitterName, callback);
					
					// and refresh gui
					cf.updateGuiStreamEditor();
				}
			})
		);

		
		g.add(cf.control().addButton(this.toString() + "_refresh").setLabel("REFRESH")
				.setPosition(margin_x * 2, pos_y)
				.moveTo(g)
				.addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						// and refresh gui
						cf.updateGuiStreamEditor();
					}
				})
			);

		pos_y += margin_y + gap_y;
		
		
		//for ( Entry<String, List<ParameterCallback>> i : this.listeners.entrySet()) {
		for (ParameterCallback callback : this.listeners) { //i.getValue()) {
			println ("adding gui for " + callback);
			
			g.add(this.makeEmitterSelector(cf, callback, callback.getStreamSource() + callback.toString() + "_" + n)
				.moveTo(g)
				.setPosition(0, pos_y)
			);			
			
			// add 'on' button to enable.disable
			g.add(new Toggle(cf.control(), callback + "_enabled_"+n).setValue(callback.isEnabled()).setLabel("on")
					.addListenerFor(Button.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							synchronized(self) {
								//listeners.remove(callback);
								callback.setEnabled(((Toggle)theEvent.getController()).getBooleanValue());
								
								//cf.updateGuiStreamEditor(); // this causes crash for some reason ?
							}
						}					
					})
					.moveTo(g).setPosition(margin_x*1.5f, pos_y).setWidth(margin_x/4));
			
			g.add(callback.makeControls(cf, n + "_" + streamName).moveTo(g).setPosition(margin_x * 2.25f, pos_y));
			
			
			// add '[x]' button to remove mapping
			g.add(new Button(cf.control(), callback + "_del_"+n).setLabel("[x]")
					.addListenerFor(Button.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							synchronized(self) {
								listeners.remove(callback);
								
								cf.updateGuiStreamEditor(); // this causes crash for some reason ?
							}
						}					
					})
					.moveTo(g).setPosition(margin_x*2f, pos_y).setWidth(margin_x/4));
			
			g.add(callback.makeControls(cf, n + "_" + streamName).moveTo(g).setPosition(margin_x * 2.25f, pos_y));
									
			
			//margin_y += g.
			margin_y = g.getHeight() + gap_y;
			
			pos_y += margin_y + gap_y * 2;
			n++;
		}
		
		g.setBackgroundHeight(pos_y + margin_y);
		
		//}
	}

	protected ParameterCallback makeCallback(String string) {
		return new FormulaCallback().setTargetPath("/sc/BlankerScene/fl/BlankFilter/pa/alpha").setExpression("input").setStreamSource(string);		
	}
	
	protected Group makeEmitterSelector(ControlFrame cf, ParameterCallback callback, String name) {
		
		Group g = new Group(cf.control(), name + "_select_group").hideBar();
		
		ScrollableList lstParam = cf.control().addScrollableList(name)
				//.setPosition(0, pos_y)
				//.setWidth(margin_x * 2)
				.setBarHeight(16).setItemHeight(16)
				;
		lstParam
			//.moveTo(g)
			.close().setLabel(callback.getStreamSource()); //"source");
		lstParam.addItems(this.getEmitterNames());//addItem(i.getKey(), i.getKey())
		
		//g.add(lstParam);
		
		lstParam.addListenerFor(lstParam.ACTION_BROADCAST, new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
				// should set new stream source on the callback
				// also tell the Sequencer to regenerate UI
				Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)theEvent.getController().getValue());
				callback.setStreamSource((String) s.get("text"));
				//s.entrySet();
				//((FormulaCallback) fc).setTargetPath((String) s.get("text"));
			}				
		});
		
		g.add(lstParam.moveTo(g));
		return g;

		//return lstParam;
	}
	protected void println(String string) {
		System.out.println("Stream " + this + ": " + string);		
	}
	public String[] getEmitterNames() {	
		return null;
	}
	public boolean sendKeyPressed(char key) {
		// TODO Auto-generated method stub
		return false;
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

