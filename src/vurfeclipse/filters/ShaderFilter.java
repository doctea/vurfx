package vurfeclipse.filters;

import java.util.HashMap;

import ch.bildspur.postfx.PostFXSupervisor;
import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.builder.PostFX;
import ch.bildspur.postfx.pass.Pass;
import ch.bildspur.postfx.pass.SobelPass;
import processing.core.PGraphics;
import processing.core.PVector;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.ShaderFilter.CustomPass;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.scenes.Scene;
import vurfeclipse.user.scenes.OutputFX1;
import processing.opengl.*;


public class ShaderFilter extends Filter {

	class CustomPass implements Pass
	{
	  PShader shader;

	  public CustomPass(PShader shader) //, String shaderFragName, String shaderVertName)
	  {
		//System.out.println("Instantiated new CustomPass " + shader + " (told it was " + shaderFragName + " and " + shaderVertName);
	    this.shader = shader;//loadShader("negateFrag.glsl");
	  }

	  @Override
	  public void prepare(Supervisor supervisor) {
	    // set parameters of the shader if needed
	  }

	  @Override
	  public synchronized void apply(Supervisor supervisor) {
	    PGraphics pass = supervisor.getNextPass();
	    supervisor.clearPass(pass);

	    pass.beginDraw();
	    pass.shader(shader);
	    pass.image(supervisor.getCurrentPass(), 0, 0, sc.w, sc.h);
	    pass.endDraw();
	  }
	}
	
	
  int mode = 0;
  String shaderFragName;
  String shaderVertName;
  
  transient Canvas c;
  transient protected CustomPass customPass;

