package vurfeclipse;



import processing.core.*;
import controlP5.*;

//import javax.media.j3d.*;

import vurfeclipse.projects.*;
import vurfeclipse.user.projects.*;
import codeanticode.glgraphics.*;
import ddf.minim.*;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.*;

//import javax.media.opengl.*;
import processing.opengl.*;
import javax.media.opengl.GL;

import fullscreen.*;

public class VurfEclipse extends PApplet {
	/*public void setup() {
	}
	public void draw() {
		fill(color(random(255),random(255),random(255)));
		rect (width/2,height/2,5,5);
	}*/

	boolean hdRes = false;//true;
	boolean mdRes = false;
	boolean projRes = false;
	boolean ultrahiRes = true;
	boolean hiRes = true;
	boolean medRes = true;

	public boolean exportMode = false; //true;

	FullScreen fs;
	boolean fullscreen = false;
	
  boolean ready = false;


	///// SYPHON STUFF (choose one - disabled stuff or enabled stuff)

	// DISABLED SYPHON BLOCK (stubs)
	boolean syphon = false;
	public void drawSyphon(GLGraphicsOffScreen offscreen) {};
	public void initSyphon(GL gl, String theName) {};

	/*
	// ENABLE SYPHON BLOCK (supposed working code) //JSyphon stuff+info at https://forum.processing.org/topic/syphon-integration-with-processing
	//boolean syphon = true;

	/////// JSyphon
	import jsyphon.*;
	JSyphonServer mySyphon;	////Syphon stuff
	public void drawSyphon (GLGraphicsOffScreen offscreen) {
		 if (syphon) {
			 GLTexture tex = offscreen.getTexture();
			 image(tex, 0, 0, output_width, output_height);
			 server.publishFrameTexture(tex.getTextureID(), tex.getTextureTarget(), 0, 0, tex.width, tex.height, tex.width, tex.height, false);

			 renderTexture(tex);	// guessing 'tex', was 'gl'
		 }
	}

	void initSyphon(GL gl, String theName) {
		if(mySyphon!=null) {
			// in case you are using
			//  hint(DISABLE_OPENGL_2X_SMOOTH); or hint(ENABLE_OPENGL_4X_SMOOTH);
			// setup will be called a second or third time and consequently initSyphon(), too.
			// Therefore, in case a Syphon server is running, we stop it here, and
			// inform the listening clients to remove the server from their render list.
			// in the next step then we create a new server.
			mySyphon.stop();
		}
		mySyphon = new JSyphonServer();
		mySyphon.test();
		mySyphon.initWithName(theName);

		// copy to texture, to send to Syphon.
		texID = new int[1];

		gl.glGenTextures(1, texID, 0);
		gl.glBindTexture(gl.GL_TEXTURE_RECTANGLE_EXT, texID[0]);
		gl.glTexImage2D(gl.GL_TEXTURE_RECTANGLE_EXT, 0, gl.GL_RGBA8, width, height, 0, gl.GL_RGBA, gl.GL_UNSIGNED_BYTE, null);
	}

	void renderTexture(GL gl) {
		gl.glBindTexture(gl.GL_TEXTURE_RECTANGLE_EXT, texID[0]);
		gl.glCopyTexSubImage2D(gl.GL_TEXTURE_RECTANGLE_EXT, 0, 0, 0, 0, 0, width, height);
		mySyphon.publishFrameTexture(texID[0], gl.GL_TEXTURE_RECTANGLE_EXT, 0, 0, width, height, width, height, false);
	}
	//// end Syphon stuff
	*/
	//////////////////////////////// END OF SYPHON STUFF



	//ControlP5 cp5;
	//int myColorBackground = color(0, 0, 0);
	private static ControlWindow controlWindow;

	void destroyControls(ControlP5 cp5) {
	  cp5.dispose(); //dispose();
	  controlWindow.clear();
	}

	public ControlWindow getCW() {
	  if (controlWindow==null) {
		  System.out.println("VurfEclipse#getCW initialising controlWindow");
	  	  ControlP5 cp5 = getCP5();
	  	  //cp5.window().setLocation(1024, 0);
	  	  System.out.println("VurfEclipse#getCW about to do addControlWindow()");
		  controlWindow = cp5.addControlWindow("controlP5window", 300, 0, 800, 800, JAVA2D, 20);
		  System.out.println("VurfEclipse#getCW about to do hideCoordinates");
		  controlWindow.hideCoordinates();
		  System.out.println("VurfEclipse#getCW about to do setBackground");
		  controlWindow.setBackground(color(40));
		  
		  controlWindow.setLocation(800, 20);		//NOZSTOCK ADDITION
	  }
	  //if (controlWindow==null) setupControls(getCP5());
	  return controlWindow;
	}

