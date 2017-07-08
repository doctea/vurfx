package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import vurfeclipse.Targetable;
import vurfeclipse.projects.Project;

abstract public class Sequencer implements Targetable {
	public Project host;
	
	boolean locked = false;
	boolean forward = false;
	
	boolean outputDebug = true;
	public void println(String text) {		// debugPrint, printDebug -- you get the idea
		if (outputDebug) System.out.println("SQR " + (text.contains((this.toString()))? text : this+": "+text));
	}
	
	int w,h;
	public boolean readyToChange(int max_iterations) {
		  if (forward) { forward = false; return true; };
		  if (locked) return false;
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
			HashMap<String, Targetable> urls = new HashMap<String,Targetable>();
			
			urls.put("/seq/toggleLock", this);
			urls.put("/seq/forward", this);
			
			urls.put("/seq/changeTo", this);
			
			return urls;
		}
		abstract public String getCurrentSequenceName() ;
		
		public boolean isLocked() {
			return this.locked;
		}

}
