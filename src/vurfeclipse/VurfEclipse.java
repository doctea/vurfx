package vurfeclipse;

//import spout.*;

import processing.core.*;
import controlP5.*;

//import javax.media.j3d.*;

import vurfeclipse.projects.*;
import vurfeclipse.ui.ControlFrame;
//import codeanticode.glgraphics.*;
//import ddf.minim.*;

import java.awt.Color;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.udojava.evalex.AbstractFunction;
import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.ExpressionException;

//import javax.media.opengl.*;
import processing.opengl.*;
import ch.bildspur.postfx.*;


public class VurfEclipse extends PApplet {
	private PostFXSupervisor fxs;

	///// SYPHON STUFF (choose one - disabled stuff or enabled stuff)

	// DISABLED SYPHON BLOCK (stubs)
	boolean syphon = true;	// also used to turn on spout !

	public void drawSyphon(PGraphics offscreen) {};
	//public void initSyphon(GL gl, String theName) {};

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


	////// SPOUT STUFF

	/*public Spout spout;

	void initSpout(String name) {
		spout = new Spout(this);
		spout.createSender("VurFX");
		spout.createReceiver("VurFX");
	}

	public void drawSpout(PGraphics p) {
		spout.sendTexture(p);
	}*/

	private static ControlFrame controlFrame;

	//synchronized 
	public ControlFrame getCF() {
		if (controlFrame==null && enablecp5) {
			System.out.println("VurfEclipse#getCP5 creating new ControlP5..");
			//cp5 = new ControlP5(this);
			controlFrame = new ControlFrame(this, 1280, 1024, "Vurfx Controls");
			//controlFrame.setup();
			//surface.setLocation(20, 20);
		}
		//println("getCF() returning " + controlFrame);
		return controlFrame;
	}

	void setupControls() {
		println("VurfEclipse#setupControls()");
		getCF();
	}


	//@Override
	boolean resizeRenderer () {
		System.err.println("resizeRenderer() returning false");
		return false;
	}

	//boolean enablecp5 = false;
	public static boolean enablecp5 = true;//false;//true; //true;



	/////// Minim
	//Minim minim;

	String dateStamp = dateStamp();
	public String dateStamp () {
		return year() + "-" + month() + "-" + day() + "-" + hour() + "-" + minute() + "-" + second();
	}

	//config settings

	// select resolution
	boolean hdReady = true;

	boolean hdRes = false;//true;
	boolean mdRes = false; //true; //true;
	boolean projRes = false;
	boolean ultrahiRes = false;
	boolean medHiRes = true;
	boolean hiRes = true; //true;
	boolean medRes = false; //true;
	boolean lowRes = false;
	// all false for really low res 

	boolean fullscreen = false;//true;//false;
	int fullscreen_num = 2;

	int title_adjust = -20; //-100;	// amount to take off the height to compensate for window title, system bar etc
	/*int
		output_width =  (hdRes ? 1920 : mdRes ? 1600 : projRes ? 1280 : ultrahiRes ? 1280 : medHiRes ? 1080 : hiRes ? 1024 : medRes ? 800 : 640),
		output_height = (hdRes ? 1080 : mdRes ? 900 :  projRes ? 720  : ultrahiRes ? 1024 : medHiRes ?  720 : hiRes ? 768  : medRes ? 600 : 480) 
						+ (fullscreen?0:title_adjust);*/

	private int config_width = 1024; //1280; //1920
	private PVector config_aspect = RES_4_3; //RES_16_9;	// RES_16_9 

	int output_width = (int)this.getOutputResolution().x;
	int output_height= (int)this.getOutputResolution().y;

	//int desired_width 	= output_width; //(int)(output_width*1.5f);
	//int desired_height 	= output_height; //(int)(output_height*1.5f);

	//int[] texID;

	/*GLGraphics pgl;
	GL gl;*/
	Canvas offscreen;

	int lastSecond;

