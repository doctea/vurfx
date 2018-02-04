package vurfeclipse;

import spout.*;

import processing.core.*;
import controlP5.*;

//import javax.media.j3d.*;

import vurfeclipse.projects.*;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.user.projects.*;
import vurfeclipse.user.projects.TestProject;
//import codeanticode.glgraphics.*;
import ddf.minim.*;
import oscP5.OscMessage;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.*;

//import javax.media.opengl.*;
import processing.opengl.*;
import ch.bildspur.postfx.builder.*;
import ch.bildspur.postfx.pass.*;
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

	public Spout spout;

	void initSpout(String name) {
		spout = new Spout(this);
		spout.createSender("VurFX");
		spout.createReceiver("VurFX");
	}

	public void drawSpout(PGraphics p) {
		spout.sendTexture(p);
	}

	private static ControlFrame controlFrame;

	//synchronized 
	public ControlFrame getCF() {
		if (controlFrame==null && enablecp5) {
			System.out.println("VurfEclipse#getCP5 creating new ControlP5..");
			//cp5 = new ControlP5(this);
			controlFrame = new ControlFrame(this, 1280, 800, "Vurfx Controls");
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
	Minim minim;

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

	private int config_width = 1280; //1920
	private PVector config_aspect = RES_16_9;	// RES_16_9 

	int output_width = (int)this.getOutputResolution().x;
	int output_height= (int)this.getOutputResolution().y;

	int desired_width 	= output_width; //(int)(output_width*1.5f);
	int desired_height 	= output_height; //(int)(output_height*1.5f);

	//int[] texID;

	/*GLGraphics pgl;
	GL gl;*/
	Canvas offscreen;

	int lastSecond;

	static public final int global_fps = 60;

	private static final PVector RES_4_3 	= new PVector( 4 ,  3 );
	private static final PVector RES_16_9 	= new PVector( 16,  9 );
	private static final PVector RES_16_10 	= new PVector( 16, 10 );

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

		if (this.args!=null) {
			List<String> args = Arrays.asList(this.args);
			System.out.println("Passed command line arguments: " + Arrays.deepToString(this.args));
			if (args.contains("fullscreen")) {
				println("Setting fullscreen = true from commandline switch!");
				this.fullscreen = true;
			}
			if (args.contains("fullscreen_num")) {
				this.fullscreen_num = Integer.parseInt(args.get(args.indexOf("fullscreen_num")+1));
				println("Setting fullscreen_num = " + this.fullscreen_num + " from commandline switch!");
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
		println("Instantiating Project at " + desired_width + "x" + desired_height);

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

		pr = Project.chooseProject(desired_width, desired_height, vurfeclipse.user.projects.FeralFestProject.class);
		//pr = Project.chooseProject(desired_width, desired_height, "saves/FeralFestProject-test.xml");
		//pr = Project.chooseProject(desired_width, desired_height, "output/SavedProject2017-12-22-20-27-41.xml"); // not a bad one --> output/SavedProject2017-12-22-20-10-39.xml");
		//pr = Project.chooseProject(desired_width, desired_height, "output/SavedProject-NYE.xml");

		//pr = Project.chooseProject(desired_width, desired_height, vurfeclipse.user.projects.MutanteProject.class);
		//pr = Project.chooseProject(desired_width, desired_height, "output/MutanteProject-incremental.xml");


		PVector resolution = this.getOutputResolution();

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

		///frame.setLocation(500, 0);

		println("About to call getCF() in " + this + "#setup()");
		getCF(); // start up control frame
		println("Finished getCF() call!");
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

		println("Finished VurfEclipse setup(); handing off to draw()...");
		this.finishedSetup = true;
		//this.ready = true;
		//System.exit(0);
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
		if (syphon) {
			initSpout("Vurf");
		}

		// https://stackoverflow.com/questions/20551224/how-to-enable-vsync-synchronization-in-processing-2-x
		PJOGL pgl = (PJOGL)beginPGL();
		//pgl.gl.getGLProfile().
		pgl.gl.setSwapInterval(1);
		//pgl.presentX = 1920;
		//pgl.presentY = 1080;
		//pgl..hei
		endPGL();

		hint(DISABLE_DEPTH_TEST);

		frameRate(60);

		//println("initialiseGraphics() setting up PostFX");
		//setFxs(new PostFXSupervisor(this, output_width, output_height));
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
	//GLTextureWindow texWin;
	@Override
	public void draw () {
		//System.out.println("Draw!");
		if (!isReady()) {
			//println("Not yet isReady!()");
			return;
		} /*else {
			println("is ready!");
		}*/

		if (getFxs()==null) {
			setFxs(new PostFXSupervisor(this,output_width,output_height)); //.setResolution(output_width, output_height); //, 1920, 1080)); //output_width, output_height));
			//this.setSize(desired_width, desired_height);
			//this.g.init(APP.getApp().displayWidth, APP.getApp().displayHeight, ARGB);	// important !
			this.g.init(output_width, output_height, ARGB);	// important !
		}

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
		if (offscreen==null) 
			offscreen = pr.getCanvas("/out"); //, this.sketchWidth(), this.sketchHeight()); //output_width, output_height); //pr.createCanvas("/out", "Main out");

		if (frameCount>25) {	// skip rendering first 25 frames
			offscreen.getSurf().imageMode(CENTER);
			pr.applyGL(offscreen, output_width, output_height);
			//pr.applyGL(offscreen, this.displayWidth, this.displayHeight); //.displayWidth(), this.doHeight());
			//texWin.render();
		}
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
		PGraphics p = new PGraphics();
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
		if (this.pr.isDisableKeys()) {
			println("keys are disabled, ignoring " + key);
			return;
		}
		try {
			if (key == ' ') {
				screenGrab = true;
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
	

	public void oscEvent (OscMessage theOscMessage) {
		println("got oscmessage " + theOscMessage);
	}

}
