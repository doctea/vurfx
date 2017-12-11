package vurfeclipse.sequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.connectors.XMLSerializer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.*;
import vurfeclipse.ui.ControlFrame;
import controlP5.Bang;
import controlP5.CallbackEvent;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.ListBox;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textfield;




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
	private boolean historyMode = false;

	private ArrayList<String> historySequenceNames = new ArrayList<String>();
	private int historyCursor;

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
			this.historySequenceNames = (ArrayList<String>) ois.readObject();
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


	@Override
	synchronized public boolean runSequences() {
		if (!APP.getApp().isReady()) return false;
		if (!this.isSequencerEnabled()) return false;
		if (stopSequencesFlag) {
			Iterator<Sequence> it = sequences.values().iterator();
			while (it.hasNext()) {
				Sequence next = it.next();
				if (next!=null) next.stop();
			}
			stopSequencesFlag = false;
		}
		//println(this+"#runSequences");
		// probably want to move this up to Sequencer and do super.runSequences()
		if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			println(this+"#runSequences(): is readyToChange from " + this.activeSequenceName + ", calling randomSequence()");
			nextSequence();
		}
		if (getActiveSequence()==null) nextSequence();

		//host.setTimeScale(0.1f);

		if (getActiveSequence()!=null) getActiveSequence().setValuesForTime();

		//gui : update current progress
		if (getActiveSequence()!=null) this.updateGuiProgress(getActiveSequence());
		
		return getActiveSequence()!=null;
	}





	public Sequence getActiveSequence () {
		return sequences.get(activeSequenceName);
	}


	public Sequence getSequence(String name) {
		// TODO Auto-generated method stub
		return sequences.get(name);
	}

	@Override
	public HashMap<String, Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = super.getTargetURLs(); //new HashMap<String,Targetable>();
		//urls.putAll(super.getTargetURLs());

		urls.put("/seq/seed", this);
		urls.put("/seq/changeTo", this);
		urls.put("/seq/sequence", this);

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Sequence> e = it.next();
			//int count = 0;

			// todo: move the path generation into Sequence
			urls.put("/seq/changeTo/" + e.getKey(), this);
			urls.put("/seq/sequence/" + e.getKey(), this);

			// this loop processes each Sequence for the current Sequence; dont think we need to add a URL for each of em!
			/*Iterator<Sequence> sit = e.getValue().iterator();
				   while (sit.hasNext()) {
					Sequence seq = sit.next();
					//urls.put("/seq/" + e.getKey() + "/" + count, seq);
				}*/
		}

		return urls;
	}


	@Override
	public Object target(String path, Object payload) {
		println(this + "#target('"+path+"', '"+payload+"')");

		String[] spl = path.split("/",4); // TODO: much better URL and parameter checking.
		if (spl[2].equals("changeTo")) {	   
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
		} else if (spl[2].equals("sequence")) {					// creates a sequence and adds it to the bank, but doesn't switch to it. returns url to change to the sequence
			if (payload instanceof HashMap<?,?>) {		
				Sequence newSeq = this.createSequence((HashMap<String,Object>)payload);
				String seqName = (String) ((HashMap) payload).get("current_sequence_name");
				seqName = seqName==null?"loaded":"loaded " + seqName;
				this.addSequence(seqName, newSeq);
				return "/seq/changeTo/"+seqName;
			} else {
				return this.collectParameters();	// strictly this should be done in a target that explicitly means 'current sequence' 
			}
		} else if (spl[2].equals("seed")) {
			this.getActiveSequence().setSeed((Long)payload);
			return "Set seed to "+payload;
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

	synchronized public Sequence addSequence(String SequenceName, Sequence sc) {
		sequences.put(SequenceName, sc);
		if (activeSequenceName.equals("")) activeSequenceName = SequenceName;

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
		int count = randomPool.size();
		int chosen = 0;
		//try {
		chosen = (int)APP.getApp().random(0,count);
		if (chosen<seqList.size()) seq_pos = chosen; // set list index
		if (chosen<0) chosen = 0;
		//changeSequence((String)sequences.keySet().toArray()[chosen]);
		println("Chose random element " + chosen + " of " + count + "('" + (String)randomPool.get(chosen) + "')");
		changeSequence((String)randomPool.toArray()[chosen]);
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
		if (historyMode) {
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

	public void changeSequence(String sequenceName, boolean remember, boolean restart) {
		if (this.getActiveSequence()!=null) {		// mute the current sequence 
			if (restart) this.getActiveSequence().stop();//setMuted(true);
			// check if this is already the top of the sequence history, if so don't add it again 
		}

		this.activeSequenceName = sequenceName;

		if (null==this.getActiveSequence()) {
			println("Got NULL for " + this.activeSequenceName + "!");
			return;
		}

		int oldCursor = historyCursor;
		
		println("Changing to sequence: " + sequenceName + "  (" + this.getActiveSequence().toString() + ")");
		if (remember && this.shouldRemember(sequenceName)) {
			this.addHistorySequenceName(sequenceName);
			//if (historyCursor==this.historySequenceNames.size()-1)	// if the cursor is already tracking the history then set cursor to most recent item so that 'j' does jump to the most recent sequence

			historyCursor = this.historySequenceNames.size()-1;

			// GUI: add latest sequence to history GUI
			if (this.getActiveSequence()!=null) this.lstSequences.addItem(this.getCurrentSequenceName(), this.getCurrentSequenceName());
		}
		
		// update gui for changed sequences
		this.updateGuiSequenceChanged(oldCursor, historyCursor);
		
		//muteAllSequences();
		//this.getActiveSequence().setMuted(false);	/// WTF IS THIS ..? 
		if (restart) this.getActiveSequence().start();
	}


	private void updateGuiSequenceChanged(int oldCursor, int newCursor) {
		if (oldCursor!=newCursor) this.lstSequences.getItem(oldCursor).put("state", false);
		if (newCursor>=this.lstSequences.getItems().size()) this.lstSequences.getItem(newCursor).put("state", true);
		if (!getCurrentSequenceName().equals("")) {
			this.txtCurrentSequenceName.setValue(this.getCurrentSequenceName());
		}
	}

	private void updateGuiProgress(Sequence activeSequence) {
		this.sldProgress.changeValue(activeSequence.getPositionPC()*100);
		this.sldProgress.setLabel("Progress iteration ["+(activeSequence.getPositionIteration()+1)+"/"+max_iterations+"]");
	}

	public void updateGuiTimeScale(double f) {
		this.sldTimeScale.changeValue((float) f);
	}


	private boolean shouldRemember(String sequenceName) {
		// TODO Auto-generated method stub
		if (sequenceName.contains("_next_")) {
			return false;
		}
		if (getSequence(sequenceName).readyToChange(0)) {
			host.println(this.toString() + ": " + sequenceName +" is ready to change for 0, not remembering!");;
		}
		return true;
	}


	private void addHistorySequenceName(String activeSequenceName2) {
		println("Added " + activeSequenceName2 + " as the " + this.historySequenceNames.size() + "th history item");
		historySequenceNames.add(activeSequenceName2);
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
		/*if (key=='w') {
    	try {
    			saveHistory();
    	} catch (IOException e) {
    		System.out.println("Couldn't save history! " + e);
    	}
    } else */if (key=='e') {
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
    } else if (key=='p') {
    	this.historyMode = !this.historyMode;
    	println ("historyMode set to " + historyMode);
    	/*    } else if (key=='f') {
    	saveSequence(this.getCurrentSequenceName() + ((VurfEclipse)APP.getApp()).dateStamp());
    } else if (key=='F') {
    	//loadSequence(host.getApp().select.selectInput("Load a sequence", activeSequenceName));
    	loadSequence("test.xml");*/
    } else if (super.sendKeyPressed(key)) {
    } else {
    	return false;
    }
    return true;
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


	protected Bang saveHistoryButton;
	protected Bang loadHistoryButton;

	private Textfield txtCurrentSequenceName;
	private ListBox lstSequences;
	private Slider sldProgress;
	private Slider sldTimeScale;
	@Override public void setupControls (ControlFrame cf, String tabName) {
		super.setupControls(cf, tabName);

		println("Project#setupControls about to grab cp5 before scene loop..");
		final ControlP5 cp5 = cf.control();

		//this.setupMonitor(cp5);

		int c = 0;

		int margin_y = 20; // start under the tab row
		int margin_x = 5;

		int width = cf.sketchWidth();
		int height = cf.sketchHeight();

		Tab sceneTab = cp5.addTab(tabName);

		txtCurrentSequenceName = new Textfield(cp5, "Current Sequence Name")
				.setPosition(margin_x, margin_y)
				.setWidth(width/3)
				.moveTo(sceneTab);
		sceneTab.add(txtCurrentSequenceName);

		lstSequences = new controlP5.ListBox(cp5, "sequence names")  	    		
				.setPosition(width-(width/3), margin_y + 100)
				.setSize(width/3, height-margin_y-100)
				.setItemHeight(20)
				.moveTo(sceneTab)
				.setType(ListBox.LIST);

		sldProgress = new controlP5.Slider(cp5, "progress")
				.setPosition(margin_x, margin_y * 3)
				.setWidth(width/3)
				.setHeight(margin_y*2)
				.moveTo(sceneTab)
				.setValue(0.0f);
		
		sldTimeScale = new controlP5.Slider(cp5, "timescale")
				.setPosition(margin_x, margin_y * 5)
				.setWidth(width/3)
				.setHeight(margin_y*2)
				.setRange(0.01f, 8.0f)
				.moveTo(sceneTab)
				.setValue(0.0f);
		
		tglLocked = new controlP5.Toggle(cp5, "locked")
				.setPosition(margin_x + (width/3), margin_y)
				.moveTo(sceneTab)
				.setValue(0.0f);
		
		tglEnabled = new controlP5.Toggle(cp5, "enabled")
				.setPosition(tglLocked.getWidth() + (margin_x*2) + (width/3), margin_y)
				.changeValue(this.isSequencerEnabled()?1.0f:0.0f)
				.moveTo(sceneTab)
				;
		
		tglStreams = new controlP5.Toggle(cp5, "streams")
				.setPosition((tglLocked.getWidth()*2) + (margin_x*3) + (width/3), margin_y)
				.changeValue(this.isStreamsEnabled()?1.0f:0.0f)
				.moveTo(sceneTab)
				;

		super.updateGuiStatus();
		
		//this.saveHistoryButton = cf.control().addBang("SAVE sequencer history").moveTo(tabName);		//.moveTo(((VurfEclipse)APP.getApp()).getCW()/*.getCurrentTab()*/).linebreak();
		//zthis.loadHistoryButton = cf.control().addBang("LOAD sequencer history").moveTo(tabName);		//.moveTo(((VurfEclipse)APP.getApp()).getCW()/*.getCurrentTab()*/).linebreak();
		cf.control().addCallback(this);
	}

	@Override public void controlEvent (CallbackEvent ev) {
		//println("controlevent in " + this);
		if (ev.getAction()==ControlP5.ACTION_RELEASED) {
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
		} else if (ev.getAction()==ListBox.ACTION_CLICK) { 
			if (ev.getController()==this.lstSequences) {
				//println("My name is: " + this.lstSequences.getValueLabel().getText());
				String sequenceName = this.lstSequences.getValueLabel().getText();

				Map<String, Object> test = this.lstSequences.getItem((int)this.lstSequences.getValue());
				//sequenceName = (String) test.get("value");

				println("got value " + (int)this.lstSequences.getValue());
				println("got list-selected sequenceName " + sequenceName);
				this.histMoveCursorAbsolute((int)this.lstSequences.getValue(),true); //distance, restart);
				//this.changeSequence(sequenceName, false, true);
			} else if (ev.getController()==this.tglLocked) {
				this.toggleLock(ev.getController().getValue()==1.0f);
			} else if (ev.getController()==this.tglEnabled) {
				this.toggleEnabled(ev.getController().getValue()==1.0f);
			} else if (ev.getController()==this.tglStreams) {
				this.toggleStreams(ev.getController().getValue()==1.0f);
			}
		} else if (ev.getAction()==ControlP5.ACTION_BROADCAST) {
			if (ev.getController()==this.sldProgress) {
				this.getActiveSequence().setValuesForNorm(this.sldProgress.getValue(),this.getActiveSequence().iteration);
			} else if (ev.getController()==this.sldTimeScale) {
				//this.getActiveSequence().setValuesForNorm(this.sldTimeScale.getValue(),this.getActiveSequence());
				this.setTimeScale(sldTimeScale.getValue());
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
		} else {
			super.controlEvent(ev);
		}
	}



	@Override public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();  
		params.put("/seq/seed", this.getActiveSequence().getSeed());
		params.put("/seq/sequence",this.getActiveSequence().collectParameters());	// /seq/sequence to load it and add it to bank, whereas /seq/changeTo loads it and changes to it
		params.put("/seq/current_sequence_name", this.getCurrentSequenceName());	// just save the name, used when re-loading from xml or hashmap
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
	}

}
