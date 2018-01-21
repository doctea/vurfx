package vurfeclipse.sequence;

import java.util.List;

import controlP5.Accordion;
import controlP5.ControlP5;
import controlP5.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import vurfeclipse.APP;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.ui.SequenceEditor;

public class ChainSequence extends Sequence {

	ArrayList<Sequence> chain = new ArrayList<Sequence>();
	
	public ChainSequence() { super(); }
	
	public ChainSequence(Scene host, int lengthMillis) {
		super(host,lengthMillis);
	}
	public ChainSequence(int lengthMillis) {
		super(lengthMillis);
	}
	synchronized public ChainSequence addSequence(Sequence seq) {
		seq.setLengthMillis(this.getLengthMillis());
		if (this.host==null) this.host = seq.host;
		chain.add(seq);
		return this;
	}
	synchronized public ChainSequence addSequence(Scene sc, String sequenceName) {
		return addSequence(sc.getSequence(sequenceName));
	}
	
	@Override public void start() {
		super.start();
		if (this.scene_parameters==null) {
			Iterator<Sequence> it = chain.iterator();
			while(it.hasNext()) {
				it.next().start();
			}
		}
	}
	@Override public void stop() {
		super.stop();
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().stop();
		}
	}
	
	@Override
	synchronized public boolean readyToChange(int max_i) {
		//return iteration>=max_i;
		// assume ready, unless one of the chained items isnt
		if (super.readyToChange(max_i)) return true;
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			if (!it.next().readyToChange(max_i)) return false;
		}
		return true;
	}	
	
	
	@Override
	synchronized public ArrayList<Mutable> getMutables() {
		if (this.mutables==null) {
			ArrayList<Mutable> muts = super.getMutables();// new ArrayList<Mutable>();
			//if (host!=null) muts.add(host);
			Iterator<Sequence> it = chain.iterator();
			while(it.hasNext()) {
				muts.addAll(it.next().getMutables());
			}
			this.mutables = muts;
		}
		return this.mutables;
	}	
	
	@Override
	synchronized public void setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().setValuesForNorm(pc,iteration);
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
	  /*Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().onStart();
		}*/
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		/*Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().onStop();
		}*/
	}

	@Override
	synchronized public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		ArrayList<HashMap<String,Object>> chains = new ArrayList<HashMap<String,Object>>();
		for (Sequence cs : chain) {
			HashMap<String,HashMap<String, Object>> temp = cs.getSceneParameters();
			cs.clearSceneParameters();	// remove any scene parameters that are set on the object before saving, since we save our own copy with the chain
			HashMap<String, Object> full_params = cs.collectParameters();
			if (full_params==null) {
				println("wtf, saving null full_params..?");
				cs.collectParameters();
			}
			chains.add(full_params);
		}
		params.put("chain",  chains);
		/*params.put("filterPath", filterPath);
		params.put("parameterName", parameterName);
		params.put("value", "value");*/
		return params;
	}
	
	@Override
	synchronized public void loadParameters(HashMap<String,Object> params) {
		super.loadParameters(params);
		ArrayList<HashMap<String,Object>> chains = (ArrayList<HashMap<String,Object>>) params.get("chain");
		for (HashMap<String,Object> cs : chains) {
			// cs contains info to build a new ChainSequence and attach it
			//ChainSequence n = new ChainSequence(this.host, (Integer) cs.get("lengthMillis"));
			if (cs==null) {
				println("skipping null chain sequence from broken save file :(");
				continue;
			}
			if (cs.containsKey("scene_parameters")) cs.remove("scene_parameters");	// don't load scene_parameters for chained sequences, since if there are any they are there from an old version of save format
			Sequence n = Sequence.makeSequence((String) cs.get("class"), (Scene) APP.getApp().pr.getObjectForPath((String) cs.get("hostPath")));
			n.loadParameters(cs);
			this.addSequence(n);
		}		
	}
	
	
	@Override
	synchronized public SequenceEditor makeControls(ControlP5 cp5, String name) {
		// add an accordion to hold the sub-sequences and recurse
		SequenceEditor sequenceEditor = super.makeControls(cp5, name);
		
		//if (true) return sequenceEditor;
		
		//cp5.addLabel("Sequence Editor: " + name).setValue("Sequence Editor: " + name).moveTo(sequenceEditor).setPosition(100,100);
		Accordion acc = cp5.addAccordion(name + "_acc")
				.moveTo(sequenceEditor)
				.setWidth(sequenceEditor.getWidth()-10)
				//.setBackgroundHeight(cp5.papplet.sketchHeight()/5)
				.setPosition(10,sequenceEditor.getBackgroundHeight())
				.setBackgroundHeight(10)
				.setCollapseMode(Accordion.MULTI);
				;
		
		int n = 0;
		for (Sequence cs : chain) {
			/*Group g = cp5.addGroup(cs.getClass().getSimpleName() + ": " + name + "         [acc_" + n + "_gr]")
					.moveTo(acc);*/
			Group conts = cs.makeControls(cp5, cs.getClass().getSimpleName() + ": " + name + "    [n!: " + n + "]")
					//.setPosition(0,n*30))
					.setWidth(cp5.papplet.displayWidth/2);
			Group g = conts;
			//g.add(conts);
			//g.setBackgroundHeight(conts.getBackgroundHeight());
			println("got a " + cs.getClass().getSimpleName() + " with height " + g.getBackgroundHeight()); 
			acc.addItem(g);
			acc.setBackgroundHeight(acc.getBackgroundHeight() + g.getBackgroundHeight());
			n++;
		}
		
		acc.open();
		
		//sequenceEditor.add(acc.moveTo(sequenceEditor));
		//sequenceEditor.setBackgroundHeight(n * 30);
		
		sequenceEditor.setBackgroundHeight(sequenceEditor.getBackgroundHeight() + acc.getBackgroundHeight());
		
		/*int n = 0;
		int y = 40;
		for (Sequence cs : chain) {
			Group t = cs.makeControls(cp5, "n: " + n + " - " + name + n + " " + cs.toString())
				.setPosition(10, y) //y) //10 + (20 * n) ) //80 + (n * 30)
					//+ (n*30)).setColorBackground((int) (Math.random()*255))
				.moveTo(sequenceEditor); //80 + (n * 30)).moveTo(sequenceEditor);
			n++;
			//y += n * 60; //t.getBackgroundHeight();
			y += 30 + t.getBackgroundHeight();
			println(this + " for " + cs + ": got backgroundheight " + t.getBackgroundHeight() + " so updating y to " + y);
		}
		
		println (this + ":setting backgroundheight " + y);
		sequenceEditor.setBackgroundHeight(sequenceEditor.getBackgroundHeight() + y);*/
		
		// different approach, flatten all the sequences. since this is a chain sequence, we know how to handle chain sequences
		/*ArrayList<Sequence> sequences = new ArrayList<Sequence>();
		//sequences.addAll(this.chain);
		for (Sequence cs : chain) {
			if 
		}*/
		
		return sequenceEditor;
	}	
	
}