	synchronized public ControlP5 getCP5() {
		if (cp5==null && enablecp5) {
			System.out.println("VurfEclipse#getCP5 creating new ControlP5..");
			cp5 = new ControlP5(this);
		}
		System.out.println("getCP5 returning " + cp5);
		return cp5;
	}

	void setupControls() {
		System.out.println("VurfEclipse#setupControls");

		getCW();
	}


	//@Override
	boolean resizeRenderer () {
		System.err.println("resizeRenderer() returning false");
		return false;
	}

	//boolean enablecp5 = false;
	public static boolean enablecp5 = true;//false;//true; //true;


	/////// Minim
	Minim minim;

	String dateStamp = dateStamp();
	String dateStamp () {
	 return year() + "-" + month() + "-" + day() + "-" + hour() + "-" + minute() + "-" + second();
	}

	//config settings
	int title_adjust = -50; //-100;
	int
		output_width =  								(hdRes ? 1920 : mdRes ? 1600 : projRes ? 1280 : ultrahiRes ? 1280 : hiRes ? 1024 : medRes ? 800 : 640),
		output_height = title_adjust + 	(hdRes ? 1080 : mdRes ? 900 :  projRes ? 960  : ultrahiRes ? 1024 : hiRes ? 768  : medRes ? 600 : 480);
		//output_width = 1280; int output_height = 1024;;;;
	//int output_width = hiRes ? 1280 : 800, output_height = hiRes? 1024 : 600;
	//int output_width = 1280, output_height = 1024;
	int desired_width = output_width; //(int)(output_width*1.5f);
	int desired_height = output_height; //(int)(output_height*1.5f);




	String gfx_mode = GLConstants.GLGRAPHICS;
	//String gfx_mode = P2D;

	//Frame f;// = new Frame(width,height);

	int[] texID;

	GLGraphics pgl;
	GL gl;
	GLGraphicsOffScreen offscreen;

	int lastSecond;

	static public final int global_fps = 60;

	boolean screenGrab = false;

	boolean enableStreams = true;
	boolean enableSequencer = true;

	//Scene sc;
	public static Project pr;

	//EventProcessor ep;

	//static PApplet APP;// = this;
	public static IOUtils io;

	//GwrxInterface gw;

	int gw_height = 0; //!enablecp5?300:0;

	/********
	* SETUP
	*********/

	public static void main(String args[]) {
	    //PApplet.main(new String[] { "--present", "vurfeclipse.VurfEclipse" });
		PApplet.runSketch(new String[] { "vurfeclipse.VurfEclipse" }, new VurfEclipse());
		//PApplet.runSketch(new String[] { "--present", "vurfeclipse.VurfEclipse" }, new VurfEclipse());
		//PApplet.
		//PApplet.main(new String[] { "vurfeclipse.VurfEclipse" });
	}

	int sizeCount = 0;
	@Override
	public void size(int w, int h, String gfx) {
		sizeCount++;
		if (sizeCount>=2) {
			System.out.println("size(): ignoring " + sizeCount + "th call so as not to trigger GL error.");
			return;
		}
		super.size(w,h,gfx);
	}

	int refCount = 0;
	
