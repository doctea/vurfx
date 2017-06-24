//// Works in Processing 1.5.1.  Requires GLGraphics library (so currently does not work in Processing 2.0)

boolean hiRes = false;
boolean medRes = false;

boolean exportMode = false; //true;

//boolean syphon = true;
// JSyphon stuff+info at https://forum.processing.org/topic/syphon-integration-with-processing


import controlP5.*;
//ControlP5 cp5;
//int myColorBackground = color(0, 0, 0);
static ControlWindow controlWindow;

import processing.opengl.*;
import javax.media.opengl.GL;
import processing.video.*;
import codeanticode.glgraphics.*;

//boolean enablecp5 = false;
static boolean enablecp5 = true;

/////// JSyphon
//import jsyphon.*;
//JSyphonServer mySyphon;

/////// Minim
import ddf.minim.*;
Minim minim;

String dateStamp = dateStamp();
String dateStamp () {
  return year() + "-" + month() + "-" + day() + "-" + hour() + "-" + minute() + "-" + second();
}

// config settings
int
desired_width =  hiRes ? 1024 : medRes ? 800 : 640,
desired_height = hiRes ? 768  : medRes ? 600 : 480;

//int output_width = hiRes ? 1280 : 800, output_height = hiRes? 1024 : 600;
int output_width = 1280, output_height = 1024;


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

//Scene sc;
static Project pr;

//EventProcessor ep;

static PApplet APP;// = this;
static IOUtils io;

//GwrxInterface gw;

int gw_height = !enablecp5?300:0;

/********
 * SETUP
 *********/

void setup () {
  System.out.println("-------------==================== \\\\/URF/ ===================--------------");
  System.out.println("Initialising at " + desired_width + ", " + desired_height + " " + gfx_mode);
  size(output_width, output_height + gw_height, gfx_mode);
  frameRate(global_fps);

  if (enablecp5) {
    cp5 = new ControlP5(this);
    setupControls(cp5);
  }

  minim = new Minim(this);

  APP = this;

  IR = new ImageRepository();

  io = new IOUtils();

  if (gfx_mode==GLConstants.GLGRAPHICS) {
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
  }

  //pgl = (PGraphicsOpenGL) g;
  gl = pgl.gl;
  /*if (syphon) {
   initSyphon(gl, "Vurf");
   }*/


  //colorMode(ARGB);
  colorMode(RGB, 255, 255, 255, 100);


  cursor(CROSS);

  noSmooth();
  //tint(255);
  noTint();

  lastSecond = exportMode?0:millis();

  pr = new TestProject(desired_width, desired_height, gfx_mode);
  //pr = new SimpleProject(desired_width, desired_height, gfx_mode);
  pr = new PsychNightProject(desired_width, desired_height, gfx_mode);
  //pr = new PostProject(desired_width, desired_height, gfx_mode);
  pr.initialise(cp5);

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

  /*
  //sc = new KinectScene(desired_width, desired_height);
   sc = new DemoScene(desired_width, desired_height); //(ge);
   //sc = new SimpleScene(desired_width,desired_height); //(ge);
   //sc = new FeedbackScene(desired_width, desired_height);
   sc.initialise();*/

  //f = new Frame(width,height);

  //APP = this;
  // create layers that are gonna do the heavy lifting
  //
}



/*******
 *
 * DRAW()
 *
 ********/
int timeMillis;
void draw () {
  timeMillis = (exportMode?timeMillis+=(1000/global_fps):millis());
  if (exportMode)
    System.out.println("For frameCount " + frameCount + ", got timeMillis " + timeMillis);

  if (enableStreams)
    pr.processStreams(timeMillis);
  //offscreen.beginDraw();

  //pgl.beginGL();

  if (frameCount>25) {
    pr.applyGL(offscreen, output_width, output_height);
  }
  //offscreen.endDraw();

  /*if (gw!=null) {
    gw.drawStuff(offscreen, output_height, gw_height); //gw_height);
  }*/

  //GLTexture tex = offscreen.getTexture();
  //image(tex, 0, 0, output_width, output_height);
  //server.publishFrameTexture(tex.getTextureID(), tex.getTextureTarget(), 0, 0, tex.width, tex.height, tex.width, tex.height, false);
  //if (syphon)
  //renderTexture(gl);

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


//// Syphon stuff
/*void initSyphon(GL gl, String theName) {
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

void fileSelectedProject (File selection) {
  try {
    if (selection!=null) {
      destroyControls(cp5);
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
}

ControlP5 cp5;
void keyPressed() {
  try {
    if (key == ' ') {
      screenGrab = true;
    }
    else if (key=='/') {
      enableStreams = !enableStreams;
    }
    else if (key=='l') {
      //pr.cp5.destroy();
      destroyControls(cp5);
      pr.finish();
      //pr.dispose();
      pr = null;
      pr = Project.loadProject(); //selectInput("select a file"));

      cp5 = new ControlP5(this);
      setupControls(cp5);
      pr.initialise(cp5);
      //selectInput("Choose a project file to load", "fileSelectedProject");
    }
    else {
      pr.sendKeyPressed(key);
    }
  }
  catch (Exception e) {
  }
}


void dispose() {
  pr.finish();
}

static public PApplet getAPP() {
  return APP;
}

public GLGraphicsOffScreen getStaticGLBuff(int width, int height) {
  return new GLGraphicsOffScreen(APP, width, height);
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


