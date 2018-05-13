package vurfeclipse.sequencers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.connectors.XMLSerializer;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.projects.Project;
import vurfeclipse.projects.SavedProject;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.*;
import vurfeclipse.streams.Stream;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.ui.SequenceEditor;
import vurfeclipse.ui.StreamEditor;
import controlP5.Accordion;
import controlP5.Bang;
import controlP5.CallbackEvent;
import controlP5.ControlP5;
import controlP5.ControlP5Base;
import controlP5.Controller;
import controlP5.Group;
import controlP5.ListBox;
import controlP5.Numberbox;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textfield;
import controlP5.Toggle;

public class SequenceSequencer extends Sequencer implements Targetable {
	String activeSequenceName = "";
	HashMap<String,Sequence> sequences = new HashMap<String,Sequence>();
	ArrayList<String> randomPool = new ArrayList<String>();

	//HashMap<String,ArrayList<Sequence>> switched_sequences = new HashMap<String,ArrayList<Sequence>>();
	// list of Sequences that are applicable for each SequenceName

	int seq_pos = 0;
	ArrayList<String> seqList = new ArrayList<String>();

	boolean stopSequencesFlag = true;

	private boolean bindToRandom = true;
	private boolean randomMode = true;
	private List<String> historySequenceNames = Collections.synchronizedList(new LinkedList<String>());
	private int historyCursor;
	private SequenceEditor grpSequenceEditor;


	// oneshot functionality
	Sequence oneshot = null;
	int oneshotStart;
	private boolean debug;

	public boolean startOneShot(String sequenceName) {
		this.oneshot = (Sequence) this.getSequence(sequenceName).clone();
		this.oneshot.setLengthMillis(25); //this.oneshot.getLengthMillis()/4);
		this.oneshot.start();	// reset to initial parameters but don't reset time
		//this.oneshotStart = APP.getApp().timeMillis;
		return this.oneshot != null;
	}

	public boolean processOneShot() {
		//this.oneshot = null;
		if (this.oneshot==null) return false;
		if (this.oneshot.readyToChange(1)) {
			println("oneshot is ready to change!");
			this.oneshot.stop();
			this.oneshot = null;
			
			// cheekily restart the current sequence in an attempt to 'carry on'
			this.getActiveSequence().restart();
			
			return false;
		}

		//println("processOneShot processing " + this.oneshot);
		// calculate time
		/*int elapsed = APP.getApp().timeMillis - this.oneshotStart;
		println ("position is " + elapsed);
		double pc = 1.0d / ((double)oneshot.getLengthMillis() / (double)elapsed);
		println ("oneshot got pc " + pc);
		this.oneshot.setValuesForNorm(pc);
		this.oneshot.setValuesForNorm(this.oneshot.getPCForElapsed(elapsed));*/
		this.oneshot.setValuesForTime(APP.getApp().timeMillis - oneshotStart);

		return true;
	}


	public SequenceSequencer (Project host, int w, int h) {
		//super(host, w,h);
		this.host = host;
		this.w = w;
		this.h = h;

		//this.filterCount = 16;
		//this.filters = new Filter[filterCount];
	}