	@Override
	public void setup () {
		 refCount++;
		 APP.setApp(this);

		 
		 System.out.println(refCount + ": -------------==================== \\\\/URF/ ===================--------------");
		 System.out.println("Working Directory = " +
		     System.getProperty("user.dir"));
		 
		 /*if (refCount==1) {
			 System.out.println("returning from setup() because refCount is " + refCount); 
			 return;
		 }*/
		 
		 //if (enablecp5 && refCount==1) setupControls();
		 

		 //size(output_width, output_height + gw_height, gfx_mode);
		 System.out.println("Initialising size() at " + output_width + ", " + output_height + " using renderer " + gfx_mode);
		 this.size(output_width, output_height, gfx_mode); // + gw_height, gfx_mode);

		 if (frame != null) {
			 frame.removeNotify();
			 		frame.setVisible(false);
			    frame.setResizable(false);
			    frame.setLayout(new BorderLayout() /*{
			    	@Override public void {
			    		
			    		
			    	}
			    }*/);
			    frame.setLocation(0, 0);
			    //frame.setSize(output_width, output_height);
			    frame.addWindowStateListener(new WindowStateListener() {
			      public void windowStateChanged(WindowEvent arg0) {
			         System.out.println(arg0);
			      }
			   });
			 	frame.setUndecorated(true);

			   frame.setVisible(true);
			   frame.addNotify();
			   //frame.dispose();
			   //frame = null;
			   //frame.setUndecorated(true);
			   frame.setMenuBar(null);
		 }

	     this.delaySetup();


	     


		 //System.exit(1);

		 frameRate(global_fps);

		 if (fullscreen) {
			 fs = new FullScreen(this);
			 fs.enter();
		 }

		 ImageRepository.IR = new ImageRepository();

		 io = new IOUtils();

		 initialiseGraphics();

		 delaySetup();


		 //colorMode(ARGB);
		 colorMode(RGB, 255, 255, 255, 100);


		 cursor(CROSS);

		 noSmooth();
		 //tint(255);
		 noTint();

		 lastSecond = exportMode?0:millis();

		//// instantiate a project to display

		 //pr = new TestProject(desired_width, desired_height, gfx_mode);
		 //pr = new SimpleProject(desired_width, desired_height, gfx_mode);
		 //pr = new PsychNightProject(desired_width, desired_height, gfx_mode);
		 //pr = new PostProject(desired_width, desired_height, gfx_mode);
		 //pr = new NozstockProject(desired_width, desired_height, gfx_mode);

		 //pr = new AboveBoardsProject(desired_width, desired_height, gfx_mode);
		 System.out.println("Instantiating Project at " + desired_width + "x" + desired_height);
		 //pr = new KinectTestProject(desired_width, desired_height, gfx_mode);

		 //pr = new ParadoxProject(desired_width, desired_height, gfx_mode);
		 //pr = new SocioSukiProject(desired_width, desired_height, gfx_mode);
		 //pr = new MutanteProject(desired_width, desired_height, gfx_mode);
		 //pr = new FeralFestProject(desired_width, desired_height, gfx_mode);
		 //pr = new KinectTestProject(desired_width, desired_height, gfx_mode);
		 //pr = new MagicDustProject(desired_width, desired_height, gfx_mode);
		 //pr = new PharmacyProject(desired_width, desired_height, gfx_mode);
		 //pr = new TempSocioSukiVideoProject(desired_width, desired_height, gfx_mode);

		 //pr = new NewJourneyProject(desired_width, desired_height, gfx_mode);
		 
		 pr = new MinimalProject(desired_width, desired_height, gfx_mode);

		 System.out.println("Initialising " + pr);
		 pr.initialise();

		 //gw = new GwrxInterface(APP, pr);

		 //pr.setupControls();

		 if (exportMode) {
		   timeMillis = 500;
		   lastSecond = timeMillis;
		   noLoop();
		   while (true && frameCount< (60*global_fps)) {
		     System.out.println("exportMode: about to call redraw()");
		     draw();
		   }
		   exit();
		 }

		 System.out.println("Finished VurfEclipse setup(); handing off to draw()...");
		 this.ready = true;
		 //System.exit(0);
	}



	private void delaySetup() {
		 try {
			 int sleepTime = 1000;
			 System.out.println("Pausing for " + sleepTime + " milliseconds to wait for stuff to catch up..");
			 Thread.sleep(sleepTime);
		 } catch (Exception e) {
			 System.out.println("Caught " + e);
		 }
		 System.out.println("Finished pausing.");

	}
	private void initialiseGraphics() {
		 if (gfx_mode==GLConstants.GLGRAPHICS) {
			   System.out.println("Setting up in GLConstants.GLGRAPHICS mode, so have to do some funky GL shit..");

			   offscreen = new GLGraphicsOffScreen(this, width, height); //, true, 4);
			   offscreen.beginDraw();
			   offscreen.setDepthMask(true);
			   //offscreen.background(0);
			   offscreen.endDraw();
			   System.out.println("==== offscreen is " + offscreen);

			   pgl = (GLGraphics) g;
			   //pgl.beginDraw(); pgl.endDraw();
			   //gl = offscreen.gl;
			   //gl = pgl.gl;
			   // found these bits here! https://github.com/pixelpusher/CreativeCode/blob/master/SoundCircle/SoundCircle.pde
			   gl = pgl.beginGL();
			   pgl.gl.glDisable(GL.GL_DEPTH_TEST);

			   pgl.gl.setSwapInterval( 1 ); // use value 0 to disable v-sync
			   pgl.background(0);
			   pgl.endGL();
			   		   
			   System.out.println("..Finished funky GL shit.");
		 }
		 

		 //pgl = (PGraphicsOpenGL) g;
		 gl = pgl.gl;
		 if (syphon) {
		   initSyphon(gl, "Vurf");
		 }
	}

