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
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.Filter;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.ChangeParameterSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.StreamChainSequence;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.ui.ControlFrame;

abstract public class Stream implements Serializable {
	boolean debug = false;
	public String streamName = "Unnamed";
	
	boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	Stream () {

	}
	public Stream (String streamName) {
		this.streamName = streamName;
	}
	// Message >- [targetId] -< Callback

	List<Callback> listeners = Collections.synchronizedList(new LinkedList<Callback>());
	// targetId -< Callback

	ConcurrentHashMap<String,Collection> messages = new ConcurrentHashMap<String, Collection>();
	// targetId -< Message

	/*public void finish () {
    this.listeners = null;//.dispose();
    this.messages = null;//.dispose();
  	}*/

	synchronized public void registerEventListener (String paramName, Callback callback) {
		//List dis = this.getOrAddParameter(paramName);  //this.dispatchers.get(paramName);
		//TODO - store callbacks with a copy of the object that they belong to so they can be deleted upon request. possibly store them in lists of the hash of the object...
		//or use a string to group them + mark groups for deletion during message processing

		System.out.println("in " + this + " - registerEventListener(" + paramName + ", " + callback + ")");
		List<Callback> dis = this.getListenerList(); //paramName);
		callback.setStreamSource(paramName);
		synchronized(dis) {
			dis.add(callback);
		}
		//dis = this.getListenerList(paramName);
		System.out.println("listsize is now " + dis.size());
		System.out.println("listener size is " + listeners.size());
	}