	static public final int global_fps = 60;

	private static final PVector RES_1_1 	= new PVector( 1 ,  1 );
	private static final PVector RES_4_3 	= new PVector( 4 ,  3 );
	private static final PVector RES_16_9 	= new PVector( 16,  9 );
	private static final PVector RES_16_10 	= new PVector( 16, 10 );

	public static final int MOUSE_LEFT = 37;
	public static final int MOUSE_RIGHT = 39;
	public static final int MOUSE_MIDDLE = 3;

	boolean screenGrab = false;

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
		//PApplet.runSketch(new String[] { "vurfeclipse.VurfEclipse" }, new VurfEclipse());
		//PApplet.runSketch(new String[] { "--present", "vurfeclipse.VurfEclipse" }, new VurfEclipse());
		//PApplet.
		//PApplet.main(new String[] { "vurfeclipse.VurfEclipse" });
		PApplet.main("vurfeclipse.VurfEclipse",args);
	}

	int sizeCount = 0;
	@Override
	public void size(int w, int h, String gfx) {
		sizeCount++;
		if (sizeCount>=2) {
			println("size(): ignoring " + sizeCount + "th call so as not to trigger GL error.");
			//System.exit(1);
			return;
		} else {
			println("size(): Passing size call number " + sizeCount);
		}
		super.size(w,h,gfx, System.getProperty("user.dir"));
	}

	int refCount = 0;
	private boolean finishedSetup;

	private int config_x=-1;
	private int config_y=-1;

	private int config_control_x;

	@Override
	public String sketchPath() {
		return System.getProperty("user.dir") + "/output/";		
	}
	@Override
	public String sketchPath(String where) {
		return sketchPath() + where;		
	}

	@Override
	public String dataPath(String file) {
		return this.dataPath() + "/" + file;		
	}
	public String dataPath() {
		return System.getProperty("user.dir") + "/bin/data";
	}


	@Override
	public void settings () {
		APP.setApp(this);

		System.out.println(refCount + ": -------------==================== \\\\/URF/ [1] settings() ===================--------------");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		System.out.println("sketch directory:" + this.sketchPath());

		String choice = "";
		
		if (this.args!=null) {
			List<String> args = Arrays.asList(this.args);
			System.out.println("Passed command line arguments: " + Arrays.deepToString(this.args));
			if (args.contains("-fullscreen")) {
				println("Setting fullscreen = true from commandline switch!");
				this.fullscreen = true;
			}
			if (args.contains("-fullscreen_num")) {
				this.fullscreen_num = Integer.parseInt(args.get(args.indexOf("-fullscreen_num")+1));
				println("Setting fullscreen_num = " + this.fullscreen_num + " from commandline switch!");
			}
			if (args.contains("-load")) {
				choice = args.get(args.indexOf("-load")+1);
				println("Loading file / class " + choice);
			}
			if (args.contains("-width")) {
				config_width = Integer.parseInt(args.get(args.indexOf("-width")+1));
				println("got resolution width " + config_width);
			}
			if (args.contains("-aspect43")) {
				config_aspect = RES_4_3;
			} else if (args.contains("-aspect169")) {
				config_aspect = RES_16_9;
			} else if (args.contains("-aspect")) {
				config_aspect = RES_16_10;
			} else if (args.contains("-aspect11")) {
				config_aspect = RES_1_1;
			}
			if (args.contains("-x")) {
				config_x = Integer.parseInt(args.get(args.indexOf("-x")+1));
			}
			if (args.contains("-y")) {
				config_y = Integer.parseInt(args.get(args.indexOf("-y")+1));
			}
			if (args.contains("-control_x")) {
				config_control_x = Integer.parseInt(args.get(args.indexOf("-control_x")+1));
			}
			
			if (args.contains("-nodraw")) {
				this.enableRendering = false;
			}
		}

		//size(output_width, output_height + gw_height, gfx_mode);

		boolean enableDebugStream = false;
		if (enableDebugStream) {
			System.out.println("Enabling DebugStream to capture System.err output");
			DebugStream.activate();
		}

		this.delaySetup();

		//System.exit(1);


		ImageRepository.IR = new ImageRepository();

		io = new IOUtils();

		//// instantiate a project to display
		println("Instantiating Project at " + this.getOutputResolution()); //desired_width + "x" + desired_height);

		//pr = new TestProject(desired_width, desired_height, gfx_mode);
		//pr = new SimpleProject(desired_width, desired_height, gfx_mode);
		//pr = new PsychNightProject(desired_width, desired_height, gfx_mode);
		//pr = new PostProject(desired_width, desired_height, gfx_mode);
		//pr = new NozstockProject(desired_width, desired_height, gfx_mode);

		//pr = new AboveBoardsProject(desired_width, desired_height, gfx_mode);
		//pr = new KinectTestProject(desired_width, desired_height, gfx_mode);

		//pr = new ParadoxProject(desired_width, desired_height, gfx_mode);
		//pr = new SocioSukiProject(desired_width, desired_height, gfx_mode);

		//pr = new MutanteProject(desired_width, desired_height);
		//pr = Project.bootProject(desired_width,  desired_height, "saves/FeralFestProject-test.xml");
		//pr = Project.bootProject(desired_width,  desired_height, "saves/SavedProject2017-12-17-21-58-53.xml");

		//pr = new FeralFestProject(desired_width, desired_height);
		//pr = new KinectTestProject(desired_width, desired_height, gfx_mode);
		//pr = new MagicDustProject(desired_width, desired_height, gfx_mode);
		//pr = new PharmacyProject(desired_width, desired_height, gfx_mode);
		//pr = new TempSocioSukiVideoProject(desired_width, desired_height, gfx_mode);

		//pr = new TestProject(desired_width, desired_height);

		//pr = new NewJourneyProject(desired_width, desired_height, gfx_mode);

		//pr = new MinimalProject(desired_width, desired_height, gfx_mode);
		
		//pr = Project.chooseProject(desired_width, desired_height, vurfeclipse.user.projects.FeralFestProject.class);
		//pr = Project.chooseProject(desired_width, desired_height, "saves/FeralFestProject-test.xml");
		//pr = Project.chooseProject(desired_width, desired_height, "output/SavedProject2017-12-22-20-27-41.xml"); // not a bad one --> output/SavedProject2017-12-22-20-10-39.xml");
		//pr = Project.chooseProject(desired_width, desired_height, "output/SavedProject-NYE.xml");

		//pr = Project.chooseProject(desired_width, desired_height, vurfeclipse.user.projects.MutanteProject.class);
		//pr = Project.chooseProject(desired_width, desired_height, "output/MutanteProject-incremental.xml");
		
		if (choice.equals("")) {
			choice = "vurfeclipse.user.projects.FeralFestProject.class";
			println("No choice of project made!!! Setting default project to '" + choice + "'");
		}

		PVector resolution = this.getOutputResolution();
		
		int desired_width = (int) resolution.x, desired_height = (int) resolution.y;
		output_width = desired_width; output_height = desired_height;
		pr = Project.chooseProject(desired_width, desired_height, choice);


		if (fullscreen) {
			//((PGraphicsOpenGL)this.offscreen.getSurf()).updatePixelSize();
			println("going fullscreen on " + fullscreen_num);
			//this.setSize(output_width, output_height);
			//this.setSize(desired_width, desired_height);
			this.setSize(APP.getApp().displayWidth, APP.getApp().displayHeight);
			this.fullScreen(P3D, fullscreen_num);
			//this.g.init(desired_width, gw_height, ARGB);
			//
		} else {
			println("Initialising size() at " + output_width + ", " + output_height + " using renderer"); //" + gfx_mode);
			this.size(desired_width, desired_height, P3D);//(output_width, output_height, P3D); //, gfx_mode); // + gw_height, gfx_mode);
		}
		

		System.out.println("Finished VurfEclipse#settings() - handing off to setup!");
	}

	private PVector getOutputResolution() {
		PVector r = getOutputResolution(this.config_width, this.config_aspect); //RES_4_3);
		println("getOutputResolution returning " + r);
		return r;
	}

	private PVector getOutputResolution(int width, PVector aspect) {
		PVector p = new PVector();
		p.x = width;
		p.y = width * (aspect.y/aspect.x);

		return p;
	}
	
	public float getAspectX() {
		return this.config_aspect.x / this.config_aspect.y;
	}
	


	private void delaySetup() {
		if (false)
			try {
				int sleepTime = 1000;
				System.out.println("Pausing for " + sleepTime + " milliseconds to wait for stuff to catch up..");
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				System.out.println("Caught " + e);
			}
		System.out.println("Finished pausing.");
	}
	private void initialiseGraphics() {	// called from setup()
		/*if (gfx_mode==GLConstants.GLGRAPHICS) {
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
		 }*/

		//offscreen = pr.createCanvas("/out", "Main out");

		//pgl = (PGraphicsOpenGL) g;
		/*gl = pgl.gl;
		 if (syphon) {
		   initSyphon(gl, "Vurf");
		 }*/
		/*if (syphon) {
			initSpout("Vurf");
		}*/

		// https://stackoverflow.com/questions/20551224/how-to-enable-vsync-synchronization-in-processing-2-x
		//frameRate(1000);
		PJOGL pgl = (PJOGL)beginPGL();
		pgl.gl.setSwapInterval(1);
		endPGL();

		//hint(DISABLE_DEPTH_TEST);
		hint(ENABLE_DEPTH_SORT);

		//frameRate(60);
		

		//println("initialiseGraphics() setting up PostFX");
		//setFxs(new PostFXSupervisor(this, output_width, output_height));
	}


	@Override
	public void setup () {	// was public void setup() {
		refCount++;
		println(refCount + ": -------------==================== \\\\/URF/ [2] setup() ===================--------------");

		//this.g.setSize(1920,1080); //desired_width/2, desired_height);
		//this.g.setSize(Screen.getMainScreen().getWidth(), Screen.getMainScreen().getHeight());


		/*if (refCount==1) {
			 System.out.println("returning from setup() because refCount is " + refCount); 
			 return;
		 }*/

		//if (enablecp5 && refCount==1) setupControls();

		//pr.setupSequencer();
		//pr.initialiseScenes();
		



		pr.initialise();

		//delaySetup();
		pr.initialiseScenes();

		pr.initialiseStreams();

		//System.out.println("about to call setupControls on " + pr.toString());
		//pr.setupControls(getCF());


		//frameRate(global_fps);

		/*if (fullscreen) {
			 fs = new FullScreen(this);
			 fs.enter();
		 }*/
		initialiseGraphics();

		//this.getGraphics().setSize(1920, 1080);//.scale(1.5f);
		//println("mat is: " + mat.);
		//mat.scale(config_aspect.y / config_aspect.x);
		//this.getGraphics().setMatrix(mat);

		//this.setSize(output_width, output_height);

		//colorMode(ARGB);
		colorMode(RGB, 255, 255, 255, 100);

		cursor(CROSS);

		//noSmooth();
		//tint(255);
		noTint();

		//System.out.println("Initialising " + pr);
		//pr.initialise();

		if (getFxs()==null) {
			setFxs(new PostFXSupervisor(this,output_width,output_height)); //.setResolution(output_width, output_height); //, 1920, 1080)); //output_width, output_height));
			//this.setSize(desired_width, desired_height);
			//this.g.init(APP.getApp().displayWidth, APP.getApp().displayHeight, ARGB);	// important !
			this.g.init(output_width, output_height, ARGB);	// important !
		}
		
		if (offscreen==null) 
			offscreen = pr.getCanvas("/out"); //, this.sketchWidth(), this.sketchHeight()); //output_width, output_height); //pr.createCanvas("/out", "Main out");

		
		offscreen.getSurf().imageMode(CENTER);

		println("Finished VurfEclipse setup(); handing off to draw()...");
		this.finishedSetup = true;
		//this.ready = true;
		//System.exit(0);

		println("About to call getCF() in " + this + "#setup()");
		getCF(); // start up control frame
		println("Finished getCF() call!");
	}


	public boolean isReady() {
		return ( this.finishedSetup &&		//false	&& /// false to debug without drawing ! 
				(this.pr!=null && this.pr.isInitialised()));
	}


	/*******
	 *
	 * DRAW()
	 *
	 ********/
	public int timeMillis;

	private boolean moved = false;
	
	private boolean enableRendering = true;
	//GLTextureWindow texWin;
	@Override
	public void draw () {
		
		/*if (!moved && config_x>=0) {
			println("set location!");
			
			APP.getApp().controlFrame.queueUpdate(new Runnable() {

				@Override
				public void run() {
					frame.setLocation(config_x, config_y);
					
				}
			});
			moved = true;
		}*/
		
		//System.out.println("Draw!");
		if (!isReady()) {
			//println("Not yet isReady!()");
			return;
		} /*else {
			println("is ready!");
		}*/


		/*if (texWin==null) {
			GLTextureWindow texWin = new GLTextureWindow(this, 0, 0, this.desired_width, this.desired_height);
			texWin.setTexture(offscreen.getTexture());
			texWin.init();
		}*/

		//timeMillis = (exportMode?timeMillis+=(1000/global_fps):millis());
		timeMillis = millis();

		//if (enableStreams)
		pr.processStreams(timeMillis);

		//if (enableSequencer)
		pr.processSequencer(timeMillis);

		//offscreen.beginDraw();

		//pgl.beginGL();
		//if (offscreen==null) 
			//offscreen = pr.getCanvas("/out"); //, this.sketchWidth(), this.sketchHeight()); //output_width, output_height); //pr.createCanvas("/out", "Main out");

		//	if (frameCount>25) {	// skip rendering first 25 frames
			//offscreen.getSurf().imageMode(CENTER);
		if (this.isEnableRendering()) {
			pr.applyGL(offscreen, output_width, output_height);
		}
			//pr.applyGL(offscreen, this.displayWidth, this.displayHeight); //.displayWidth(), this.doHeight());
			//texWin.render();
		//}
		//offscreen.endDraw();

		/*if (gw!=null) {
		   gw.drawStuff(offscreen, output_height, gw_height); //gw_height);
		 }*/

		//this.imageMode(0);
		this.background(0);	// was it always thus?
		//this.imageMode(CORNERS);
		//offscreen.getSurf().imageMode(CENTER);
		//this.setSize(APP.getApp().displayWidth, APP.getApp().displayHeight);
		//this.g.scale(0.25f);//0.8f); //1.5f);
		//this.g.scale(0.75f);

		//this.image(offscreen.getSurf(), 0, 0, APP.getApp().sketchWidth(), APP.getApp().sketchHeight()); //output_width, output_height);	// actually draw to applet!
		//this.image(offscreen.getSurf(), 0, 0, APP.getApp().displayWidth, APP.getApp().displayHeight); //output_width, output_height);	// actually draw to applet!
		if (fullscreen) {	// inside draw loop, have to draw differently if we want to centre the output properly!
			//this.getGraphics().setSize(APP.getApp().sketchWidth(), APP.getApp().sketchHeight());
			//this.getGraphics().setSize(APP.getApp().displayWidth, APP.getApp().displayHeight);
			//offscreen.getSurf().setSize(APP.getApp().sketchWidth(), APP.getApp().sketchHeight());
			//this.getGraphics().setSize(output_width, output_height);
			//this.getGraphics().setMatrix((PMatrix2D)null);//.getMatrix().transpose();
			//println ("rendering at " + APP.getApp().displayWidth + "x" + APP.getApp().displayHeight);
			this.image(offscreen.getSurf(), 0, 0, APP.getApp().displayWidth, APP.getApp().displayHeight); //
			//this.image(offscreen.getSurf(), 0, 0, output_width, output_height);	// actually draw to applet!
		} else {
			this.image(offscreen.getSurf(), 0, 0, output_width, output_height);	// actually draw to applet!
		}
		//this.resetMatrix();
		//this.g.init(desired_width, desired_height, ARGB);

		//this.image(offscreen.getSurf(), 0, 0, 1920, APP.getApp().displayHeight); //output_width, output_height);	// actually draw to applet!
		//this.applyMatrix(offscreen.getSurf().getMatrix());
		//this.mask(this.get());//offscreen.getSurf());
		//this.image(offscreen.getSurf(), 0, 0, output_width, output_height);	// actually draw to applet!
		//this.applyMatrix((PMatrix3D)offscreen.getSurf().getMatrix().scale(1.5f);
		//this.scale(1.5f);
		//this.g.scale(1.f);
		//this.pi
		//this.image(offscreen.getSurf(), 0, 0, output_width, output_height);	// actually draw to applet!


		if (syphon) drawSyphon (offscreen.getSurf());
		//if (spout!=null) drawSpout(offscreen.getSurf());

		//pgl.endGL();

		//screenGrab = true;
		if (/*exportMode ||*/ screenGrab) { 
			frameCount++;
			saveImage();
			screenGrab = false;
		}
	}


	private boolean isEnableRendering() {
		return this.enableRendering;
	}
	/*****
	 * supporting methods
	 *****/

	int saveincr = 0;
	void saveImage() {
		saveincr++;
		//saveFrame("output/image"+saveincr+"."+random(100000)+".tiff");
		//saveFrame("output/image-" + dateStamp + "-" +saveincr+".tiff");
		//saveFrame("output/image-" + dateStamp + "-#####.png");
		String filename = pr.getClass().getSimpleName() + "-image-" + dateStamp() + "-" + saveincr + ".png";
		filename = /*this.sketchOutputPath() + "/" +*/ filename;
		println ("saving screenshot to " + filename);
		this.offscreen.getSurf().get().save(filename);
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

	public boolean restEnabled = false;
	@Override
	public void keyPressed() {
		handleKey(key);
	}


	@Override
	public void dispose() {
		pr.finish();
	}

	public synchronized PApplet getAPP() {
		return APP.getApp();
	}

	public PGraphics getStaticGLBuff(int width, int height) {
		PGraphics p = new PGraphicsOpenGL();
		p.setSize(width, height);;
		return p;
		//return new PGraphics(APP.getApp(), width, height);
	}


	static public int makeColour(int r, int g, int b) {
		return (255 << 24) | (r << 16) | (g << 8) | b;
	}
	static public int makeColour(int r, int g, int b, int a) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public PostFXSupervisor getFxs() {
		return fxs;
	}

	public PostFXSupervisor setFxs(PostFXSupervisor fxs) {
		this.fxs = fxs;
		return this.getFxs();
	}

	/**
	 * @author Jeeeyul 2011. 11. 1.
	 * @since M1.10
	 * https://jeeeyul.wordpress.com/2012/10/18/make-system-out-println-rocks/
	 */
	public static class DebugStream extends PrintStream {
		private static final DebugStream INSTANCE = new DebugStream();

		public static void activate() {
			System.setErr(INSTANCE);
		}

		private DebugStream() {
			super(System.err);
		}

		@Override
		public void println(Object x) {
			showLocation();
			super.println(x);
		}

		@Override
		public void println(String x) {
			super.println("----");
			super.println("original error output: \"" + x + "\"");
			showLocation();
		}

		private void showLocation() {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			StackTraceElement last = trace[3]; //= Thread.currentThread().getStackTrace()[3];
			super.print("caught error output at ");
			super.print(last);
			for (StackTraceElement element : Arrays.copyOfRange(trace,3,trace.length)) {	    	  
				//super.print(MessageFormat.format("({0}:{1, number,#}) : ", element.getFileName(), element.getLineNumber()) + " " + element);
				super.print(element);
				//System.out.print(trace);
				super.println();
			}
			super.println("----");
		}
	}

	public void handleKey(char key) {
		if (this.pr.isDisableKeys() && key!='!') {
			println("keys are disabled, ignoring '" + key + "'");
			return;
		} else if (key=='!') {
			this.pr.disableKeys = false;
		}
		try {
			if (key == ' ') {
				screenGrab = true;
			} else if (key == '*') {		// debug -- try to restart the control panel if its fucked up!
				this.controlFrame.stop();
				this.controlFrame.control().papplet.exit();
				this.controlFrame.exit();
				this.controlFrame = null;
				this.setupControls();
			}
			/*else if (key=='/') {
			     enableStreams = !enableStreams;
			   }*/
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
			println("keyPressed Caught exception " + e);
			e.printStackTrace();
		}		
	}

	// callback for selectInput() ? - DOESNT WORK ?
	public void loadSnapshot(String filename) {
		this.pr.loadSnapshot(filename);
	}

	//PGraphics rcvd = this.createPrimaryGraphics();
	/*public PImage getSpoutImage() {
		this.spout.receiveTexture(rcvd);
		return rcvd;
	}*/

	public Color createDefaultColorFromName(final String name) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			
			byte[] thedigest = md.digest(name.getBytes());
			String dig = "";//new String(thedigest);
			for (int i = 0 ; i < thedigest.length ; i++) {
				//thedigest[i] = new Byte(Byte.toString(thedigest[i]));//.byteAt(0);
				dig += Integer.toHexString(thedigest[i]);
			}

		    String md5 = "#" + dig.substring(0, 6); //new String(thedigest).substring(0, 6);
		    Color defaultColor = Color.decode(md5);
		    int darkness = ((defaultColor.getRed() * 299) + (defaultColor.getGreen() * 587) + (defaultColor.getBlue() * 114)) / 1000;
		    if (darkness > 125) {
		        defaultColor = defaultColor.darker();
		    }
		    return defaultColor;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public Expression makeEvaluator(String expression) {
			com.udojava.evalex.Expression e = new com.udojava.evalex.Expression(expression);
			e.addFunction(eval_function_rgb);
			
			e.addFunction(eval_function_rgbi);
		return e;
	}
	
	static AbstractFunction eval_function_rgb = new AbstractFunction("rgb", -1) {
		@Override
		public BigDecimal eval(List<BigDecimal> parameters) {
			if (parameters.size() <= 0) {
				throw new ExpressionException("rgb requires at least 1 parameter (ideally 4)");
			}
			float r = 0.0f, g = 0.0f, b = 0.0f, a = 1.0f;
			r = parameters.get(0).floatValue();
			
			if (parameters.size()>3) {
				a = parameters.get(3).floatValue();
			} else {
				a = r;
			}
			if (parameters.size()>2) {
				b = parameters.get(2).floatValue();
			} else {
				b = r;
			}
			if (parameters.size()>1) {
				g = parameters.get(1).floatValue();
			} else {
				g = r;
			}
			
			//println("called rgb with "  + r + ","+g+","+b + "a, returning " + APP.getApp().color(r*255, g*255, b*255, a*255));
			
			return new BigDecimal( APP.getApp().color(r*255, g*255, b*255, a*255));
		}
	};
	
	static AbstractFunction eval_function_rgbi = new AbstractFunction("rgbi", -1) {
		@Override
		public BigDecimal eval(List<BigDecimal> parameters) {
			if (parameters.size() <= 0) {
				throw new ExpressionException("rgb requires at least 1 parameter (ideally 4)");
			}
			float r = 0.0f, g = 0.0f, b = 0.0f, a = 1.0f;
			r = parameters.get(0).floatValue();
			
			if (parameters.size()>3) {
				a = parameters.get(3).floatValue();
			} else {
				a = r;
			}
			if (parameters.size()>2) {
				b = parameters.get(2).floatValue();
			} else {
				b = r;
			}
			if (parameters.size()>1) {
				g = parameters.get(1).floatValue();
			} else {
				g = r;
			}
			
			//println("called rgbi with "  + r + ","+g+","+b + "a, returning " + new Color (APP.getApp().color(r, g, b, a)).getRGB()); //APP.getApp().color(r, g, b, a));
			
			return new BigDecimal(new Color (APP.getApp().color(r, g, b, a)).getRGB());
		}
	};

	public String sketchOutputPath(String path) {
		return (sketchOutputPath()+"/").replace("null/","")+path.replace(".json", "");
	}

	static HashMap<String,PFont> fonts = new HashMap<String,PFont>(); 
	public PFont getFont(String fontName, int size) {
		if (!fonts.containsKey(fontName + "_" + size)) {
			fonts.put(fontName+"_"+size,createFont(fontName, size));
		}
		return fonts.get(fontName+"_"+size);
	}
	
	
	
	
	// cribbed from https://forum.processing.org/two/discussion/752/how-to-copy-a-pshape-object
/*	public PShape copyShape(PShape original){
		 
		  PShape copy_shape = createShape();
		  int nOfVertexes = original.getVertexCount();      
		  int nOfVertexesCodes = original.getVertexCodeCount();      
		  int code_index = 0; // which vertex i'm reading?
		  PVector pos[] = new PVector[nOfVertexes];
		  int codes[] = new int[nOfVertexes];
		 
		  println("nOfVertexes: "+nOfVertexes);
		  println("nOfVertexesCodes: "+nOfVertexesCodes);
		 
		  // creates the shape to be manipulated
		  // and initiate the codes array
		  beginShape();
		    for (int i=0; i< nOfVertexes; i++){
		      copy_shape.vertex(0,0);
		      codes[i] = 666; //random number, different than 0 or 1
		    }
		  endShape();
		 
		  // GET THE CODES
		  for (int i=0; i< nOfVertexesCodes; i++){
		    int code = original.getVertexCode(i);
		    codes[code_index] = code;
		    if (code == 0) {
		      code_index++;
		    } else if( code == 1){
		      code_index +=3;
		    }
		  }
		  // GET THE POSITIONS
		  for (int i=0; i< nOfVertexes; i++){
		    pos[i] = original.getVertex(i);
		  }
		  //for debugging purposes
		  println("==============POS==============");
		  printArray(pos);
		  println("==============CODES==============");
		  printArray(codes);
		 
		  copy_shape = createShape();
		  copy_shape.beginShape();
		  for (int i=0; i< nOfVertexes; i++){
		    if ( codes[i] == 0) {
		      //if a regular vertex
		      copy_shape.vertex(pos[i].x, pos[i].y);
		 
		    } else if ( codes[i]==1 ){
		       //if a bezier vertex
		       copy_shape.bezierVertex(pos[i].x, pos[i].y,
		                               pos[i+1].x, pos[i+1].y,
		                               pos[i+2].x, pos[i+2].y);
		 
		    } else {
		     //this vertex will be used inside the bezierVertex, wich uses 3 vertexes at once
		     println("skipping vertex "+i);
		    }
		  }
		  copy_shape.endShape();
		  return copy_shape;
		}*/
	public PShape copyShape(PShape original) {
		return PShapeAcess.copyShape(this, original);
	}
	
	public static class PShapeAcess extends PShape {
		  
		  public static PShape copyShape(PApplet parent, PShape shape) {
		    return createShape(parent, shape);
		  }
		  
	}


	
}
