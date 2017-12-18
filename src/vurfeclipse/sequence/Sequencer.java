package vurfeclipse.sequence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import controlP5.Bang;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ListBox;
import controlP5.ScrollableList;
import controlP5.Tab;
import controlP5.Textfield;
import controlP5.Toggle;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.streams.Stream;
import vurfeclipse.ui.ControlFrame;

abstract public class Sequencer implements Serializable, Targetable, CallbackListener {
	public Project host;

	boolean locked = false;
	boolean forward = false;

	boolean outputDebug = true;

	public void println(String text) { // debugPrint, printDebug -- you get the
		// idea
		if (outputDebug)
			System.out.println("SQR "
					+ (text.contains((this.toString())) ? text : this + ": " + text));
	}

	int w, h;

	public int max_iterations;

	protected double timeScale = 1.0d;

	protected Toggle tglLocked;
	protected Toggle tglEnabled;
	protected Toggle tglStreams;

	private boolean enableSequencer = true;

	/////////// Event stuff
	//public abstract boolean initialise();
	boolean enableStreams = true;
	
	private HashMap<String,Stream> streams = new HashMap<String,Stream>();
	
	public void addStream(String streamName, Stream st) {
		//this.streams.put(streamName, st);
		this.streams.put(streamName, st);
	}
	public Stream getStream(String streamName) {
		return (Stream) this.streams.get(streamName);
	}

	public void toggleStreams() {
		this.toggleStreams(!this.isStreamsEnabled());
	}
	public void toggleStreams(boolean b) {
		this.enableStreams = b;
		this.updateGuiStatus();
	}
	public boolean isStreamsEnabled() {
		return this.enableStreams;
	}
	/////////////// end Event stuff

	
	public boolean isSequencerEnabled() {
		return this.enableSequencer;
	}

	public boolean readyToChange(int max_iterations) {
		this.max_iterations = max_iterations;
		if (forward) {
			forward = false;
			return true;
		}
		if (locked)
			return false;
		return checkReady(max_iterations);
	}

	public boolean checkReady(int max_iterations) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean toggleLock() {
		this.toggleLock(!this.locked);
		return locked;
	}
	public boolean toggleLock(boolean payload) {
		this.locked = payload;
		// gui: update lock status
		this.updateGuiStatus();
		return locked;
	}
	
	public boolean toggleEnabled() {
		return this.toggleEnabled(!this.isSequencerEnabled());
	}
	public boolean toggleEnabled(boolean payload) {
		this.enableSequencer = payload;
		this.updateGuiStatus();
		return enableSequencer;		
	}

	protected void updateGuiStatus() {
		if (this.tglLocked!=null) this.tglLocked.changeValue(this.isLocked()?1.0f:0.0f);
		if (this.tglEnabled!=null) this.tglEnabled.changeValue(this.isSequencerEnabled()?1.0f:0.0f);
		if (this.tglStreams!=null) this.tglStreams.changeValue(this.isStreamsEnabled()?1.0f:0.0f);
	}

	public void setForward() {
		this.forward = true;
	}

	abstract public boolean runSequences();

	public HashMap<String, Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = new HashMap<String, Targetable>();

		urls.put("/seq/toggleLock", this);
		urls.put("/seq/forward", this);

		urls.put("/seq/changeTo", this);

		urls.put("/seq/timeScale", this);

		return urls;
	}
	
	@Override public Object target(String path, Object payload) {
		if (path.equals("/seq/timeScale")) {
			this.setTimeScale((Double)payload);
			return payload;
		}
		
		String[] spl = path.split("/",4); // TODO: much better URL and parameter checking.
		if (spl[2].equals("toggleLock")) {				// just gets status of lock...
			if (spl.length>3) payload = spl[3];
			if (payload instanceof Boolean) {
				this.toggleLock((boolean) payload);
			} else if (payload instanceof String) {
				this.toggleLock((boolean)payload.equals("true"));
			}
			return "Lock is " + this.toggleLock();
		} else if (spl[2].equals("stream_setup")) {
			//this.streams = (HashMap<String, Stream>) payload;
			for (Entry<String,Object> i : ((HashMap<String,Object>)payload).entrySet()) {
				this.addStream(i.getKey(), Stream.makeStream(i.getValue()));
			}
		}
		return null;
	}

	abstract public String getCurrentSequenceName();

	public boolean isLocked() {
		return this.locked;
	}

	public boolean sendKeyPressed(char key) {
		if (key==';' || key=='f') {		// FORWARDS
			setForward();
		} else if (key=='l') {
			println("toggling sequencer lock " + toggleLock());
		} else if (key=='\'') {
			println("toggled enableSequencer to " + this.toggleEnabled());
		} else if (key=='q') {
			setTimeScale(getTimeScale()+0.01d);
		} else if (key=='a') {
			setTimeScale(getTimeScale()-0.01d);
		} else if (key=='m') {
			this.toggleStreams();
			println("toggled enableStreams to " + this.enableStreams);
		} else {
			return false;
		}
		return true;
	}

	public void setupControls(ControlFrame cf, String tabName) {
		// TODO Auto-generated method stub
		//this.sequencer.setupControls(cf, tabname);


	}

	public void controlEvent (CallbackEvent ev) {
		//println("controlevent in " + this);
		/*if (ev.getAction()==ControlP5.ACTION_RELEASED) {
	      if (ev.getController()==this.saveHistoryButton) {

	      }
	      else if (ev.getController()==this.saveButton) {
	        println("save preset " + getSceneName());
	        //this.savePreset(saveFilenameController.getText(), getSerializedMap());
	        this.savePreset(getSceneName());
	      }
	      else if (ev.getController()==this.loadButton) {
	        println("load preset");
	        this.loadPreset2(getSceneName()); //saveFilenameController.getText());
	      }
	    }*/
	}

	public HashMap<String, Object> collectParameters() {
		// TODO Auto-generated method stub
		HashMap<String, Object> params = new HashMap<String, Object>();
		//params.put("/seq/changeTo", this.getCurrentSequenceName());
		params.put("/seq/timeScale", this.getTimeScale());
		
		params.put("/seq/stream_setup", this.collectStreamParameters());
				
		return params;
	}

	public double getTimeScale() {
		return timeScale; //1.0d;
	}

	public void setTimeScale(double f) {
		//println("setTimeScale(" + f + ")");;
		/*if (f>2.0d) {
			println ("setting timescale to " + f + "!");
		}*/
		timeScale = f;
		((SequenceSequencer)this).updateGuiTimeScale(f);
		if (APP.getApp().isReady() && ((SequenceSequencer)this).getActiveSequence()!=null) ((SequenceSequencer)this).getActiveSequence().setValuesForTime();
		// TODO Auto-generated method stub
	}

	public boolean runStreams(int time) {
		if (enableStreams) {
			Iterator<?> i = streams.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry e = (Map.Entry) i.next();
				//println("processStreams in " + this + " for " + e);
				Stream s = (Stream) e.getValue();
				s.processEvents(time);
				s.deliverEvents();
			}
		}

		return true;
	}


	private HashMap<String,HashMap<String,Object>> collectStreamParameters() {
		HashMap<String,HashMap<String,Object>> params = new HashMap<String,HashMap<String,Object>>();
		for (Entry<String, Stream> s : this.streams.entrySet()) {
			params.put(s.getKey(), s.getValue().collectParameters());
		}
		return params;
	}

	
}
