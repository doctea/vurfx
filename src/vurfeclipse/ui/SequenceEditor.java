package vurfeclipse.ui;

import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.Group;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.Sequence;

public class SequenceEditor extends Group {
	
	Sequence sequence;
	String sequenceName;

	public Sequence getSequence() {
		return sequence;
	}

	public void setSequence(String sequenceName, Sequence sequence) {
		this.sequence = sequence;
		this.sequenceName = sequenceName;
		setupControls();
	}

	public SequenceEditor(ControlP5 theControlP5, String theName) {
		super(theControlP5, theName);
		// TODO Auto-generated constructor stub
	}

	public SequenceEditor(ControlP5 theControlP5, ControllerGroup<?> theParent, String theName, int theX, int theY, int theW, int theH) {
		super(theControlP5, theParent, theName, theX, theY, theW, theH);
		// TODO Auto-generated constructor stub
	}

	public SequenceEditor setupControls() {
		cp5.addTextarea("length").moveTo(this);
		
		sequence.makeControls(cp5, sequenceName);
	
		return this;
	}

}