	/*******
	*
	* DRAW()
	*
	********/
	public int timeMillis;
	GLTextureWindow texWin;
	@Override
	public void draw () {
	//System.out.println("Draw!");
		
		if (!ready) return;
	
		/*if (texWin==null) {
			GLTextureWindow texWin = new GLTextureWindow(this, 0, 0, this.desired_width, this.desired_height);
			texWin.setTexture(offscreen.getTexture());
			texWin.init();
		}*/
		
	 timeMillis = (exportMode?timeMillis+=(1000/global_fps):millis());
	 if (exportMode)
	   System.out.println("For frameCount " + frameCount + ", got timeMillis " + timeMillis);

	 if (enableStreams)
	   pr.processStreams(timeMillis);

	if (enableSequencer)
		 pr.processSequencer(timeMillis);

	 //offscreen.beginDraw();

	 //pgl.beginGL();

	 if (frameCount>25) {	// skip rendering first 25 frames
	   pr.applyGL(offscreen, output_width, output_height);
	   //texWin.render();
	 }
	 //offscreen.endDraw();

	 /*if (gw!=null) {
	   gw.drawStuff(offscreen, output_height, gw_height); //gw_height);
	 }*/

	 if (syphon) drawSyphon (offscreen);

	 //pgl.endGL();

	 //screenGrab = true;
	 if (exportMode || screenGrab) { //exportMode) {
	   frameCount++;
	   saveImage();
	   screenGrab = false;
	 }
	 /*if (exportMode) {
	  //offscreen.saveFrame("ouptut/image-" + dateStamp + "-#####.tiff");
	  System.out.println("exportMode: saving");
	  saveincr ++;
	  offscreen.save("output/image-" + dateStamp + "-"+saveincr + ".tiff");
	  }*/
	}



	/*****
	*
	*
	* supporting methods
	*****/




	/*
	int saveincr = 0;
	void saveImage () {
	saveincr++;
	delay(100); // give the buffer enough time to catch up. 50 is too slow, 250 works
	loadPixels();
	PImage s = createImage(width, height, ARGB);
	s.loadPixels();
	for (int i = 0 ; i < pixels.length ; i++) {
	s.pixels[i] = pixels[i];//get(i, i*width);
	}
	s.updatePixels();
	//updatePixels();
	s.save("output/image"+saveincr+random(100000)+".jpg");
	}*/

	int saveincr = 0;
	void saveImage() {
	 saveincr++;
	 //saveFrame("output/image"+saveincr+"."+random(100000)+".tiff");
	 //saveFrame("output/image-" + dateStamp + "-" +saveincr+".tiff");
	 saveFrame("output/image-" + dateStamp + "-#####.png");
	}

	/*void fileSelectedProject (File selection) {
	 try {
	   if (selection!=null) {
	     destroyControls(getCP5());
	     pr.finish();
	     pr = null;
	     pr = Project.loadProject(selection.getCanonicalPath());
	     cp5 = new ControlP5(this);
	     setupControls(cp5);
	     pr.initialise(cp5);
	   }
	 }
	 catch (Exception e) {
	 }
	}*/

	private ControlP5 cp5;
	@Override
	public void keyPressed() {
	 try {
	   if (key == ' ') {
	     screenGrab = true;
	   }
	   else if (key=='/') {
	     enableStreams = !enableStreams;
	   }
	   /*else if (key=='l') {
	     //pr.cp5.destroy();
	     destroyControls(getCP5());
	     pr.finish();
	     //pr.dispose();
	     pr = null;
	     pr = Project.loadProject(); //selectInput("select a file"));

	     cp5 = new ControlP5(this);
	     setupControls(cp5);
	     pr.initialise();
	     //selectInput("Choose a project file to load", "fileSelectedProject");
	   } */
	   else {
	     pr.sendKeyPressed(key);
	   }
	 }
	 catch (Exception e) {
	 }
	}


	@Override
	public void dispose() {
	 pr.finish();
	}

	public synchronized PApplet getAPP() {
	 return APP.getApp();
	}

	public GLGraphicsOffScreen getStaticGLBuff(int width, int height) {
	 return new GLGraphicsOffScreen(APP.getApp(), width, height);
	}


	static public int makeColour(int r, int g, int b) {
	 return (255 << 24) | (r << 16) | (g << 8) | b;
	}
	static public int makeColour(int r, int g, int b, int a) {
	 return (a << 24) | (r << 16) | (g << 8) | b;
	}

	/*public PGraphics createBuffer (int width, int height, String mode) {
	return (PGraphics) createGraphics(w, h, gfx_mode);
	}*/
}