  public ShaderFilter(Scene sc, String shaderFragName, String shaderVertName) {
    super(sc);
    this.shaderFragName = shaderFragName;
    this.shaderVertName = shaderVertName;
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/

  public ShaderFilter(Scene sc, String shaderFragName) {
	  this(sc,shaderFragName,"");
  }

@Override
  synchronized public void updateParameterValue(String paramName, Object value) {
		//if (!this.parameters.containsKey(paramName)) this.addParameter(paramName, value);
		super.updateParameterValue(paramName, value);
		if (glFilter!=null) {
			//println("glFilter hasParameter("+paramName+") returns " + glFilter.hasParameter(paramName));
			//System.exit(1);
	
			Object current = getParameterValue(paramName);
	
			//glFilter.set(paramName, ((Class<? extends current.getClass()>) getParameter(paramName)).cast(value), value); //
			Parameter p = this.getParameter(paramName);
			if (p.getDataType()==Integer.class) {
				glFilter.set(paramName, (Integer) value);
			} else if (p.getDataType()==Float.class || p.getDataType()==Double.class) {
				if (value instanceof Float) {
					glFilter.set(paramName, ((Float)value).floatValue());
				} else if (value instanceof Double) {
					glFilter.set(paramName, ((Double)value).floatValue());
				}
			} else if (p.getDataType()==Boolean.class) {
				glFilter.set(paramName, (Boolean) value);
			} else {
				println(this + "#updateParameterValue("+paramName+","+value+") doesn't know what to do with " + value + " when setting gl shader uniforms?");
			}
			
			/*if (current instanceof Float ) {
				//println("setting GLFilter parameter " + paramName + " " + value);
				glFilter.set(paramName, ((Float)value).floatValue());
			} else if (current instanceof Integer) {
				//println("setting GLFilter parameter " + paramName + " " + value);
				glFilter.set(paramName, ((Integer)value).intValue());
			} else {
				println("ShaderFilter#updateParameterValue doesn't know what to do with passed value for " + paramName + " (is a " + paramName.getClass() + ")");
			}*/
		}
    //return this;
  }

  public void initShader(String shaderFragName, String shaderVertName) {
	  println("initShader("+shaderFragName+","+shaderVertName+")");
	  if (shaderVertName!="")
		  glFilter = APP.getApp().loadShader(shaderFragName,shaderVertName); //new GLTextureFilter(APP.getApp(), shaderName);
	  else
		  glFilter = APP.getApp().loadShader(shaderFragName);
	  
	  //glFilter.bind();	// force compilation when loaded to save hassle later
	  //glFilter.unbind();
	  glFilter.init();

	  glFilter.set("src_tex_unit0", src);
	  glFilter.set("dest_tex_size_x", (float)sc.w);
	  glFilter.set("dest_tex_size_y", (float)sc.h);
	  glFilter.set("dest_tex_size",  new PVector((float)sc.w,(float)sc.h));
	  customPass = this.getPassForShader(glFilter,out,src); //, shaderFragName, shaderVertName);
	  //glFilter.set("bottomSampler", out);
	  
	  //if (glFilter.hasParameter("width")) glFilter.setParameterValue("width", sc.w);
	  //if (glFilter.hasParameter("height")) glFilter.setParameterValue("height", sc.h);

  }
  
  transient private HashMap<PShader,Pass> passes = new HashMap<PShader,Pass>();
  
  protected CustomPass getPassForShader(PShader tf, PGraphics out, PGraphics src) {
	  CustomPass p = (CustomPass) this.passes.get(tf);
	  if (p==null) {
		p = new CustomPass(tf); //,"blend mode ,"blend mode..!");
	    this.passes.put(tf,p);
	  }
	  return p;
  }

  @Override public boolean start() {
	  super.start();
	  this.updateAllParameterValues();
	return true;
  }
  
  transient PShader glFilter;
  public boolean initialise() {
    // set up inital variables or whatevs
    //temp = new int[sc.w*sc.h];
    pixelCount = sc.w*sc.h;


    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    //glFilter = new GLTextureFilter(APP.getApp(), shaderName); //"Edges.xml");
    initShader(shaderFragName, shaderVertName);

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
    //initShader(shaderFragName,shaderVertName);
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
	//c.getSurf().image(src,0,0);

	//c.getSurf().rect(400, 20, 20, 400);
	  
    //t.filter(glFilter,out);//.getTexture());	// TODO POSTFX
	//println("About to apply " + this.shaderFragName);
	customPass.shader.set("src_tex_unit0", src);
	c.getSurf().beginDraw();
    this.filter(src/*c.getSurf()*/, customPass, c.getSurf()); //out);
    c.getSurf().endDraw();
    
	out.beginDraw();
	out.image(c.getSurf(),0,0,sc.w,sc.h);
	/*out.color(255,128,96);
	out.rect(0, 0, 50, 50);
    out.rect(50, 50, 100, 100);*/
    //println("out is " + out);
    out.endDraw();

    return true;
  }

  transient SobelPass sobelPass = new SobelPass(APP.getApp());
  protected void filter(PGraphics source, Pass pass, PGraphics out) {
	  PostFXSupervisor fxs = ((VurfEclipse)APP.getApp()).getFxs();
	  //TODO: ADD REAL SHADER HERE
	  //fxs.pass(sobelPass);
	  //fxs.pass(new Pass
	  /*source.beginDraw();
	  source.sphere(60);
	  source.text(this.getFilterName() + ": " + shader, sc.w/2, sc.h/2);
	  source.endDraw();*/
	  fxs.render(source);
	  //this.applyPass(fxs,glFilter);
	  //println("doing filter for " + this.shaderFragName);
	  fxs.pass(pass);
	  //fxs.pass(new CustomPass(shader)); //glFilter));
	  //fxs.pass(sobelPass);
	  fxs.compose(out);
  } 
  
  @Deprecated
  protected void filter(PGraphics source, PShader shader, PGraphics out) {
	  filter(source,new CustomPass(shader),out);
	  PostFXSupervisor fxs = ((VurfEclipse)APP.getApp()).getFxs();
	  //TODO: ADD REAL SHADER HERE
	  //fxs.pass(sobelPass);
	  //fxs.pass(new Pass
	  /*source.beginDraw();
	  source.sphere(60);
	  source.text(this.getFilterName() + ": " + shader, sc.w/2, sc.h/2);
	  source.endDraw();*/
	  fxs.render(source);
	  //this.applyPass(fxs,glFilter);
	  //fxs.pass(new CustomPass(shader)); //glFilter));
	  fxs.pass(customPass);
	  //fxs.pass(sobelPass);
	  fxs.compose(out);
  }

  @Deprecated
  protected void filter(PGraphics source, PShader shader) {
	  filter(source, shader, out);
  }
  
  public void applyPass(PostFXSupervisor fxs, PShader shader) {
	  PGraphics pass = fxs.getNextPass();
	  //fxs.clearPass(pass);
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
