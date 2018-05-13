package vurfeclipse.ui;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import processing.core.PApplet;
import vurfeclipse.VurfEclipse;
import vurfeclipse.sequencers.SequenceSequencer;

public class ControlFrame extends PApplet {

	int w, h;
	PApplet parent;
	ControlP5 cp5;

	public ControlFrame(PApplet _parent, int _w, int _h, String _name) {
		super();   
		parent = _parent;
		w=_w;
		h=_h;
		PApplet.runSketch(new String[]{this.getClass().getName()}, this);
	}

	public void settings() {
		size(w, h); //, P3D);
	}
	
	@Override
	public void exit() {
		System.out.println("NOW!");
	}

	int setupCount = 0;
	public void setup() {
		setupCount++;
		System.out.println("ControlFrame setup count " + setupCount);
		this.cp5 = new ControlP5(this);
		cp5.setFont(createFont("Arial",10));
		frameRate(60);

		//cp5.getPointer().enable(); // experimental .. 

		
		/*    
    cp5.addToggle("auto")
       //.plugTo(parent, "auto")
       .setPosition(10, 70)
       .setSize(50, 50)
       .setValue(true);

    cp5.addKnob("blend")
       //.plugTo(parent, "c3")
       .setPosition(100, 300)
       .setSize(200, 200)
       .setRange(0, 255)
       .setValue(200);

    cp5.addNumberbox("color-red")
       //.plugTo(parent, "c0")
       .setRange(0, 255)
       .setValue(255)
       .setPosition(100, 10)
       .setSize(100, 20);

    cp5.addNumberbox("color-green")
       //.plugTo(parent, "c1")
       .setRange(0, 255)
       .setValue(128)
       .setPosition(100, 70)
       .setSize(100, 20);

    cp5.addNumberbox("color-blue")
       //.plugTo(parent, "c2")
       .setRange(0, 255)
       .setValue(0)
       .setPosition(100, 130)
       .setSize(100, 20);

    cp5.addSlider("speed")
       //.plugTo(parent, "speed")
       .setRange(0, (float) 0.1)
       .setValue((float) 0.01)
       .setPosition(100, 240)
       .setSize(200, 30);
		 */
		System.out.println("From " + this + ": calling setupControls() on " + VurfEclipse.pr);
		VurfEclipse.pr.setupControls(this);

		//surface.setLocation(10, 10);

		System.out.println("Finished ControlFrame setup()");
	}

	public ControlP5 control() {
		//if (this.cp5==null)
		//  this.setup();
		return this.cp5;
	}

	boolean processing = false;
	public void draw() {
		background(190);
		if (!processing) {
			processing = true;
			this.processUpdateQueue();
			processing = false;
		}
	}
	
	List<Runnable> updateQueue = Collections.synchronizedList(new ArrayList<Runnable>());

	synchronized private void processUpdateQueue() {
		//if (processing) return;
		ArrayList<Runnable> runQueue = new ArrayList<Runnable>();
		synchronized(updateQueue) {
			runQueue.addAll(updateQueue);
		}
			ListIterator<Runnable> li = runQueue.listIterator();
			//println("starting queue loop...");
			while (li.hasNext()) {
				Runnable q = li.next();
				q.run();
				synchronized(updateQueue) {
					updateQueue.remove(q);
				}
			}
			//println("...out of queue loop and removed spent");
			//this.clearQueue();
		//}
	}

	synchronized private void clearQueue() {
		updateQueue.clear();
	}

	public void queueUpdate(final Runnable runnable) {
		//synchronized(updateQueue) {
			/*if (debug && processing) 
				println("queueing a " + runnable + " while processing!!");*/
			synchronized (updateQueue) {
				this.updateQueue.add(runnable);
			}
		//}
	}

	@Override
	public void keyPressed () {
		((VurfEclipse)parent).handleKey(key);
	}

	public void updateGuiStreamEditor() {
		final ControlFrame self = this;
		this.queueUpdate(new Runnable() {
			@Override
			public void run() {
				((SequenceSequencer)VurfEclipse.pr.getSequencer()).updateGuiStreamEditor(self); //.streamEditor.setupControl(this, VurfEclipse.pr.getSequencer().getStreams());
			}
		});
	}
	
	public CallbackListener toFront = new CallbackListener() {
		public void controlEvent(CallbackEvent theEvent) {
			theEvent.getController().bringToFront();
			theEvent.getController().getParent().bringToFront();
			((ScrollableList)theEvent.getController()).open();
		}
	};

	public CallbackListener close = new CallbackListener() {
		public void controlEvent(CallbackEvent theEvent) {
			((ScrollableList)theEvent.getController()).close();
		}
	};

}