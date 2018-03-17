package vurfeclipse.filters;


import ch.bildspur.postfx.pass.Pass;
import processing.core.PApplet;
import processing.opengl.PShader;
import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;

public class BlendDrawer extends ShaderFilter {

  int rotation = 0;

  int x = 0, y = 0;
  int w, h;

  int currentBlendMode = 4;

  static String blendModes[] = {
    "BlendColor.glsl",
    "BlendLuminance.glsl",
    "BlendMultiply.glsl",
    "BlendSubtract.glsl",
    "BlendAdd.glsl",
    "BlendColorDodge.glsl",
    "BlendColorBurn.glsl",
    "BlendDarken.glsl",
    "BlendLighten.glsl",
    "BlendDifference.glsl",
    "BlendInverseDifference.glsl",
    "BlendExclusion.glsl",
    "BlendOverlay.glsl",
    "BlendScreen.glsl",
    //"BlendHardLight.xml",
    "BlendSoftLight.glsl",
    "BlendUnmultiplied.glsl",
    "BlendPremultiplied.glsl"
  };

  transient PShader blendFilters[] = new PShader[blendModes.length];


  //transient PShader glFilter;
  //transient PGraphics t;


  public BlendDrawer(Scene sc) {
    super(sc,blendModes[4]);
    this.w = sc.w;
    this.h = sc.h;
  }
  public BlendDrawer(Scene sc, int x, int y, int w, int h) {
    this(sc);
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  public BlendDrawer(Scene sc, int w, int h) {
	  this(sc);
	  this.w = w;
	  this.h = h;
  }
  BlendDrawer(Scene sc, int rotation) {
    this(sc);
    this.rotation = rotation;
  }
  public BlendDrawer(Scene sc, int x, int y, int w, int h, int rotation) {
    this(sc, x, y, w, h);
    this.rotation = rotation;
  }


  public void setParameterDefaults () {
    super.setParameterDefaults();
    //println("setting defaults");
    //this.changeParameterValue("Opacity", 1.0);
    //this.changeParameterValue("BlendMode", 4);
    addParameter("Rotate", new Integer(0), 0, 360);

    addParameter("X", new Float(0.0f), -1.0f, 1.0f);
    addParameter("Y", new Float(0.0f), -1.0f, 1.0f);
    addParameter("Scale", new Float(1.0f), 0.0f, 4.0f);

    addParameter("BlendMode", 4, 0, blendModes.length-1);

    addParameter("Opacity", 1.0f, 0.0f, 1.0f);

  }

  public void updateParameterValue(String paramName, Object value) {
    if (paramName.equals("BlendMode"))
      this.setBlendMode((Integer)value);
    else {
      glFilter = getFilterNumber(this.currentBlendMode);
      super.updateParameterValue(paramName, value);
    }
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/

  public void setBlendMode(int n) {
    this.currentBlendMode = n;
    //changeParameterValue("BlendMode", n);
  }

  synchronized public PShader getFilterNumber(int n) {
    if (this.blendFilters==null) blendFilters = new PShader[blendModes.length];
    n = n % blendModes.length;
    if (this.blendFilters[n]==null && n<this.blendFilters.length) {
      println("BlendDrawer#getFilterNumber(n) initialising GLTextureFilter " + blendModes[n]);
      this.blendFilters[n] = APP.getApp().loadShader(blendModes[n]); //new PShader(APP.getApp(),blendModes[n]);
      this.blendFilters[n].init();
    }
    return this.blendFilters[n];
  }

  public boolean initialise() {
    super.initialise();
    // set up inital variables or whatevs

    //this.glFilter = new GLTextureFilter(APP,blendModes[currentBlendMode]);//"BlendColor.xml");
    /*GLTextureParameters params = new GLTextureParameters();
    params.wrappingU = GLTextureParameters.REPEAT;
    params.wrappingV = GLTextureParameters.REPEAT;*/
    //this.t = new GLTexture(APP.getApp(),sc.w,sc.h,params);
    //this.t = new PImage(sc.w,sc.h);
    //this.t = new PGraphics();
    //t.setSize(this.w, this.h);

    for (int i = 0 ; i < blendModes.length ; i++) {
      getFilterNumber(i);
    }

    return true;
  }

  synchronized public boolean applyMeatToBuffers() {
    //println("in applymeattobuffers in blenddrawer (" + this + "), src is " + src);

	 //if(true)return true;
    //src = sc.host.getCanvas(this.canvas_in).getSurf();
    //out = sc.host.getCanvas(this.canvas_out).getSurf();

    //Shader tf = getFilterNumber(currentBlendMode);
    //glFilter = tf;
    //tf.set("bottomSampler", out);
    //tf.set("topSampler", src);
    //this.shaderFragName = "blend mode " + currentBlendMode;
    customPass = (CustomPass) this.getPassForBlendMode(currentBlendMode); //Shader(tf,out,src);
    customPass.shader.set("bottomSampler", out());
    customPass.shader.set("topSampler", in());
    customPass.shader.set("Opacity", new Float((Float)this.getParameterValue("Opacity")));
    
    //tf.set("bottomSampler", out);
    //tf.set("topSampler",  src);
    //tf.apply(new PImage[]{src, out}, t); // all are called the same way
    //t.shader(tf);
    //println("Applying shader " + currentBlendMode + " " + tf.toString() + " to " + out.toString());
    
    temporary_canvas.getSurf().beginDraw();
    if (true) {	// this version is faster
        temporary_canvas.getSurf().image(in(),0,0,sc.w,sc.h);	// WORKING 2017-10-29 //draw what's currently in the output buffer onto the temporary output buffer
    	//c.getSurf().resetShader();
        temporary_canvas.getSurf().shader(customPass.shader);
    } else {
    	this.filter(in(), customPass, temporary_canvas.getSurf()); //c.getSurf()); //c.getSurf(), tf);	// filter the temporary input buffer using src as input
    }
    //this.filter(src, tf, out); //c.getSurf()); //c.getSurf(), tf);	// WORKING 2017-1022
	//customPass.shader.set(shaderFragName, out);
    //this.filter(src, customPass, c.getSurf()); //c.getSurf()); //c.getSurf(), tf);	// filter the temporary input buffer using src as input
    temporary_canvas.getSurf().endDraw();
    
	//if (true) return super.applyMeatToBuffers();
	out().beginDraw();

    // image draw mode
    //out.getTexture().blend();
    //out.setBlendMode(REPLACE);
    //tint(128);
    //out.image(src.getTexture(),x,y,w,h);
    /*if (t!=null)
      t.clear(0);*/
    out().pushMatrix();

    //println ("x and y are " + (Float)getParameterValue("X") + "," +  (Float)getParameterValue("Y"));

    out().translate(w/2, h/2);
    out().translate(
    		w*(Float)getParameterValue("X"),
    		h*(Float)getParameterValue("Y")
    );
    
    //float x = (Float)getParameterValue("X");

    //if ((Float)getParameterValue("Zoom")!=1.0f) {
    	out().scale((Float)getParameterValue("Scale"));
    //}
    /*x = (int) PApplet.map(((Float)getParameterValue("X")),-2.0f,2.0f,-2*w,2*w);
    y = (int) PApplet.map(((Float)getParameterValue("Y")),-2.0f,2.0f,-2.0f*h,2.0f*h);*/

    rotation = (Integer) getParameterValue("Rotate");
    if (rotation!=0) {
        out().rotate(PApplet.radians(rotation));
    }


    /*out.color(this.random(255));
    out.fill(this.random(255));
    out.rect(random(sc.w), random(sc.h), random(100), random(100));*/
    
    
    

    int im = out().imageMode;// to restore imageMode
    //out.image(t,x,y,w,h);
    out().imageMode(out().CENTER);
    //out.image(t,x,y);
    out().image(temporary_canvas.getSurf(),
    		0,0
    		,w,h	// added to try and support hi-res display of blobs 2015-07-12
    		//w * (Float)getParameterValue("X"),
    		//h * (Float)getParameterValue("Y")
    );


    out().imageMode(im);
    

    
    //if (rotation!=0) {
      out().popMatrix();
    //}
      //out.ellipse(random(sc.w), random(sc.h), random(100), random(100));
      


      out().endDraw();
    // pixel copy mode
      //arrayCopy(src.pixels, out.pixels);

    return true;
  }

  /*public String getFilterLabel() {
    return super.getFilterLabel() + " [" + currentBlendMode + "]:" + blendModes[currentBlendMode];
  }*/
  /*public String toString() {
    return super.toString() + " " + blendModes[currentBlendMode];
  }*/

  private Pass getPassForBlendMode(int currentBlendMode) {
	// TODO Auto-generated method stub
	return this.getPassForShader(this.getFilterNumber(currentBlendMode), out(), in());
  }

  public Filter nextMode () {
    currentBlendMode++;
    if(this.currentBlendMode>=blendModes.length)
      currentBlendMode = 0;

    changeParameterValue("BlendMode", currentBlendMode);

    return this;
    //println("Switched to currentBlendMode " + currentBlendMode + " " + blendModes[currentBlendMode]);
  }
  
	public void beginDraw () {
		//out().beginDraw();
	}
	public void endDraw () {
		//out().endDraw();
	}


}
