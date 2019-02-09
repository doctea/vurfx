package vurfeclipse.sequence;

import controlP5.Accordion;
import controlP5.ControlP5;
import controlP5.Group;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Map;

import vurfeclipse.APP;
import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.ui.SequenceEditor;

public class ChainSequence extends Sequence {

	protected ArrayList<Sequence> chain = new ArrayList<Sequence>();
	
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
		
		if (!(this.chain.size()>0)) 
			this.initialiseDefaultChain(); 

		if (this.scene_parameters!=null && this.scene_parameters.size()>0) {		
			//this.initialiseDefaultChain();
			for (Sequence seq : chain) {
				seq.start();
			}
		}
	}
	
	protected void initialiseDefaultChain() {
		for (Sequence seq : chain) 
			if (seq instanceof ChainSequence) 
				((ChainSequence)seq).initialiseDefaultChain();
	}

	@Override public void stop() {
		super.stop();
		for (Sequence seq : chain) {
			seq.stop();
		}
	}
	
	@Override
	synchronized public boolean readyToChange(int max_i) {
		//return iteration>=max_i;
		// assume ready, unless one of the chained items isnt
		if (super.readyToChange(max_i)) return true;
		if (chain.size()>0) {
			for (Sequence seq : chain) {
				if (!seq.readyToChange(max_i)) return false;
			}
			return true;
		} else {
			return false;
		}
	}	
	
	
	@Override
	synchronized public ArrayList<Mutable> getMutables() {
		if (this.mutables==null) {
			ArrayList<Mutable> muts = super.getMutables();// new ArrayList<Mutable>();
			//if (host!=null) muts.add(host);
			for (Sequence seq : chain) {
				muts.addAll(seq.getMutables());
			}
			this.mutables = muts;
		}
		return this.mutables;
	}	
	
	@Override
	synchronized public void __setValuesForNorm(double pc, int iteration) {
		/*if (debug && iteration>0) 
			println ("setvaluesfornorm with non-zero iteration " + iteration);*/
		for (Sequence seq : chain) { 
			seq.setValuesForNorm(pc,iteration);
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
	synchronized public Map<String,Object> collectParameters() {
		Map<String,Object> params = super.collectParameters();
		ArrayList<Map<String,Object>> chains = new ArrayList<Map<String,Object>>();
		for (Sequence cs : chain) {
			Map<String, Map<String, Object>> temp = cs.getSceneParameters();
			cs.clearSceneParameters();	// remove any scene parameters that are set on the object before saving, since we save our own copy with the chain
			Map<String, Object> full_params = cs.collectParameters();
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
	public void restart() {
		super.restart();
		for (Sequence seq : chain) {
			//seq.clearSceneParameters(); // clear scene parameters because parent is dealing with them
			seq.setSceneParameters(null);//new HashMap<String, HashMap<String, Object>> ());	// set to empty scene_parameters instead of null, because otherwise onStart() gets triggered and overwrites values
			seq.restart();
		}
	}

	
	@Override
	synchronized public void loadParameters(Map<String,Object> params) {
		super.loadParameters(params);
		if (params.containsKey("chain")) {
			ArrayList<Map<String,Object>> chains = (ArrayList<Map<String,Object>>) params.get("chain");
			for (Map<String,Object> cs : chains) {
				// cs contains info to build a new ChainSequence and attach it
				//ChainSequence n = new ChainSequence(this.host, (Integer) cs.get("lengthMillis"));
				if (cs==null) {
					println("skipping null chain sequence from broken save file :(");
					continue;
				}
				try {
					if (cs.containsKey("scene_parameters")) cs.remove("scene_parameters");	// don't load scene_parameters for chained sequences, since if there are any they are there from an old version of save format
					Sequence n = Sequence.makeSequence((String) cs.get("class"), (Scene) APP.getApp().pr.getObjectForPath((String) cs.get("hostPath")));
					n.loadParameters(cs);
					this.addSequence(n);
				} catch (Exception e) {
					println("caught " + e + " trying to loadparameters for " + this);
				}
			}		
		} else {
			this.initialiseDefaultChain();
		}
	}
	
	SequenceEditor sequenceEditor;
	@Override
	synchronized public SequenceEditor makeControls(ControlFrame cf, String name) {
		// add an accordion to hold the sub-sequences and recurse
		if (null!=sequenceEditor) {
			//return sequenceEditor;
			sequenceEditor.removeControllers();
			sequenceEditor.removeListeners();
			sequenceEditor.remove();
		}
		sequenceEditor = super.makeControls(cf, name);
		
		ControlP5 cp5 = cf.control();
		//if (true) return sequenceEditor;
		
		
		//cp5.addLabel("Sequence Editor: " + name).setValue("Sequence Editor: " + name).moveTo(sequenceEditor).setPosition(100,100);
		Accordion acc = cp5.addAccordion(name + "_acc")
				.moveTo(sequenceEditor)
				.setWidth(sequenceEditor.getWidth()-10)
				//.setBackgroundHeight(cp5.papplet.sketchHeight()/5)
				.setPosition(10,40) //sequenceEditor.getBackgroundHeight())
				.setBackgroundHeight(10)
				.setBarHeight(15)
				.setCollapseMode(Accordion.MULTI);
				;
		
		int n = 0;
		for (Sequence cs : chain) {
			/*Group g = cp5.addGroup(cs.getClass().getSimpleName() + ": " + name + "         [acc_" + n + "_gr]")
					.moveTo(acc);*/
			Group conts = cs.makeControls(cf, cs.getClass().getSimpleName() + ": " + name + "    [n!: " + n + "]")
					//.setPosition(0,n*30))
					.setWidth(cp5.papplet.displayWidth/2);
			Group g = conts;
			//g.add(conts);
			//g.setBackgroundHeight(conts.getBackgroundHeight());
			//println("got a " + cs.getClass().getSimpleName() + " with height " + g.getBackgroundHeight()); 
			acc.addItem(g);
			acc.setBackgroundHeight(acc.getBackgroundHeight() + g.getBackgroundHeight());
			acc.setBarHeight(15);
			n++;
		}
		
		if (n>0)
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

	@Override
	public boolean notifyRemoval(Filter newf) {
		boolean relevant = false;
		for (Sequence s : this.chain) {
			if (s.notifyRemoval(newf))
				relevant = true;
		}
		return relevant;
	}	
	
	@Override
	public void preserveCurrentParameters() {
		super.preserveCurrentParameters();
		//this.lastLoadedParams = this.collectParameters();
		for (Sequence seq : chain) {
			seq.preserveCurrentParameters();
		}
	}

	@Override
	synchronized public void removeSequence(Sequence to_remove) {
		if (to_remove==this) {
			println("Told to remove self -- must be at top of chain, or parent has ignored request to remove me - not removing!");
			return;
		}
		synchronized(this.chain) {
			if (!this.chain.remove(to_remove)) {
				for (Sequence s : this.chain) {
					s.removeSequence(to_remove);
				}
			} else {
				println("removed " + to_remove + " from  "+ this + "!");
			}
		}
	}

	
}
