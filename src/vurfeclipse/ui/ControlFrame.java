package vurfeclipse.ui;
import controlP5.ControlP5;
import controlP5.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.projects.Project;
import vurfeclipse.ui.ControlFrame.MonitorCanvas;

public class ControlFrame extends PApplet {

  int w, h;
  PApplet parent;
  ControlP5 cp5;

	public MonitorCanvas getMonitorCanvas(Project pr) {
		return new MonitorCanvas(parent,pr);
	}

	public class MonitorCanvas extends controlP5.Canvas {
		public PImage monitor;
		private Project pr;

		public MonitorCanvas(PApplet parent, Project pr) {
			this.pr = pr;
		}

		@Override
		synchronized public void draw(PGraphics pg) {
			// renders a square with randomly changing colors
			// make changes here.
			//pg.fill(100);
			//pg.rect(APP.getApp().random(255)-20, APP.getApp().random(255)-20, 240, 30);
			//pg.fill(255);
			//pg.beginDraw();

			//cp5.setGraphics(pr.getCanvas(getPath()+"out").getSurf(), 0, 0);

			if (pr.isInitialised() && pr.monitor!=null && pr.monitor[0]!=null) { //&& APP.getApp().millis()%30==0) {
				//pg.beginDraw();

				
				pg.beginDraw();
				//pg.text("This text is drawn by MyCanvas !!", 0/*APP.getApp().random(255)*/,APP.getApp().random(255));
				pg.image(//monitor // 
						//pr.getCanvas("/out").getSurf().get() //getCache(pr.getCanvas("/out").getSurf().get())
						pr.monitor[0],
						//,0 ,0, 128, 128
						0, 20 
				);
				//APP.getApp().spout.receiveTexture(pg); //,0,0);
				
				int w = 128; int h = 96; int margin_y = 20;
				for (int i = 0 ; i < pr.monitor.length ; i++) {
						if (null!=pr.monitor[i]) pg.image(pr.monitor[i], i * w, margin_y, w, h);
				}
				
				pg.endDraw();

				/*pr.getCanvas(getPath()+"out").getSurf().loadPixels();
				    PImage i = pr.getCanvas(getPath()+"out").getSurf().get(); 
			    	pg.image(i,0,150,w/8,h/8);
			    	pg.endDraw();*/
			}
			//pg.endDraw();
			//
		}

	}
  public ControlFrame(PApplet _parent, int _w, int _h, String _name) {
    super();   
    parent = _parent;
    w=_w;
    h=_h;
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }

  public void settings() {
    size(w, h, P3D); 	// P3D);
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
	 
     frameRate(30);

	 
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
	  ((VurfEclipse)parent).handleKey(key);
  }
}