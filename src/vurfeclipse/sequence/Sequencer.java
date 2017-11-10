package vurfeclipse.sequence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import controlP5.Bang;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import vurfeclipse.Targetable;
import vurfeclipse.projects.Project;
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

	public boolean readyToChange(int max_iterations) {
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
		this.locked = !this.locked;
		return locked;
	}

	public void setForward() {
		this.forward = true;
	}

	abstract public void runSequences();

	public HashMap<String, Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = new HashMap<String, Targetable>();

		urls.put("/seq/toggleLock", this);
		urls.put("/seq/forward", this);

		urls.put("/seq/changeTo", this);

		return urls;
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
    } else {
    	return false;
    }
		return true;
	}

	public void setupControls(ControlFrame cf, String string) {
		// TODO Auto-generated method stub

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
		params.put("/seq/changeTo", this.getCurrentSequenceName());
		return params;
	}

}