	public void saveHistory() throws IOException {
		this.saveHistory("history.txt");
	}
	public void saveHistory(String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this.historySequenceNames);
		/*for (String seqname : this.historySequenceNames) {
				oos.writeObject(seqname);
				oos.writeChars("\r\n");
			}*/
		oos.close();
		println("Saved sequencer history to " + fileName + "!");
	}

	public void loadHistory()throws IOException {
		// TODO Auto-generated method stub
		loadHistory("history.txt");
	}
	public void loadHistory(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		try {
			this.historySequenceNames = (LinkedList<String>) ois.readObject();
			println("Loaded sequencer history from " + fileName + "!");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ois.close();
	}


	@Override
	public boolean checkReady(int max_iterations) {
		if (getActiveSequence()!=null && getActiveSequence().getLengthMillis()<100) {
			host.println("SequenceSequencer is " + this.toString() + ": " + getActiveSequence() + " has " + getActiveSequence().getLengthMillis() + " length..?");
		}
		return getActiveSequence()==null || getActiveSequence().readyToChange(max_iterations);
	}

	/*@Override
	  public boolean readyToChange(int max_iterations) {
		  //if (getActiveSequence()==null || getActiveSequence().getLengthMillis()==0) return true;
		  return super.readyToChange(max_iterations);
	  }*/

	/*@Override
	synchronized public boolean runSequences(int time) {
		
		return runSequences(this.ticks);
	}*/
	
	@Override
	synchronized public boolean runSequences(int time) {
		if (!this.isSequencerEnabled()) return false;
		
		if (last==0) last = time; //APP.getApp().timeMillis;
		
		this.ticks += /*APP.getApp().timeMillis*/ time - last;
		
		last = time; //APP.getApp().timeMillis;
		
		return this._runSequences(this.ticks);
	}




	private void _runSequences(Float castAs) {
		if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			println(this+"#runSequences(): is readyToChange from " + this.activeSequenceName + ", calling randomSequence()");
			nextSequence();
		}
		
		if (getActiveSequence()==null) nextSequence();
		
		if (getActiveSequence()!=null) 
			getActiveSequence().setValuesForNorm(castAs);

		//gui : update current progress
		if (getActiveSequence()!=null) this.updateGuiProgress(getActiveSequence());
		
		//this.getActiveSequence().setValuesForNorm(castAs);		
	}
	
	synchronized public boolean _runSequences(int time) {
		
		if (debug) 
			println("runSequences time is " + time);
		if (!APP.getApp().isReady()) return false;

		if (stopSequencesFlag) {
			for (Sequence seq : sequences.values()) {
				if (seq!=null) seq.stop();
			}
			stopSequencesFlag = false;
		}

		this.processOneShot();

		//println(this+"#runSequences");
		// probably want to move this up to Sequencer and do super.runSequences()
		if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			println(this+"#runSequences(): is readyToChange from " + this.activeSequenceName + ", calling randomSequence()");
			nextSequence();
		}
		if (getActiveSequence()==null) nextSequence();

		//host.setTimeScale(0.1f);

		if (getActiveSequence()!=null) 
			getActiveSequence().setValuesForTime(time);

		//gui : update current progress
		if (getActiveSequence()!=null) this.updateGuiProgress(getActiveSequence());

		return getActiveSequence()!=null;
	}

	public Sequence getActiveSequence () {
		Sequence ret = sequences.get(activeSequenceName);
		return ret;
	}


	public Sequence getSequence(String name) {
		// TODO Auto-generated method stub
		return sequences.get(name);
	}

	@Override
	public HashMap<String, Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = super.getTargetURLs(); //new HashMap<String,Targetable>();
		//urls.putAll(super.getTargetURLs());

		urls.put("/seq/globalTime", this);
		urls.put("/seq/seqTime", this);
		
		urls.put("/seq/next", this);
		
		urls.put("/seq/seed", this);
		urls.put("/seq/changeTo", this);
		urls.put("/seq/sequence", this);

		urls.put("/seq/bank/sequences", this);
		urls.put("/seq/bank/history", this);

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		/*while (it.hasNext()) {
			Entry<String, Sequence> e = it.next();
			//int count = 0;

			// todo: move the path generation into Sequence
			urls.put("/seq/changeTo/" + e.getKey(), this);
			urls.put("/seq/sequence/" + e.getKey(), this);

			// this loop processes each Sequence for the current Sequence; dont think we need to add a URL for each of em!
			//Iterator<Sequence> sit = e.getValue().iterator();
				//   while (sit.hasNext()) {
					//Sequence seq = sit.next();f
					//urls.put("/seq/" + e.getKey() + "/" + count, seq);
				//}
		}*/

		return urls;
	}


	@Override
	public Object target(String path, Object payload) {
		//println(this + "#target('"+path+"', '"+payload+"')");

		String[] spl = path.split("/",4); // TODO: much better URL and parameter checking.
		if (spl[2].equals("oneshot")) {
			if (spl.length>3) 									// if given a named sequence as either the last query portion 
				payload = spl[3];
			if (payload instanceof String) {
				this.startOneShot((String)payload);
			} else if (payload instanceof Integer || payload instanceof Float) {
				this.startOneShot((String) ((Entry) this.sequences.entrySet().toArray()[(Integer)payload]).getKey());
			}
		} else if (spl[2].equals("globalTime")) {
			if (spl.length>3) 									// if given a named sequence as either the last query portion 
				payload = spl[3];
			//this.ticks = (Integer)Parameter.castAs(payload, Integer.class);//.toString());
			this._runSequences((Integer)Parameter.castAs(payload, Integer.class));
		} else if (spl[2].equals("seqTime")) {
			this._runSequences((Float)Parameter.castAs(payload,  Double.class));
		} else if (spl[2].equals("changeTo")) {	   
			if (spl.length>3) 									// if given a named sequence as either the last query portion 
				payload = spl[3];
			if (payload instanceof String) {					// if got a string payload then switch to the sequence named that
				println ("Sequencer attempting changeSequence to " + payload.toString());
				changeSequence(payload.toString(), true, true); // 2017-12-09 removed quick hack ; 2017-11-16, quick hack to prevent targeted changes from saving in history..
			} else if (payload instanceof HashMap<?,?>) {		// if given a hashmap, load the hashmap like its a partial saved snapshot and switch to it
				println ("Sequencer attempting changeSequence to passed-in definition of a sequence!");
				Sequence newSeq = this.createSequence((HashMap<String,Object>)payload);
				String seqName = (String) ((HashMap) payload).get("current_sequence_name");
				seqName = seqName==null?"loaded":seqName;
				this.addSequence(seqName, newSeq);
				changeSequence(seqName);
			}
			return "Sequencer active Sequence is currently " + activeSequenceName;
		} else if (spl[2].equals("next")) {
			if (payload!=null) {
				if ((Float)Parameter.castAs(payload, Float.class)>=1.0f) {
					this.nextSequence();
				}
			}
		} else if (spl[2].equals("bank")) {	//  /bank/sequences
			//println ("loading bank for " + spl[3]);
			if ((spl[3].equals("sequences") || spl[3].equals("history") ) && (payload instanceof HashMap<?,?> || (payload instanceof LinkedHashMap<?,?>))) {		
				for (Entry<String,Object> s : ((HashMap<String,Object>) payload).entrySet()) {
					HashMap<String,Object> c = (HashMap<String,Object>)s.getValue();
					Sequence newSeq = this.createSequence(c);
					String seqName = (String) c.get("current_sequence_name");
					println("got seqName " + seqName);
					seqName = seqName==null?s.getKey():seqName;
					this.addSequence(seqName, newSeq);
					if (spl[3].equals("history")) 
						this.addHistorySequenceName(seqName);
				}
				this.updateGuiHistory();
			} else {
				if (spl[3].equals("sequences")) 
					return this.collectBankSequences();
				else
					return this.collectBankHistory();
			}
		} else if (spl[2].equals("sequence")) {					// creates a sequence and adds it to the bank, but doesn't switch to it. returns url to change to the sequence
			if (payload instanceof HashMap<?,?>) {		
				Sequence newSeq = this.createSequence((HashMap<String,Object>)payload);
				String seqName = (String) ((HashMap) payload).get("current_sequence_name");
				seqName = seqName==null?"loaded":seqName;
				this.addSequence(seqName, newSeq);
				return "/seq/changeTo/"+seqName;
			} else {
				return this.collectParameters();	// strictly this should be done in a target that explicitly means 'current sequence' 
			}
		} else if (spl[2].equals("seed")) {
			this.getActiveSequence().setSeed((Long)payload);
			this.getActiveSequence().println("Set seed to "+payload);
			return payload;			
		} else {
			return (super.target(path,  payload));			
		}
		return payload;
	}



	public Sequence addListSequence(String sequenceName) {
		if (this.sequences.containsKey(sequenceName))
			this.seqList.add(sequenceName);
		return this.sequences.get(sequenceName);
	}


	public Sequence addListSequence(String sequenceName, Sequence sc) {
		this.seqList.add(sequenceName);
		return addSequence(sequenceName, sc);
	}

	synchronized public Sequence addSequence(String sequenceName, Sequence sc) {
		sequences.put(sequenceName, sc);

		if (activeSequenceName.equals("")) {
			//activeSequenceName = sequenceName;
			this.changeSequence(sequenceName);
		}

		if (isBindToRandom()==true) 
			this.randomPool.add(sequenceName);

		//host.addSequence(sc);
		//this.sequences.put(SequenceName, sc);

		return sc;
	}

	public void bindAll(HashMap<String,Sequence> seqs) {
		this.bindAll(seqs, 1);
	}
	public void bindAll(HashMap<String,Sequence> seqs, int weight) {
		for (int i = 0 ; i<weight ; i++) {
			sequences.putAll(seqs);
			if (isBindToRandom()==true) this.randomPool.addAll(seqs.keySet());
		}
	}
	public synchronized void bindAll(String prefix, HashMap<String,Sequence> seqs, int weight) {
		Iterator<String> it = seqs.keySet().iterator();
		HashMap<String,Sequence> newSeqs = new HashMap<String,Sequence> ();
		while (it.hasNext()) {
			String c = it.next();
			newSeqs.put(prefix+"_"+c, seqs.get(c));
			//seqs.remove(c);
		}
		bindAll(newSeqs, weight);
	}

	public Sequence bindSequence(String nameInSequencer, Sequence seq, int weight) {
		for (int i = 0 ; i < weight ; i++) {
			this.sequences.put(nameInSequencer+"_"+i, seq);
			if (isBindToRandom()==true) this.randomPool.add(nameInSequencer+"_"+i);
		}
		return seq;
	}
	public Sequence bindSequence(String nameInSequencer, Scene sc, String presetSequenceName, int weight) {
		return bindSequence(nameInSequencer, sc.getSequence(presetSequenceName), weight);
	}
	public Sequence bindSequence(String nameInSequencer, Sequence seq) {
		return this.bindSequence(nameInSequencer, seq, 1);
	}


	public Sequence bindSequence(String nameInSequencer, Scene sc, String seqName) {
		// TODO Auto-generated method stub
		return this.bindSequence(nameInSequencer,sc,seqName,1);
	}

	/*public Sequence bindSequence(String switchedSequenceName, String sequenceName, Scene sc) {
		  //addSequence(switchedSequenceName,Sequence);
		  Sequence seq = sc.getSequence(sequenceName);
		  if (seq!=null)
			  bindSequence(switchedSequenceName, sce.getSequence(sequenceName));
		  else
			  throw new NullPointerException("sequenceName " + sequenceName + " passed a null value for Sequence " + switchedSequenceName + " and " + Sequence);

		  return Sequence;
	  }

		public Sequence bindSequence(String name, Sequence sc) {	// bind it and all sequences
			addSequence(name,sc);

			Iterator<Entry<String, Sequence>> it = sc.getSequences().entrySet().iterator();
			while(it.hasNext()) {
				bindSequence(name, it.next().getValue());
			}

			return sc;
		}
	 */

	public void randomSequence() {
		if (randomPool.size()==0) {
			println("No randomPool is empty!");
			return;
		}

		int count = randomPool.size();
		int chosen = 0;
		//try {
		chosen = (int)APP.getApp().random(0,count);
		if (chosen<seqList.size()) seq_pos = chosen; // set list index
		if (chosen<0) chosen = 0;
		//changeSequence((String)sequences.keySet().toArray()[chosen]);
		println("Chose random element " + chosen + " of " + count + "('" + (String)randomPool.get(chosen) + "')");
		
		Sequence newseq = this.getSequence((String)randomPool.toArray()[chosen]);
		newseq = (Sequence)newseq.clone();
		String newname = "r:" + (String)randomPool.toArray()[chosen];
		this.addSequence(newname, newseq);
		
		changeSequence(newname); //(String)randomPool.toArray()[chosen]);
		/*} catch (Exception e) {
		  	this.println("randomSequence() with chosen " + chosen + " (of count " + count + ") caught " + e);
		  }*/
	}

	public void nextRandomSequence() {	// version of randomSequence() that honours the isLocked() status
		if (!this.isLocked()) {
			this.randomSequence();
		}
	}


	public void nextSequence() {
		if (this.isHistoryMode()) {
			this.histNextSequence(1, true);
		} else if (randomMode) {
			randomSequence();
		} else {
			println("Moving to seqList index " + seq_pos++);
			if (seq_pos>=seqList.size()) {
				seq_pos = 0;
				println("Resetting index to 0 because sequence list reached " + seqList.size());
			}
			String newSequenceName = seqList.get(seq_pos); 
			changeSequence(newSequenceName);
		}
	}

	/*public void histPreviousSequence(int distance) {
	  	this.histPreviousSequence(distance, true);
	  }*/


	private void histMoveCursorAbsolute(int value, boolean restart) {
		// TODO Auto-generated method stub
		println ("got move distance " + (value - historyCursor));
		this.histMoveCursor(value - historyCursor, restart);
	}
	public void histMoveCursor(int distance, boolean restart) {
		int size = this.historySequenceNames.size();	  	
		int oldCursor = historyCursor;

		historyCursor+=distance;

		if (historyCursor<0) {
			historyCursor = 0;
			host.println("SequenceSequencer already at start of history");
		} else if (historyCursor>size-1) {
			historyCursor = size - 1;

			host.println("SequenceSequencer already at end of history");
		} else {
			if (this.historySequenceNames.get(historyCursor)!=null) {

				host.println("SequenceSequencer moving history cursor to " + historyCursor + " with restart of " + restart);
				String previousSequenceName = this.historySequenceNames.get(historyCursor);

				changeSequence(previousSequenceName, false, restart);

				// GUI: update display
				this.updateGuiSequenceChanged(oldCursor, historyCursor);

				//if (size>1) this.historySequenceNames.remove(size-1);
			}
		}
	}

	public void histPreviousSequence(int distance, boolean restart) {
		this.histMoveCursor(-distance, restart);
	}
	public void histNextSequence(int distance, boolean restart) {
		this.histMoveCursor(distance, restart);
	}

	public void changeSequence(String sequenceName) {
		this.changeSequence(sequenceName,true,true);
	}

	synchronized public void changeSequence(String sequenceName, boolean remember, boolean restart) {
		if (this.getActiveSequence()!=null) {		// mute the current sequence 
			if (restart) this.getActiveSequence().stop();//setMuted(true);
			// check if this is already the top of the sequence history, if so don't add it again 
		}

		String oldSequenceName = this.activeSequenceName;
		this.activeSequenceName = sequenceName;

		if (null==this.getActiveSequence()) {
			println("Got NULL for " + this.activeSequenceName + "!");
			return;
		} else {
			this.getActiveSequence().iteration = 0;
		}

		int oldCursor = historyCursor;

		println("Changing to sequence: " + sequenceName + "  (" + this.getActiveSequence().toString() + ")");
		if (remember && this.shouldRemember(sequenceName)) {
			this.addHistorySequenceName(sequenceName);
			//if (historyCursor==this.historySequenceNames.size()-1)	// if the cursor is already tracking the history then set cursor to most recent item so that 'j' does jump to the most recent sequence

			historyCursor = this.historySequenceNames.size()-1;

			// GUI: add latest sequence to history GUI
			this.updateGuiAddHistory(oldSequenceName, sequenceName);
		}

		// update gui for changed sequences
		this.updateGuiSequenceChanged(oldCursor, historyCursor);

		//muteAllSequences();
		//this.getActiveSequence().setMuted(false);	/// WTF IS THIS ..? 
		if (restart) this.getActiveSequence().start();
	}


	private void updateGuiAddHistory(final String oldSequenceName, final String newSequenceName) {
		SequenceSequencer self = this;

		this.updateGuiHistory();

		if (APP.getApp().isReady()) APP.getApp().getCF().queueUpdate(new Runnable () {
			@Override
			public void run() {
				/*if (self.getActiveSequence()!=null && APP.getApp().isReady() && self.lstHistory!=null) 
					self.lstHistory.addItem(self.getCurrentSequenceName(), self.getCurrentSequenceName());
				 */

				// sync lstHistory with actual history
				//lstHistory.clear();
				//lstHistory.addItems(historySequenceNames);

				if (lstHistory.getItems().contains(oldSequenceName)) lstHistory.getItem(oldSequenceName).put("state", false);
				lstHistory.getItem(newSequenceName).put("state", true);
			}
		});
	}

	private void updateGuiHistory() {
		if (APP.getApp().isReady()) APP.getApp().getCF().queueUpdate(new Runnable () {
			@Override
			public void run() {
				/*if (self.getActiveSequence()!=null && APP.getApp().isReady() && self.lstHistory!=null) 
					self.lstHistory.addItem(self.getCurrentSequenceName(), self.getCurrentSequenceName());
				 */

				// sync lstHistory with actual history
				lstHistory.clear();
				lstHistory.addItems(historySequenceNames);
			}
		});
	}


	private void updateGuiSequence() {

		SequenceSequencer self = this;

		if (APP.getApp().isReady()) APP.getApp().getCF().queueUpdate(new Runnable () {
			@Override
			public void run() {
				//self.setupControls(cf, tabName);
				grpSequenceEditor.setSequence(getCurrentSequenceName(), getActiveSequence());	// in case above doesn't set things up properly
			}
		});
		
	}

	private void updateGuiSequenceChanged(final int oldCursor, final int newCursor) {
		//if (!APP.getApp().isReady()) return;
		if (null==lstHistory) return;
		if (lstHistory.getItems()==null) return;
		//if (lstHistory.getItems().size()==0) return;

		SequenceSequencer self = this;

		if (APP.getApp().isReady()) APP.getApp().getCF().queueUpdate(new Runnable () {
			@Override
			public void run() {
				
				txtSeed.setText(""+getActiveSequence().getSeed());
				
				if (oldCursor>=0 && oldCursor<lstHistory.getItems().size() && oldCursor!=newCursor) 
					lstHistory.getItem(oldCursor).put("state", false);

				if (newCursor>=0 && newCursor<lstHistory.getItems().size()) 
					lstHistory.getItem(newCursor).put("state", true);

				if (!getCurrentSequenceName().equals("")) 
					txtCurrentSequenceName.setValue(getCurrentSequenceName());

				if (!getCurrentSequenceName().equals("")) {
					/*this.grpSequenceEditor.remove();
					this.grpSequenceEditor = (SequenceEditor) 
					this.getActiveSequence()
						.makeControls(APP.getApp().getCF().control(), getCurrentSequenceName())
							.setSequence(this.getCurrentSequenceName(), getActiveSequence())
							.moveTo(this.grpSequenceEditor)
							.setPosition(0,20)
					;*/
					grpSequenceEditor.setSequence(getCurrentSequenceName(), getActiveSequence());
				}
			}
		});
	}

	private void updateGuiProgress(final Sequence activeSequence) {
		if (!APP.getApp().isReady()) return;
		if (null==activeSequence) return;
		if (null==this.sldProgress) return;

		SequenceSequencer self = this;
		
		synchronized(this) {
			if (APP.getApp().isReady()) APP.getApp().getCF().queueUpdate(new Runnable () {
				@Override
				public void run() {
					sldProgress.changeValue(activeSequence.getPositionPC()*100);
					sldProgress.setLabel("Progress iteration ["+(activeSequence.getPositionIteration()+1)+"/"+max_iterations+"]");
				}
			});
		}
	}

	public void updateGuiTimeScale(final double f) {
		if (!APP.getApp().isReady()) return;
		if (null==this.sldTimeScale) return;

		SequenceSequencer self = this;

		if (APP.getApp().isReady()) APP.getApp().getCF().queueUpdate(new Runnable () {
			@Override
			public void run() {
				sldTimeScale.changeValue((float) f);
			}
		});
	}


	synchronized private boolean shouldRemember(String sequenceName) {
		// TODO Auto-generated method stub
		if (sequenceName.contains("_next_")) {
			return false;
		}
		if (getSequence(sequenceName).readyToChange(0)) {
			host.println(this.toString() + ": " + sequenceName +" is ready to change for 0, not remembering!");
		}
		return true;
	}


	synchronized private void addHistorySequenceName(String activeSequenceName2) {
		SequenceSequencer self = this;

		this.historySequenceNames.add(activeSequenceName2);

		//this.update

		if (APP.getApp().isReady()) {
			APP.getApp().getCF().queueUpdate(new Runnable () {
				@Override
				public void run() {
					//println("Added " + activeSequenceName2 + " as the " + historySequenceNames.size() + "th history item");
					//historySequenceNames.add(activeSequenceName2);
					// sync ?
				}
			});
		}
	}


	@Override
	public String getCurrentSequenceName() {
		return activeSequenceName;
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Scene sceneForPath, int length) {
		this.bindAndPermute(newPrefix, matchPrefix, sceneForPath, length, 1);
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Scene sceneForPath, int length, int weight) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Sequence> ent = it.next();
			if (ent.getKey().startsWith(matchPrefix)) { //matches
				Iterator<Entry<String, Sequence>> sit = sceneForPath.getSequences().entrySet().iterator();
				while (sit.hasNext()) {
					Entry<String, Sequence> s = sit.next();
					toAdd.put(
							newPrefix + "_" + s.getKey() + "_" + ent.getKey()  + "_PERMUTED",
							new ChainSequence(length).addSequence(s.getValue()).addSequence(ent.getValue())
							);
					println(newPrefix + "_" + s.getKey() + "_" + ent.getKey() + "_PERMUTED");
				}
			}
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd, weight);
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Sequence sequence, int length) {
		this.bindAndPermute(newPrefix, matchPrefix, sequence, length, 1);
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Sequence sequence, int length, int weight) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Sequence> ent = it.next();
			if (ent.getKey().startsWith(matchPrefix)) { //matches
				/*Iterator<Entry<String, Sequence>> sit = sceneForPath.getSequences().entrySet().iterator();
				while (sit.hasNext()) {
					Entry<String, Sequence> s = sit.next();*/
				toAdd.put(
						newPrefix + "_" + ent.getKey() + "_" + sequence + "_PERMUTED",
						new ChainSequence(length).addSequence(sequence).addSequence(ent.getValue())
						);
				println(newPrefix + "_" + ent.getKey() + "_" + sequence + "_PERMUTED");
				//}
			}
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd, weight);
	}


	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, String matchPrefix2, int length) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Sequence> ent = it.next();
			if (ent.getKey().startsWith(matchPrefix)) { //matches
				Iterator<Entry<String, Sequence>> sit = sequences.entrySet().iterator();
				while (sit.hasNext()) {
					Entry<String, Sequence> s = sit.next();
					if (s.getKey().startsWith(matchPrefix2)) {
						toAdd.put(
								newPrefix + "_" + ent.getKey() + "_" + s.getKey() + "_PERMUTED",
								new ChainSequence(length)
								.addSequence(s.getValue())
								.addSequence(ent.getValue())
								);
						println(newPrefix + "_" + ent.getKey() + "_" + s.getKey() + "_PERMUTED");
					}
				}
			}
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd);
	}


	public void bindAndPermute(String newPrefix, Sequence seq, Scene scene, int length) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> sit = scene.getSequences().entrySet().iterator();
		while (sit.hasNext()) {
			Entry<String, Sequence> s = sit.next();
			toAdd.put(
					newPrefix + "_" +  s.getKey() + "_PERMUTED",
					new ChainSequence(length).addSequence(s.getValue()).addSequence(seq)
					);
			println(newPrefix + "_" + s.getKey() + "_PERMUTED");
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd);
	}


	public void bindSequences(String prefix, Scene scene) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> sit = scene.getSequences().entrySet().iterator();
		while (sit.hasNext()) {
			Entry<String, Sequence> s = sit.next();
			toAdd.put(
					prefix + "_" +  s.getKey(),
					s.getValue()
					//new ChainSequence(length).addSequence(s.getValue()).addSequence(seq)
					);
			//println(prefix + "_" + s.getKey() + "_PERMUTED");
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd);
	}

	public void bindSavedSequences(String prefix, int sequenceLength, int weight) {
		List<String> textFiles = new ArrayList<String>();
		String directory = System.getProperty("user.dir") + "/saves"; //APP.getApp().sketchPath("");
		File dir = new File(directory);

		HashMap<String, Sequence> sequences = new HashMap<String, Sequence>();

		for (File file : dir.listFiles()) {
			if (file.getName().startsWith(host.getClass().getSimpleName()) && file.getName().endsWith((".xml"))) {
				println("bindSavedSequences() got " + file.getName());
				//textFiles.add(file.getName());
				HashMap<String, Object> input = this.host.readSnapshotFile("saves/"+file.getName()).get("/seq");//.get("/seq/sequence");
				HashMap<String,Object> sequence_settings = (HashMap<String,Object>)(input.containsKey("/seq/sequence") ? input.get("/seq/sequence") : input.get("/seq/changeTo"));
				Sequence newSeq = this.createSequence(sequence_settings);

				((HashMap<String, Sequence>) sequences).put("_saved " + file.getName(), newSeq);
			}
		}
		//return textFiles;

		//for (String filename : textFiles) {
		//for (int i = 0 ; i < weight ; i++) {
		this.bindAll(sequences, weight / dir.list().length); 	// weight the weight by how many presets we loaded
		//}
		//}
	}



	public int getSequenceCount() {
		return sequences.size();
	}


	public boolean isBindToRandom() {
		return bindToRandom;
	}


	public void setBindToRandom(boolean bindToRandom) {
		this.bindToRandom = bindToRandom;
	}


	public void setRandomMode(boolean b) {
		// TODO Auto-generated method stub
		this.randomMode = b;
		this.seqList = this.randomPool;
	}


	public void restartSequence() {
		println("restarting" + activeSequenceName);
		this.getActiveSequence().iteration = 0;
		this.getActiveSequence().start();
	}


	int sequenceDistance = 1;

	public void cutSequence() {
		// go up and down cursor alternately; if nothing to switch to, do a random mahfucker?
		if (sequenceDistance>0) {
			this.histNextSequence(sequenceDistance,false);
		} else {
			this.histPreviousSequence(-sequenceDistance,false);
		}

		if (sequenceDistance == 1) sequenceDistance = -1; else if (sequenceDistance == -1) sequenceDistance = 1; 
	}


	/*
	   		//
	  public ArrayList<Sequence> getAllSequences() {
		  ArrayList<Sequence> seqs = new ArrayList<Sequence> ();
		  Iterator<ArrayList<Sequence>> it = this.switched_sequences.values().iterator();
		  while (it.hasNext()) {
			  seqs.addAll(it.next());
		  }
		  return seqs;
	  }
	 * public void changeSequence(Sequence seq) {

	  }*/

	@Override 
	public boolean sendKeyPressed(char key) {
		//println("wtf?");
		if (key=='e') {
			try {
				loadHistory();
			} catch (IOException e) {
				System.out.println("Couldn't load history! " + e);
			}
		} else if (key=='j' || key=='J') {	// HISTORY BACK
			histPreviousSequence(1,key=='j'?true:false);
		} else if (key=='k' || key=='K') { // HISTORY FORWARD
			histNextSequence(1,key=='k'?true:false);
		} else if (key=='O' ) { // RESTART CURRENT SEQUENCE (stutter effect)
			restartSequence();
		} else if (key=='o') { // HISTORY 'cut' between cursor/next (or do random if at end?)
			cutSequence();
		} else if (key=='B') { // dump entire current sequencer bank to separate .xml files
			this.saveBankSequences(this.host.getClass().getSimpleName());
		} else if (key=='p') {
			this.togglePlaylist(!this.isHistoryMode());
		} else if (key=='d') {
			this.preserveCurrentSceneParameters();
		} else if (key=='v') {
			//this.preserveCurrentSceneParameters();
			HashMap<String, HashMap<String, Object>> current_parameters = this.host.collectSceneParameters();
			Sequence seq = this.cloneSequence(this.activeSequenceName, "Copy of " + this.activeSequenceName);
			seq.setSceneParameters(current_parameters);
			seq.start();
		} else if (key=='U') {
			println("starting oneshot!");
			//this.startOneShot((String)this.sequences.keySet().toArray()[0]);
			this.target("/seq/oneshot", (String)this.sequences.keySet().toArray()[0]);
		} else if(key=='1') {
			APP.getApp().getCF().control().getWindow().activateTab("Sequencer");
		} else if (key=='2') {
			APP.getApp().getCF().control().getWindow().activateTab("Stream Editor");
		} else if (key=='3') {
			APP.getApp().getCF().control().getWindow().activateTab("Scenes");
		} else if (key=='4') {
			APP.getApp().getCF().control().getWindow().activateTab("Monitor"); 
		} else if (super.sendKeyPressed(key)) {
			return true;
		} else {
			return false;
		}
		return true;
		
		// crap
		  			/*    } else if (key=='f') {
    	saveSequence(this.getCurrentSequenceName() + ((VurfEclipse)APP.getApp()).dateStamp());
    } else if (key=='F') {
    	//loadSequence(host.getApp().select.selectInput("Load a sequence", activeSequenceName));
    	loadSequence("test.xml");*/

	}

	@Deprecated
	private Sequence loadSequence(String filename) {
		HashMap<String, Object> input;

		//input ((VurfEclipse)APP.getApp()).io.deserialize(filename, HashMap.class);
		input = this.readSequenceFile(filename);

		Sequence newSeq = this.createSequence(input);
		//this.addSequence(filename, newSeq); //Sequence.createSequence((String) input.get("class")));
		//this.changeSequence(filename);

		return newSeq;
	}


	private HashMap<String, Object> readSequenceFile(String filename) {
		// TODO Auto-generated method stub
		try {
			return (HashMap<String, Object>) XMLSerializer.read(filename);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e1 + " trying to load sequence '" + filename + "'");
			e1.printStackTrace();
			return null;
			//return this;
		}
	}


	public Sequence createSequence(HashMap<String, Object> hashMap) {
		try {
			/*if (hashMap.containsKey("/seq")) { // have been passed the whole Snapshot 
  				hashMap = (HashMap<String,Object>) hashMap.get("/seq"); // so just grab the /seq part for processing here
  			}*/
			Scene host = this.host.getSceneForPath((String) hashMap.get("hostPath"));
			if (host==null) {
				System.err.println("Caught null host in createSequence() looking for " + hashMap.get("hostPath") + "!");
			}
			Sequence newSeq = Sequence.makeSequence((String) hashMap.get("class"), host);
			newSeq.loadParameters(hashMap);

			return newSeq;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e + " trying to load sequence from input hash");
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	private void saveSequence(String filename) {
		if (!filename.endsWith(".xml")) filename += ".xml";
		filename = filename.replace(':', '_');

		Sequence toSave = this.getActiveSequence();
		HashMap<String,Object> output; //= new HashMap<String,Object>();
		output = toSave.collectParameters();
		try {
			XMLSerializer.write(output, filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e.toString() + " trying to save sequence of class " + toSave.getClass().getSimpleName() + " to '" + filename + "'");
			e.printStackTrace();
		}
	}


	public String getProjectName() {
		return this.txtProjectName.getText();
	}

	protected Bang saveHistoryButton;
	protected Bang loadHistoryButton;

	private Textfield txtCurrentSequenceName;
	private Textfield txtSeed;
	private ListBox lstHistory;
	private Slider sldProgress;
	private Slider sldTimeScale;
	private StreamEditor grpStreamEditor;
	private Textfield txtProjectName;
	private String tabName;
	
	@Override public void setupControls (ControlFrame cf, String tabName) {
		super.setupControls(cf, tabName);
		
		this.tabName = tabName;

		println("Project#setupControls about to grab cp5 before scene loop..");
		final ControlP5 cp5 = cf.control();

		//this.setupMonitor(cp5);

		int c = 0;

		int margin_y = 20; // start under the tab row
		int margin_x = 5;

		int width = cf.sketchWidth();
		int height = cf.sketchHeight();

		Tab sequencerTab = cp5.addTab(tabName);

		txtCurrentSequenceName = new Textfield(cp5, "Current Sequence Name")
				.setPosition(margin_x, margin_y)
				//.setWidth(width/4)
				.moveTo(sequencerTab)
				.setAutoClear(false);
		sequencerTab.add(txtCurrentSequenceName);
		
		txtSeed = new Textfield(cp5, "Seed")
				.setPosition(margin_x + txtCurrentSequenceName.getWidth() + margin_x, margin_y)
				.setAutoClear(false)
				//.setWidth(width/3)
				.moveTo(sequencerTab);

		lstHistory = new controlP5.ListBox(cp5, "sequence names")  	    		
				.setPosition(width-(width/3), margin_y + 100)
				.setSize(width/3, height-margin_y-100)
				.setItemHeight(20)
				.setBarHeight(15)
				.moveTo(sequencerTab)
				.setItems(this.historySequenceNames)
				.setType(ListBox.LIST);

		sldProgress = new controlP5.Slider(cp5, "progress")
				.setPosition(margin_x, margin_y * 3)
				.setWidth(width/3)
				.setHeight(margin_y*2)
				.moveTo(sequencerTab)
				.setValue(this.getActiveSequence()!=null ? this.getActiveSequence().getPositionPC() : 0.0f);

		sldTimeScale = new controlP5.Slider(cp5, "timescale")
				.setPosition(margin_x, margin_y * 5)
				.setWidth(width/3)
				.setHeight(margin_y*2)
				.setRange(-4.0f/*0.000000001f*/, 4.0f)
				.moveTo(sequencerTab)
				.setValue((float) this.getTimeScale());

		tglLocked = new controlP5.Toggle(cp5, "locked")
				.setColorActive(VurfEclipse.makeColour(255, 0, 0))
				.setPosition(2*margin_x + (width/3), margin_y)
				.moveTo(sequencerTab)
				.setValue(this.isLocked()?1.0f:0.0f);

		tglEnabled = new controlP5.Toggle(cp5, "seq")
				//.setPosition(tglLocked.getWidth() + (margin_x*2) + (width/3), margin_y)
				.setPosition(tglLocked.getPosition()[0] + tglLocked.getWidth() + margin_x, margin_y)
				.changeValue(this.isSequencerEnabled()?1.0f:0.0f)
				.moveTo(sequencerTab)
				;

		tglStreams = new controlP5.Toggle(cp5, "streams")
				//.setPosition((tglLocked.getWidth()*2) + (margin_x*3) + (width/3), margin_y)
				.setPosition(tglEnabled.getPosition()[0] + tglLocked.getWidth() + margin_x, margin_y)
				.changeValue(this.isStreamsEnabled()?1.0f:0.0f)
				.moveTo(sequencerTab)
				;
		
		tglPlaylist = new controlP5.Toggle(cp5, "playlist")
				//.setPosition((tglLocked.getWidth()*3) + (margin_x*4) + (width/3), margin_y)
				.setPosition(tglStreams.getPosition()[0] + tglLocked.getWidth() + margin_x, margin_y)
				.changeValue(this.isHistoryMode()?1.0f:0.0f)
				.moveTo(sequencerTab)
				;


		txtProjectName = new controlP5.Textfield(cp5, "project_name")
				.setText(APP.getApp().pr.getProjectFilename())
				.setPosition(cf.sketchWidth()-200, margin_y)
				.setWidth(200)
				.moveTo(sequencerTab)
				.setAutoClear(false)
				;


		Tab streamEditorTab = cp5.addTab("Stream Editor");
		
		//Accordion accordion 
		this.grpStreamEditor = (StreamEditor) new StreamEditor(cp5, "stream editor")//this.makeStreamEditor(cf)
				.setPosition(0,40)
				.moveTo(streamEditorTab);
		this.grpStreamEditor.setupStreamEditor(cf, this.getStreams());

		super.updateGuiStatus();

		//lstHistory.addItems(this.historySequenceNames);

		this.grpSequenceEditor = (SequenceEditor) new SequenceEditor (cp5, "sequence editor")
				.setSequence(this.getCurrentSequenceName(), getActiveSequence())
				.setWidth(cp5.papplet.displayWidth/4)
				.setHeight(cp5.papplet.displayHeight/5)
				.setBarHeight(10)				
				.setPosition(0, margin_y * 8)
				.moveTo(sequencerTab)
				;

		//this.saveHistoryButton = cf.control().addBang("SAVE sequencer history").moveTo(tabName);		//.moveTo(((VurfEclipse)APP.getApp()).getCW()/*.getCurrentTab()*/).linebreak();
		//zthis.loadHistoryButton = cf.control().addBang("LOAD sequencer history").moveTo(tabName);		//.moveTo(((VurfEclipse)APP.getApp()).getCW()/*.getCurrentTab()*/).linebreak();
		cf.control().addCallback(this);
	}

	protected Accordion makeStreamEditor(ControlFrame cf) {
		Accordion accordion = cf.control().addAccordion("streams_editor").setWidth(cf.displayWidth/3);

		//Scene n;
		for (Entry<String, Stream> i : this.getStreams().entrySet()) {
			//String tabName = "["+c+"] " + n.getSceneName(); //getClass();
			//ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCP5();
			//Tab tab = cp5.addTab(tabName);

			String streamName = i.getKey();
			Group g = cf.control().addGroup(streamName);

			//println("added tab " + tabName);
			//ControllerInterface[] controls = ((Scene)i.next()).getControls();
			//cp5.begin(10,40);
			//((Scene)n).setupControls(cf,g);//tab);
			i.getValue().setupControls(cf,g);
			println("done setupControls for " + i.getValue());
			//cp5.end();

			accordion.addItem(g);

			/*for (int n = 0 ; n < controls.length ; n++) {
			    cp5.getTab("Scene " + c).add(controls[n]).moveTo("Scene " + c);
			    //cp5.addSlider(controls[n]).moveTo("Scene " + c);
			  }*/
			//c++;
			//((Scene)i).setupControls(cp5);
		}		

		accordion.open().setCollapseMode(Accordion.MULTI);

		return accordion;
	}

	synchronized public void updateGuiStreamEditor(ControlFrame cf) {
		this.grpStreamEditor.setupStreamEditor(cf, this.getStreams());
	}


	@Override public void controlEvent (CallbackEvent ev) {

		if (!ev.getController().isUserInteraction()) return;
		
		if (ev.getController() instanceof Textfield) {
			if (ev.getAction()==ControlP5.ACTION_ENTER || ev.getAction()==ControlP5.ACTION_CLICK) {
				((Textfield)ev.getController()).setFocus(true);
				host.setDisableKeys(true);	// horrible hack to disable keyboard input when a textfield is selected..
			} else if (ev.getAction()==ControlP5.ACTION_LEAVE) {
				((Textfield)ev.getController()).setFocus(false);
				host.setDisableKeys(false);	// horrible hack to disable keyboard input when a textfield is selected..
			} else {
				//println("caught " + ev);
			}
		}

		//println("controlevent in " + this);
		if (ev.getAction()==ControlP5.ACTION_RELEASE) {
			Controller c = ev.getController();
			if (ev.getController()==this.saveHistoryButton) {
				try {
					this.saveHistory();
				} catch (IOException e) {
					println("Problem saving history!");
					e.printStackTrace();
				}
			} else if (ev.getController()==this.loadHistoryButton) {
				try {
					this.loadHistory();
				} catch (IOException e) {
					println("Problem load history!");
					e.printStackTrace();
				}
			}
		} else if (ev.getAction()==ListBox.ACTION_CLICK && ev.getController()==this.lstHistory) {
				//println("My name is: " + this.lstSequences.getValueLabel().getText());
				String sequenceName = this.lstHistory.getValueLabel().getText();

				//Map<String, Object> test = this.lstSequences.getItem((int)this.lstSequences.getValue());
				//sequenceName = (String) test.get("value");

				println("got value " + (int)this.lstHistory.getValue());
				println("got list-selected sequenceName " + sequenceName);
				this.histMoveCursorAbsolute((int)this.lstHistory.getValue(),true); //distance, restart);
				//this.changeSequence(sequenceName, false, true);
		} else if (ev.getAction()==ControlP5.ACTION_BROADCAST) {
			if (ev.getController()==this.sldProgress) {
				this.getActiveSequence().setValuesForNorm(this.sldProgress.getValue()/100.0,this.getActiveSequence().iteration);
			} else if (ev.getController()==this.sldTimeScale) {
				//this.getActiveSequence().setValuesForNorm(this.sldTimeScale.getValue(),this.getActiveSequence());
				this.setTimeScale(sldTimeScale.getValue());
			} else if (ev.getController()==this.tglLocked) {
				this.toggleLock(((Toggle)ev.getController()).getBooleanValue()); //ev.getController().getValue()==1.0f);
			} else if (ev.getController()==this.tglEnabled) {
				this.toggleEnabled(((Toggle)ev.getController()).getBooleanValue()); //.getValue()==1.0f);
			} else if (ev.getController()==this.tglStreams) {
				this.toggleStreams(((Toggle)ev.getController()).getBooleanValue()); //ev.getController().getValue()==1.0f);
			} else if (ev.getController()==this.tglPlaylist) {
				this.togglePlaylist(((Toggle)ev.getController()).getBooleanValue()); //ev.getController().getValue()==1.0f);
			} else if (ev.getController()==this.txtCurrentSequenceName) {
				//TODO: sequencer rename functionality
				this.renameSequence(this.getCurrentSequenceName(), txtCurrentSequenceName.getText());

				// TODO: clone the play list entry...
				//this.addSequence(txtCurrentSequenceName.getText(), this.getActiveSequence()); // TODO: actually clone the active sequence!
				//this.changeSequence(txtCurrentSequenceName.getText());
			} else if (ev.getController()==this.txtSeed) {
				this.getActiveSequence().setSeed(Long.parseLong(this.txtSeed.getText()));
			}
			/*else if (ev.getController()==this.saveButton) {
        println("save preset " + getSceneName());
        //this.savePreset(saveFilenameController.getText(), getSerializedMap());
        this.savePreset(getSceneName());
      }
      else if (ev.getController()==this.loadButton) {
        println("load preset");
        this.loadPreset2(getSceneName()); //saveFilenameController.getText());
      }*/    
			/*
		} else if (ev.getController()==this.txtCurrentSequenceName) { // && ev.getController().isUserInteraction()) {
			//if (ev.getController() instanceof Textfield) {
				if (
						//ev.getAction()==Textfield.ACTION_RELEASE
						//(ev.getController().isUserInteraction() && (ev.getAction()==ControlP5.ACTION_BROADCAST || ev.getAction()==ControlP5.ACTION_DRAG || ev.getAction()==ControlP5.ACTION_RELEASE || ev.getAction()==ControlP5.ACTION_RELEASE_OUTSIDE || ev.getAction()==ControlP5.ACTION_PRESS)) //|| ev.getAction()==ControlP5.ACTION_BROADCAST)
						true//ev.getAction()==100
					) {
					println("sequencesequencer changed to " + ((Textfield)ev.getController()).getText());
				} else 

				if (ev.getController() instanceof Textfield && (ev.getAction()==ControlP5.ACTION_ENTER || ev.getAction()==ControlP5.ACTION_CLICK)) {
					println("sequencesequencer changed to " + ((Textfield)ev.getController()).getText());
					((Textfield)ev.getController()).setFocus(true);
					host.setDisableKeys(true);	// horrible hack to disable keyboard input when a textfield is selected..
				} else if (ev.getAction()==ControlP5.ACTION_LEAVE) {
					((Textfield)ev.getController()).setFocus(false);
					host.setDisableKeys(false);	// horrible hack to disable keyboard input when a textfield is selected..
				} else {
					ev.getController().setBroadcast(true);
					println ("got ev " + ev.getAction());
				}
				//if (ev.getController() instanceof Textfield) { // && !currentValue.equals(((Textfield)ev.getController()).getText())) {
					//sc.host.disableKeys = false;	// horrible hack to disable keyboard input when a textfield is selected..
					//((Textfield)ev.getController()).setFocus(true);
				//}
			//}
			 * 
			 */
		} else {
			super.controlEvent(ev);
		}
	}



	private void togglePlaylist(boolean b) {
		this.setHistoryMode(b);		
	}


	public void renameSequence(String currentSequenceName, String text) {
		SequenceSequencer self = this;

		this.sequences.put(text, this.getActiveSequence());

		int place = this.historySequenceNames.indexOf(currentSequenceName);
		while (place>=0) {
			this.historySequenceNames.set(place, text);
			place = this.historySequenceNames.indexOf(currentSequenceName);
		}

		historySequenceNames.remove(currentSequenceName);

		this.updateGuiHistory();
	}

	//TODO: plumb this cloneSequence in so it can be activated, and test it!
	public Sequence cloneSequence(String sequenceName, String newName) {
		Sequence sequence = this.getSequence(sequenceName);

		// clone it and add it back to sequences under new name
		sequence = (Sequence) sequence.clone();
		// add it to history under new name by switching to it
		this.addSequence(newName, sequence);
		changeSequence(newName);

		return sequence;
		// clone the play list entry...
		//this.addSequence(txtCurrentSequenceName.getText(), this.getActiveSequence()); // TODO: actually clone the active sequence!
		//this.changeSequence(txtCurrentSequenceName.getText());		
	}


	@Override public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();  
		params.put("/seq/seed", this.getActiveSequence().getSeed());

		this.getActiveSequence().setSceneParameters(this.host.collectSceneParameters());	// set the active sequence's parameters to be the current scene parameters
		params.put("/seq/sequence",this.getActiveSequence().collectParameters().put("current_sequence_name",  this.getCurrentSequenceName()));	// /seq/sequence to load it and add it to bank, whereas /seq/changeTo loads it and changes to it

		params.put("/seq/current_sequence_name", this.getCurrentSequenceName());	// just save the name, used when re-loading from xml or hashmap

		if (APP.getApp().pr instanceof SavedProject) {		// TODO: FIX THIS SOMEHOW if this is a loaded Project then save the entire bank 'cos its probably quite reasonable and small
			println("Saved project, so saving Bank Sequences!");
			params.put("/seq/bank/sequences", this.collectBankSequences());
		} else {											// if this is a Project loaded from a class then there is probably tens of thousands of Sequences in the bank, so only save the history instead
			println("Not a SavedProject, so saving the History as the Bank!");
			params.put("/seq/bank/sequences", this.collectBankHistory());
		}
		params.put("/seq/bank/history",  this.collectBankHistory());

		return params;
	}

	private LinkedHashMap<String,Object> collectBankHistory() {
		LinkedHashMap<String,Object> params = new LinkedHashMap<String,Object>();
		//for (Entry<String, Sequence> s : this.histor.entrySet()) {
		for (String e : this.historySequenceNames) {
			Sequence s = this.getSequence(e);
			HashMap<String,Object> t = s.collectParameters();
			t.put("current_sequence_name", e);			

			params.put(e, t);
		}
		return params;
	}

	public boolean notifyRemoval(Filter newf) {
		boolean relevant = super.notifyRemoval(newf);
		
		relevant = this.getActiveSequence().notifyRemoval(newf) || relevant;
		
		if (relevant) 
			this.updateGuiSequence();
		
		for (Entry<String, Sequence> s : sequences.entrySet()) {
			boolean t = s.getValue().notifyRemoval(newf);
			if (t==true) 
				relevant = t;
		}
		if (relevant) 
			this.updateGuiSequence();
		
		return relevant;
	}


	

	public void saveBankSequences (String filename) {
		for (Entry<String, Sequence> s : this.sequences.entrySet()) {
			s.getValue().saveSequencePreset("bank_" + filename + "_" + s.getKey() + ".xml");
		}
	}

	private LinkedHashMap<String,Object> collectBankSequences() {
		LinkedHashMap<String,Object> params = new LinkedHashMap<String,Object>();
		for (Entry<String, Sequence> s : this.sequences.entrySet()) {
			params.put(s.getKey(), s.getValue().collectParameters());
		}
		return params;
	}


	public void clearSequences() {
		this.sequences.clear();	
	}


	public void clearRandomSequences() {
		// TODO Auto-generated method stub
		this.randomPool.clear();	
	}


	public void preserveCurrentSceneParameters() {
		this.getActiveSequence().setSceneParameters(this.host.collectSceneParameters());
		this.getActiveSequence().preserveCurrentParameters();
	}

}
