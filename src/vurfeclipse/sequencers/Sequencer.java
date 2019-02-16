package vurfeclipse.sequencers;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ScrollableList;
import controlP5.Toggle;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.filters.Filter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.streams.Stream;
import vurfeclipse.ui.ControlFrame;

abstract public class Sequencer implements Serializable, Targetable, CallbackListener {
	public Project host;

	boolean forward = false;

	boolean outputDebug = true;

	public void println(String text) { // debugPrint, printDebug -- you get the
		// idea
		if (outputDebug)
			System.out.println("SQR "
					+ (text.contains((this.toString())) ? text : this + ": " + text));
	}

	int w, h;

	public int max_iterations = 2;

	protected double timeScale = 1.0d;

	protected Toggle tglLocked;
	protected Toggle tglEnabled;
	protected Toggle tglStreams;
	protected Toggle tglPlaylist;
	protected Toggle tglEnableSequenceControls;


	/////////// Event stuff
	//public abstract boolean initialise();
	boolean enableStreams = true;
	private boolean enableSequencer = true;
	private boolean historyMode = false;
	boolean locked = false;


	protected int ticks = 0; //for tracking sequencer position when paused
	int last = 0;
	
	private Map<String, Stream> streams = Collections.synchronizedMap(new HashMap<String,Stream>());

	public ControlFrame cf;

	private String selectedTargetPath;

	
	public void addStream(String streamName, Stream st) {
		//this.streams.put(streamName, st);
		synchronized (streams) {
			this.streams.put(streamName, st);
		}
	}
	public Stream getStream(String streamName) {
		synchronized (streams) {
			return (Stream) this.streams.get(streamName);
		}
	}
	public Stream getStreamByTitle(String stream_name) {
		for (Entry<String,Stream> e : getStreams().entrySet()) {
			if (e.getValue().streamName.equals(stream_name)) {
				return getStream(e.getKey());
			}
		}
		return null;
	}
	public Map<String, Stream> getStreams() {
		synchronized (streams) {
			return streams;
		}
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
		this.last = 0;
		this.enableSequencer = payload;
		this.updateGuiStatus();
		return enableSequencer;		
	}

	protected void updateGuiStatus() {
		APP.getApp().getCF().queueUpdate(new Runnable () {
			@Override
			public void run() {
				if (tglLocked!=null) tglLocked.changeValue(isLocked()?1.0f:0.0f);
				if (tglEnabled!=null) tglEnabled.changeValue(isSequencerEnabled()?1.0f:0.0f);
				if (tglStreams!=null) tglStreams.changeValue(isStreamsEnabled()?1.0f:0.0f);
				if (tglPlaylist!=null) tglPlaylist.changeValue(isHistoryMode()?1.0f:0.0f);
			}
		});
	}
			

	public void setForward() {
		this.forward = true;
	}

	abstract public boolean runSequences(int time);

	public HashMap<String, Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = new HashMap<String, Targetable>();

		urls.put("/seq/toggleLock", this);
		urls.put("/seq/forward", this);

		urls.put("/seq/changeTo", this);

		urls.put("/seq/timeScale", this);
		
		urls.put("/seq/time", this);

		return urls;
	}
	
	@Override public Object target(String path, Object payload) {
		if (path.equals("/seq/timeScale")) {
			this.setTimeScale(Double.parseDouble(payload.toString()));
			return payload;
		}
		
		if (path.equals("/seq/time")) {
			this.ticks = (int) Float.parseFloat(payload.toString()); //runSequences((Float)payload);
			runSequences(this.ticks);
		}
		
		String[] spl = path.split("/",4); // TODO: much better URL and parameter checking.
		if (spl.length>3) payload = spl[3];
		if (spl[2].equals("toggleLock")) {				// just gets status of lock...
			if (payload instanceof Boolean) {
				this.toggleLock((Boolean) payload);
			} else if (payload instanceof String) {
				this.toggleLock((Boolean)payload.equals("true"));
			}
			return "Lock is " + this.toggleLock();
		} else if (spl[2].equals("sequencer_enabled")) {
			this.toggleEnabled((Boolean)payload);
		} else if (spl[2].equals("streams_enabled")) {
			this.toggleStreams((Boolean)payload);
		} else if (spl[2].equals("stream_setup")) {
			//this.streams = (HashMap<String, Stream>) payload;
			for (Entry<String,Object> i : ((Map<String,Object>)payload).entrySet()) {
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
		if (key==';') { // || key=='f') {		// FORWARDS
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
		} else if (key=='!') {
			println("Panic mode - attempting reset of bits of GUI that might have gone wrong");
			for (ScrollableList c : APP.getApp().getCF().control().getAll(ScrollableList.class)) {
				c.open();
				println("opening and closing " + c);
				c.close();
			}
		} else if (this.sendKeyPressedToStreams(key)) {
			println("a stream dealt with keypress " + key  + "!");
		} else {
			return false;
		}
		return true;
	}

	private boolean sendKeyPressedToStreams(char key) {
		for (Stream s : this.getStreams().values()) {
			if (s.sendKeyPressed(key)) return true;
		}
		return false;
	}
	public void setupControls(ControlFrame cf, String tabName) {
		// TODO Auto-generated method stub
		//this.sequencer.setupControls(cf, tabname);
		this.cf = cf;

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
		//if (APP.getApp().isReady() && ((SequenceSequencer)this).getActiveSequence()!=null) ((SequenceSequencer)this).getActiveSequence().setValuesForTime(this.ticks);
		// TODO Auto-generated method stub
	}

	public boolean runStreams(int time) {
		if (enableStreams) {
			for (Map.Entry<String,Stream> e : this.getStreams().entrySet()) {
				//println("processStreams in " + this + " for " + e);
				Stream s = e.getValue();
				s.processEvents(time);
				s.deliverEvents();
			}
		}

		return true;
	}


	private HashMap<String,HashMap<String,Object>> collectStreamParameters() {
		HashMap<String,HashMap<String,Object>> params = new HashMap<String,HashMap<String,Object>>();
		for (Entry<String, Stream> s : this.getStreams().entrySet()) {
			params.put(s.getKey(), s.getValue().collectParameters());
		}
		return params;
	}
	public boolean isHistoryMode() {
		return historyMode;
	}
	public void setHistoryMode(boolean historyMode) {
		this.historyMode = historyMode;
		this.updateGuiStatus();
	}
	
	public boolean notifyRemoval(Targetable newf) {
		boolean relevant = false;
		for (Entry<String, Stream> s : getStreams().entrySet()) {
			boolean t = s.getValue().notifyRemoval(newf);
			if (t==true) relevant = true;
		}
		if (relevant) APP.getApp().getCF().updateGuiStreamEditor(); //this.updateGuiStreamEditor();
		
		return relevant;
	}
	public String getSelectedTargetPath() {
		return selectedTargetPath;
	}
	public void setSelectedTargetPath(String path) {
		println("Setting selectedTargetPath to '" + path + "'");
		this.selectedTargetPath = path;		
	}

}
