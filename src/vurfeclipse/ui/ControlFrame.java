package vurfeclipse.ui;
import controlP5.ControlP5;
import controlP5.*;
import processing.core.PApplet;
import processing.event.KeyEvent;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;

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
    size(w, h); 	// P3D);
  }

  int setupCount = 0;
  public void setup() {
	setupCount++;
	System.out.println("ControlFrame setup count " + setupCount);
    this.cp5 = new ControlP5(this);
    cp5.setFont(createFont("Arial",10));
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

  public void draw() {
    background(190);
  }
  
  @Override
  public void keyPressed () {
	  if(this.key=='1') {
		  this.cp5.getWindow().activateTab("Sequencer");
	  } else if (this.key=='2') {
		  this.cp5.getWindow().activateTab("Scenes");
	  } else if (this.key=='3') {
		  this.cp5.getWindow().activateTab("Monitor");
	  } else {
		  ((VurfEclipse)parent).handleKey(key);

	  }	  
  }
}