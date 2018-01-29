package vurfeclipse.streams;
//import java.util.List;
//import java.util.LinkedList;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textfield;
import processing.event.KeyEvent;
import vurfeclipse.APP;
import vurfeclipse.ui.ControlFrame;

public class Stream implements Serializable {
	boolean debug = false;
	String streamName = "Unnamed";

	Stream () {

	}
	public Stream (String streamName) {
		this.streamName = streamName;
	}
	// Message >- [targetId] -< Callback

	//ConcurrentHashMap<String, List<ParameterCallback>> listeners = new ConcurrentHashMap<String, List<ParameterCallback>>();
	LinkedList<ParameterCallback> listeners = new LinkedList<ParameterCallback>();
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
		boolean debug = false;
		//LinkedList<ParameterCallback> emitters = (LinkedList<ParameterCallback>) listeners.iterator(); //iterator();

		if (debug) System.out.println("deliverEvents() in " + this);
		if (debug) System.out.println("there are " + listeners.size() + " feeds .. ");

		////for each listener section "test" "sine" (A)  HashMap   - p
		////  for each listener callback (B) HashMap - b
		////    for each message (C) HashMap
		////       call B(C)
		//while (emitters.hasNext()) {
		for (ParameterCallback callback : listeners) {
			//Map.Entry<String,List<ParameterCallback>> e_l = (Map.Entry<String,List<ParameterCallback>>) callback; //emitters.next();
			String tagName = this.getMessageNameForStreamSource(callback.getStreamSource()); //(String)e_l.getKey();
			//if (debug) System.out.println("For listeners with " + e_l.getKey() + " got " + e_l.getValue());

			//debug = true;
			if (debug) println ("got callback " + callback + " and stream " + tagName); //got stream source to deliver to ")
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

					//Iterator<?> callbacks = ((List<?>)e_l.getValue()).iterator();
					//while (callbacks.hasNext()) {
					//Map.Entry e_b = (Map.Entry) b.next();
					//ParameterCallback callback = (ParameterCallback) callbacks.next();
					if (debug) System.out.println("got callback " + callback);

					if (debug) System.out.println("Delivering " + v + " to " + callback + "...");
					/*if (callback.shouldDie) {
							callbacks.remove();
							//((List)e_l.getValue()).remove(callback);
							//toDeleteList.add(callbacks);
						} else {*/
					try {
						callback.call(v);
					} catch (Exception e) {
						System.out.println("Stream " + this + " caught " + e.toString() + " while attempting to process callback for " + v + " on " + callback + "!");
						e.printStackTrace();
					}
					//}
					if (debug) System.out.println("Delivered " + v + " to " + callback + ".");

					//((List)messages.get(tagName)).remove(v);
					m.remove();
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
	}

	private String getMessageNameForStreamSource(String streamSource) {
		return streamSource.split("/")[1];
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

		CallbackListener toFront = new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				theEvent.getController().bringToFront();
				((ScrollableList)theEvent.getController()).open();
			}
		};

		CallbackListener close = new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				((ScrollableList)theEvent.getController()).close();
			}
		};



		//for ( Entry<String, List<ParameterCallback>> i : this.listeners.entrySet()) {
		for (ParameterCallback c : this.listeners) { //i.getValue()) {
			ScrollableList lstParam = cf.control().addScrollableList(c.getStreamSource() + c.toString() + "_" + n)
					.setPosition(0, pos_y)
					.setWidth(margin_x * 2)
					.setBarHeight(16).setItemHeight(16)
					;
			lstParam.moveTo(g).close().setLabel(c.getStreamSource()); //"source");
			lstParam.addItems(this.getEmitterNames());//addItem(i.getKey(), i.getKey())
			g.add(lstParam);

			println ("adding gui for " + c);
			if (c instanceof FormulaCallback) {

				CallbackListener setExpression = new CallbackListener() {
					public void controlEvent(CallbackEvent theEvent) {
						//((ScrollableList)theEvent.getController()).close();
						((FormulaCallback) c).setExpression(((Textfield)theEvent.getController()).getText());
						((Textfield)theEvent.getController()).setValueLabel(((FormulaCallback) c).getExpression());
					}
				};


				Textfield expression = cf.control().addTextfield(c.getStreamSource() + "_" + n + "_Expression_" + c.toString())
						.setText(((FormulaCallback)c).getExpression())
						.setPosition((int) margin_x * 2.25f, pos_y)
						.moveTo(g)
						.setLabel("Expression")
						.setAutoClear(false); 
				expression.addListenerFor(Textfield.ACTION_BROADCAST, setExpression);

				g.add(expression);

				final FormulaCallback fc = (FormulaCallback) c; 

				ScrollableList lstTarget = cf.control().addScrollableList(c.getStreamSource() + "_" + n + "_Target URL")
						//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
						.setLabel(((FormulaCallback)c).targetPath)
						.addItems(APP.getApp().pr.getTargetURLs().keySet().toArray(new String[0]))
						.setPosition(margin_x * 5, pos_y)
						.setWidth((cf.sketchWidth()/3))
						.moveTo(g)
						.onLeave(close)
						.onEnter(toFront)
						.setBarHeight(expression.getHeight()).setItemHeight(expression.getHeight())
						.close();

				//lstTarget.setValue(targetPath);

				lstTarget.addListenerFor(ScrollableList.ACTION_CLICK, new CallbackListener () {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						// TODO Auto-generated method stub
						Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)lstTarget.getValue());
						//s.entrySet();
						((FormulaCallback) fc).setTargetPath((String) s.get("text"));
					}				
				});

				g.add(lstTarget);
			}	
			pos_y += margin_y + gap_y;
			n++;
		}
		//}
	}

	private void println(String string) {
		System.out.println("Stream " + this + ": " + string);		
	}
	public String[] getEmitterNames() {	
		return null;
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

