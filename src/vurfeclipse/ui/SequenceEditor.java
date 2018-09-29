package vurfeclipse.ui;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.ControllerList;
import controlP5.Group;
import controlP5.Tab;
import controlP5.Toggle;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequencers.SequenceSequencer;

public class SequenceEditor extends Group {
	
	Sequence sequence;
	String sequenceName;
	private Group controller;
	
	public Sequence getSequence() {
		return sequence;
	}

	public SequenceEditor setSequence(String sequenceName, Sequence sequence) {
		this.sequence = sequence;
		this.sequenceName = sequenceName;
		setupControls();
		return this;
	}

	public SequenceEditor(ControlP5 theControlP5, String theName) {
		super(theControlP5, theName);
		// TODO Auto-generated constructor stub
	}

	public SequenceEditor(ControlP5 theControlP5, ControllerGroup<?> theParent, String theName, int theX, int theY, int theW, int theH) {
		super(theControlP5, theParent, theName, theX, theY, theW, theH);
		// TODO Auto-generated constructor stub
		//this.setBarHeight(20);
	}

	boolean disable_controls = true;
	synchronized public SequenceEditor setupControls() {

		/*for (ControllerInterface<?> c : Collections.synchronizedList((List) this.controllers)) {
			this.remove(c);
		}*/
		//if (true) return this;
		if (((SequenceSequencer) APP.getApp().pr.getSequencer()).getActiveSequence()!=sequence) {
			sequence.println("skipping setup of controls for non-active sequence!");
			return this;
		}
		//this.controllers.get().clear();
		this.removeControllers();
		//System.gc();
		// this.removeListeners();
		/*if (APP.getApp().getCF().sequenceEditor!=null)
			APP.getApp().getCF().control().remove(APP.getApp().getCF().sequenceEditor);
		APP.getApp().getCF().sequenceEditor = null;*/
		//this.remove();
		
		
		//boolean disable_controls = false; //true;//false;//false; //
		if (sequence!=null && true) { //isEnableControls()) {
			if (this.controller!=null) {
				this.controller.removeListeners();
				this.controller.removeControllers();
				//this.removeSubEditors();
				// remove listeners from filters that correspond to any controller owned by this.controller..?
				this.removeControllers();
				this.controller = null;
				this.removeControllers();
				this.removeListeners();
				//((SequenceSequencer)APP.getApp().pr.getSequencer()).getActiveSequence().seq = null;
			}
			this.controller = sequence.makeControls(APP.getApp().getCF(), sequence.getClass().getSimpleName() + ": " + sequenceName).moveTo(this).setPosition(0,10);
		} else {
			System.out.println("SequenceEditor controls disabled, not creating!");
		}
	
		return this;
	}
	
	boolean enableControls = false;
	public boolean isEnableControls() {
		return enableControls;
	}

	public void setDisableControls(boolean booleanValue) {
		this.enableControls = booleanValue;		
	}	
}