	synchronized public List<Callback> getListenerList() {
		/*List<ParameterCallback> l;
		if (!listeners.containsKey(paramName)) {
			System.out.println("getListenerList(" + paramName + ") adding new list because isn't set");
			l = new LinkedList<ParameterCallback>();
			this.listeners.put(paramName, l);
		}

		return this.listeners.get(paramName);*/
		synchronized(listeners) {
			return this.listeners;
		}
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
		if (!APP.getApp().isReady())
			return;
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
		if (this.isEnabled())
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
		//if (this instanceof OscStream) debug = true;
		//LinkedList<ParameterCallback> emitters = (LinkedList<ParameterCallback>) listeners.iterator(); //iterator();
		if (!this.isEnabled()) return;

		if (debug) System.out.println("--------------- deliverEvents() in " + this + "------------------------");
		if (debug) System.out.println("there are " + listeners.size() + " feeds .. ");
		

		////for each listener section "test" "sine" (A)  HashMap   - p
		////  for each listener callback (B) HashMap - b
		////    for each message (C) HashMap
		////       call B(C)
		//while (emitters.hasNext()) {
		
		for (Callback callback : getListenerList()) { //listeners) {
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
				//debug = true;
				//println("hello OscStream world with " + listeners.size() + " listeners");
				//println("got " + mess.size() + " messages for '" + tagName + "'");
				if (mess.size()==0) {
					for (Object k : mess.toArray()) { //messages.keySet()) {
						println("there are " + mess.size() + " messages for '" + k + "'");
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

					if (/*this instanceof MidiStream || */debug) 
						System.out.println("Delivering " + v + " to " + callback + "...");
					/*if (callback.shouldDie) {
							callbacks.remove();
							//((List)e_l.getValue()).remove(callback);
							//toDeleteList.add(callbacks);
						} else {*/
					try {
						this.preCall(callback);
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
				//mess.clear();
				

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

	abstract protected void preCall(Callback c);	// TODO Auto-generated method stub
	
	protected String getMessageNameForStreamSource(String streamSource) {
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
		
		params.put("enabled", this.isEnabled());

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

		for (Callback l : this.listeners) {
			if (!l.isTemporary()) {
				ParameterCallback pc = (ParameterCallback) l;
				callbacks.add((HashMap<String, Object>) pc.collectParameters());
			}
		}

		return callbacks;
	}

	public void readParameters(Map<String, Object> input) {
		this.streamName = (String) input.get("name");
		/*if (input.get("callbacks") instanceof HashMap) {
			Map<String, Map<String, Object>> callbacks = (Map<String, Map<String,Object>>) input.get("callbacks");
			for (Entry<String, Map<String, Object>> i : callbacks.entrySet()) {
				//this.registerEventListener(paramName, ParameterCallback.createParameterCallback(i.getValue().get("class")));
				for (Entry<String, Object> p : ((Map<String,Object>)i.getValue()).entrySet()) {
					this.registerEventListener(this.streamName + "/" + i.getKey(), ParameterCallback.makeParameterCallback((HashMap<String, Object>) p.getValue()));
				}
			}
		} else {*/
			// new List style
			if (input.containsKey("callbacks")) 
				for (Map<String, Object> params : (List<Map<String,Object>>)input.get("callbacks")) {
					this.registerEventListener((String)params.get("streamSource"), ParameterCallback.makeParameterCallback(params));
				}
			if (input.containsKey("enabled")) {
				this.setEnabled((Boolean) input.get("enabled"));
			}
		//}

		//callbacks = input.
	}
	public static Stream makeStream(Object payload) {
		// TODO Auto-generated method stub		
		System.out.println ("makeStream() " + payload);
		Map<String, Object> input = (Map<String,Object>)payload;

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
			System.err.println("Caught " + e + ": Didn't manage to instantiate " + classname + " might be missing constructor or didn't check for existence of key during readParameters?");
			e.printStackTrace();
		}

		//streamName = (String) input.get("name");
		//String paramName = (String) input.get("paramName");

		return null;
	}
	public void setupControls(final ControlFrame cf, Group g) {
		//cf.control().addScrollableList(this.streamName);
		synchronized (cf /*g*/) {
		int n = 0;
		int margin_y = 20, gap_y = 5, margin_x = 20;

		int pos_y = 10;

		final Stream self = this;

		// add 'on' button to enable/disable whole Stream
		g.add(new Toggle(cf.control(), this.toString() + "_enabled_"+n).setValue(this.isEnabled()).setLabel("on")
				.setColorActive(VurfEclipse.makeColour(0, 255, 0))
				.addListenerFor(Button.ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						synchronized(self) {
							//listeners.remove(callback);
							self.setEnabled(((Toggle)theEvent.getController()).getBooleanValue());
							
							//cf.updateGuiStreamEditor(); // this causes crash for some reason ?
						}
					}					
				})					
				.moveTo(g).setPosition(0, pos_y).setWidth(margin_x));
		
		g.add(cf.control().addButton(this.toString() + "_add").setLabel("ADD")
			.setPosition(margin_x*3, pos_y)
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
				.setPosition(margin_x * 8, pos_y)
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
		for (final Callback callback_t : this.listeners) { //i.getValue()) {
			if (callback_t.isTemporary()) continue;	// dont display 'temporary' callbacks like sequence hooks
			ParameterCallback callback = (ParameterCallback) callback_t;
			
			int pos_x = 5;

			println ("adding gui for " + callback);
			
			
			// add '[->]' button to copy mapping to sequence
			if (callback instanceof FormulaCallback) {
				FormulaCallback callback_f = (FormulaCallback) callback;
				g.add(new Button(cf.control(), this.toString() + "_" + callback_f + "_conv_"+n)
						.setColorBackground(VurfEclipse.makeColour(255, 0,0)).setLabel("->")
						.addListenerFor(Button.ACTION_BROADCAST, new CallbackListener() {
							@Override
							public void controlEvent(CallbackEvent theEvent) {
								synchronized(listeners) {
									//listeners.remove(callback);
									
									//cf.updateGuiStreamEditor(); // this causes crash for some reason ?
									ChainSequence current = (ChainSequence) ((SequenceSequencer)APP.getApp().pr.getSequencer()).getActiveSequence();
									//StreamChainSequence seq = Sequence.makeSequence(StreamChainSequence.class.getSimpleName(), current.getHost());
									StreamChainSequence seq = new StreamChainSequence();

									
									seq.setHost(current.getHost());
									ChangeParameterSequence cq = new ChangeParameterSequence();
									cq.setTargetPath(callback_f.targetPath);
									cq.setExpression(callback_f.getExpression());
									cq.setOutputMode(callback_f.getOutputMode());
									
									cq.setHost(current.getHost());
									seq.addSequence(cq);
									seq.activateEmitter(self.streamName, callback_f.getStreamSource());
									current.addSequence(seq);
									
									cf.queueUpdate(new Runnable() {
										@Override
										public void run() {								
											try {
												((SequenceSequencer) APP.getApp().pr.getSequencer()).getGrpSequenceEditor().refreshControls();										
											} catch (Exception e) {
												println("Caught exception trying to add a new filter " + e);
												e.printStackTrace();
											} 
										}});
									
								}
							}					
						})
						.moveTo(g).setPosition(0, pos_y).setWidth(margin_x/2));
			}
			
			// add '[x]' button to remove mapping
			g.add(new Button(cf.control(), this.toString() + "_" + callback + "_del_"+n)
					.setColorBackground(VurfEclipse.makeColour(255, 0,0)).setLabel("[x]")
					.addListenerFor(Button.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							synchronized(listeners) {
								listeners.remove(callback);
								
								cf.updateGuiStreamEditor(); // this causes crash for some reason ?
							}
						}					
					})
					.moveTo(g).setPosition(pos_x, pos_y).setWidth(margin_x));
					
			//.setColorForeground(VurfEclipse.makeColour(0, 64, 0));
			
			pos_x += margin_x * 1.5f;

			// add 'on' button to enable.disable
			g.add(new Toggle(cf.control(), this.toString() + "_" + callback + "_enabled_"+n).setValue(callback.isEnabled()).setLabel("on")
					.setColorActive(VurfEclipse.makeColour(0, 255, 0))
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
					.moveTo(g).setPosition(pos_x, pos_y).setWidth(margin_x));
					
			
			
			pos_x += margin_x * 1.5;
			
			
			g.add(this.makeEmitterSelector(cf, callback, callback.getStreamSource() + callback.toString() + "_" + n)
				.moveTo(g)
				.setPosition(pos_x, pos_y)
			);			
			
			pos_x += margin_x * 6;
			


			g.add(new Toggle(cf.control(), this.toString() + "_" + callback + "_latching_"+n).setValue(callback.isLatching()).setLabel("L")
					.setColorActive(VurfEclipse.makeColour(255, 255, 128))
					.addListenerFor(Button.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							synchronized(self) {
								//listeners.remove(callback);
								callback.setLatching(((Toggle)theEvent.getController()).getBooleanValue());
								//cf.updateGuiStreamEditor(); // this causes crash for some reason ?
							}
						}					
					})
					.moveTo(g).setPosition(pos_x, pos_y).setWidth(margin_x))
				//
			;
				//.setColorForeground(VurfEclipse.makeColour(128, 64, 0));
			pos_x += margin_x * 1.5f;
			

			g.add(callback.makeControls(cf, n + "_" + streamName).moveTo(g).setPosition(pos_x, pos_y));
			//g.add(callback.makeControls(cf, n + "_" + streamName).moveTo(g).setPosition(margin_x * 2.25f, pos_y));
			pos_x += margin_x * 1.5f;
		
			
			//margin_y += g.
			margin_y = g.getHeight() + gap_y;
			
			pos_y += margin_y + gap_y * 2;
			n++;
		}
		
		g.setBackgroundHeight(pos_y + margin_y);
		
		}
		//}
	}

	protected ParameterCallback makeCallback(String string) {
		return new FormulaCallback().setTargetPath("/sc/BlankerScene/fl/BlankFilter/pa/alpha").setExpression("input").setStreamSource(string);		
	}
	
	protected Group makeEmitterSelector(ControlFrame cf, final ParameterCallback callback, String name) {
		Group g = new Group(cf.control(), name + "_select_group").hideBar();

		synchronized(this) {			
			ScrollableList lstParam = cf.control().addScrollableList(name)
					//.setPosition(0, pos_y)
					//.setWidth(margin_x * 2)
					.setBarHeight(16).setItemHeight(16)
					.onLeave(cf.close)
					.onEnter(cf.toFront)
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
		}
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
	public boolean notifyRemoval(Targetable newf) {
		boolean relevant = false;

		synchronized (this.listeners) {
			for (Callback c : this.listeners) {
				boolean t = c.notifyRemoval(newf);
				if (t==true) 
					relevant = true;
			}
		}
		return relevant;
	}
	public void removeEventListener(Callback eventAdapter) {
		synchronized(this.listeners) {
			this.getListenerList().remove(eventAdapter);
		}
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

