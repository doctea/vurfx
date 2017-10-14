package vurfeclipse.filters;

import ch.bildspur.postfx.PostFXSupervisor;
import ch.bildspur.postfx.builder.PostFX;
import ch.bildspur.postfx.pass.Pass;
import ch.bildspur.postfx.pass.SobelPass;
import processing.core.PGraphics;
import processing.core.PVector;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import processing.opengl.*;



public class ShaderFilter extends Filter {

  int mode = 0;
  String shaderName;
  transient Canvas c;

  public ShaderFilter(Scene sc, String shaderName) {
    super(sc);
    this.shaderName = shaderName;
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/

  @Override
  synchronized public void updateParameterValue(String paramName, Object value) {
		//if (!this.parameters.containsKey(paramName)) this.addParameter(paramName, value);
		super.updateParameterValue(paramName, value);
		if (glFilter!=null) {
			//println("glFilter hasParameter("+paramName+") returns " + glFilter.hasParameter(paramName));
			//System.exit(1);
	
			Object current = getParameterValue(paramName);
	
			if (current instanceof Float ) {
				//println("setting GLFilter parameter " + paramName + " " + value);
				glFilter.set(paramName, ((Float)value).floatValue());
			} else if (current instanceof Integer) {
				//println("setting GLFilter parameter " + paramName + " " + value);
				glFilter.set(paramName, ((Integer)value).intValue());
			} else {
				println("ShaderFilter#updateParameterValue doesn't know what to do with passed value for " + paramName);
			}
		}
    //return this;
  }

  public void initShader(String shaderName) {
	  glFilter = APP.getApp().loadShader(shaderName); //new GLTextureFilter(APP.getApp(), shaderName);

	  //if (glFilter.hasParameter("width")) glFilter.setParameterValue("width", sc.w);
	  //if (glFilter.hasParameter("height")) glFilter.setParameterValue("height", sc.h);

  }

  PShader glFilter;
  public boolean initialise() {
    // set up inital variables or whatevs
    //temp = new int[sc.w*sc.h];
    pixelCount = sc.w*sc.h;


    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    //glFilter = new GLTextureFilter(APP.getApp(), shaderName); //"Edges.xml");
    initShader(shaderName);

    /*GLTextureParameters params = new GLTextureParameters();
    params.wrappingU = GLTextureParameters.REPEAT;
    params.wrappingV = GLTextureParameters.REPEAT;*/
    //t = new GLTexture(APP.getApp(),sc.w,sc.h, params);
    c = this.sc.host.createCanvas("/shaderfilter/"+this.getFilterName(), this.getFilterLabel());

    return true;
  }

  public Filter nextMode() {
    mode++;
    if(mode>4) mode = 0;
    initShader(shaderName);
    this.updateAllParameterValues();
    return this;
  }

  int a_bitshift = 255<<24;

  int[] temp;


  boolean autoChange = false;
  int autoCount = 0;
  int autoLimit = 5;

  int pixelCount;
  public boolean applyMeatToBuffers() {
    //t.copy(src.getTexture());
	c.getSurf().image(src,0,0);

    //t.filter(glFilter,out);//.getTexture());	// TODO POSTFX
    this.filter(c, glFilter, out);

    return true;
  }

  SobelPass sobelPass = new SobelPass(APP.getApp());
  protected void filter(Canvas source, PShader shader, PGraphics out) {
	  PostFXSupervisor fxs = ((VurfEclipse)APP.getApp()).getFxs();
	  fxs.render(source.getSurf());
	  fxs.pass(sobelPass);
	  fxs.compose(out);
  }

  protected void filter(Canvas source, PShader shader) {
	// TODO Auto-generated method stub
	  PostFXSupervisor fxs = ((VurfEclipse)APP.getApp()).getFxs();
	  fxs.render(source.getSurf());
	  //TODO: ADD REAL SHADER HERE
	  //fxs.pass(sobelPass);
	  //fxs.pass(new Pass
	  this.applyPass(fxs,glFilter);
	  fxs.compose(out);
  }
  
  public void applyPass(PostFXSupervisor fxs, PShader shader) {
	  PGraphics pass = fxs.getNextPass();
	  fxs.clearPass(pass);
	  pass.beginDraw();
	  pass.shader(shader);
	  pass.image(fxs.getCurrentPass(), 0, 0);
	  pass.endDraw();
  }

  public void beginDraw() {
	  //super.beginDraw();
	    /*src.loadPixels();
	    src.loadTexture();
	    out.loadTexture();*/
	    //super.beginDraw();

	    //if (t==null) {
	        /*GLTextureParameters params = new GLTextureParameters();
	        params.wrappingU = GLTextureParameters.REPEAT;
	        params.wrappingV = GLTextureParameters.REPEAT;*/
	    	//t = new GLTexture(APP.getApp(),sc.w,sc.h,params);
	    	//t = this.sc.host.createCanvas("/shaderfilter/"+this.getFilterName(), this.getFilterLabel()).getSurf();
	    //}
	    if (src==null) setInputCanvas(canvas_in);
	    if (out==null) setOutputCanvas(canvas_out);
	    //if (glFilter==null) glFilter = APP.getApp().loadShader(shaderName); //new GLTextureFilter(APP.getApp(), shaderName);
  }
  
  public void endDraw() {
	  //super.endDraw();
    //out.loadPixels(); // makes no difference apparently
    //out.updatePixels(); // stops form working
    //out.loadTexture(); // this line stops this from working...
  }

}
