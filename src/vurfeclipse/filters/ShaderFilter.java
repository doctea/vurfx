package vurfeclipse.filters;

import java.util.HashMap;
import java.util.Map;

import ch.bildspur.postfx.PostFXSupervisor;
import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.Pass;
import ch.bildspur.postfx.pass.SobelPass;
import controlP5.CallbackEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.scenes.Scene;
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

			//pass.setSize(sc.w, sc.h);
			//pass.setSize(1920, 1080); //sc.w, sc.h);
			//pass.res
			pass.beginDraw();
			pass.shader(shader);
			//pass.imageMode(PApplet.CORNERS);
			pass.image(supervisor.getCurrentPass(), 0, 0, sc.w, sc.h);
			pass.endDraw();
		}
	}


	@Override
	public HashMap<String,Object> collectFilterSetup() {	// for saving snapshots, save setup of filter
		HashMap<String,Object> output = super.collectFilterSetup();
		output.put("shaderFragName", this.shaderFragName);
		output.put("shaderVertName", this.shaderVertName);
		return output;
	}
	
	@Override
	public ShaderFilter readSnapshot(Map<String,Object> input) {
		super.readSnapshot(input);
		this.shaderFragName = (String) input.get("shaderFragName");
		this.shaderVertName = (String) input.get("shaderVertName");
		return this;
	}

	
	int mode = 0;
	String shaderFragName;
	String shaderVertName;

	transient Canvas temporary_canvas;
	transient protected CustomPass customPass;

	public ShaderFilter(Scene sc) {
		super(sc);
	}
	
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
				if (value instanceof Float || value instanceof Double) {
					glFilter.set(paramName, ((Float)value).intValue()); //(int)(Float.parseFloat(value.toString())));
				} else if (value instanceof Integer || value instanceof Long){
					glFilter.set(paramName, (Integer)value); //(int)Integer.(value.toString()));
				}
			} else if (p.getDataType()==Float.class || p.getDataType()==Double.class) {
				if (value instanceof Double) {
					glFilter.set(paramName, ((Double)value).floatValue());
				} else if (value instanceof Float) {
					/* non-working test of scaling zooms better to allow finer control 
					if (paramName.equals("offset_x")) { 
						println("offset_x" + value);
					}
					//value = Math.pow(10.0, (float)value / 1000.0);
					value = ((Double)Math.log10(Math.abs((float)value)*1000.0f)).floatValue()/1000.0f;///1000.0);
					if (paramName.equals("offset_x")) { 
						println("offset_x" + value + "\n---\n");
					}
					*/
					if (paramName.equals("offset_x")) {	// cheeky sync zoom hack .. 
					    // bizarrely this works to smooth it out quite a bit, but still isn't quite right... 
						println("offset_x" + value);
						value = ((Double)Math.pow(10.0,  ((float)value/4.0d))).floatValue();
						//value = ((Double)Math.log10((float)value)).floatValue();
						println("offset_x" + value);
					}


					glFilter.set(paramName, ((Float)value).floatValue());
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

	synchronized public void initShader(String shaderFragName, String shaderVertName) {
		println("initShader("+shaderFragName+","+shaderVertName+")");
		//out.init(sc.w, sc.h, APP.getApp().ARGB);//, format); //
		//out.beginDraw();
		//PGraphics oldg = APP.getApp().g;
		//APP.getApp().g = out;
		if (!shaderVertName.equals(""))
			glFilter = 
			APP.getApp(). 
			//out.
			loadShader(shaderFragName,shaderVertName); //new GLTextureFilter(APP.getApp(), shaderName);
		//new PGLShader(APP.getApp(), shaderFragName, shaderVertName);
		else
			glFilter = 
			APP.getApp().
			//out.
			loadShader(shaderFragName);
		//new PGLShader(APP.getApp(), shaderFragName);
		//APP.getApp().g = oldg;
		//out.endDraw();

		//glFilter = new PGLShader((PApplet)APP.getApp(), shaderFragName, shaderVertName);
		//if (!shaderFragName.equals("")) glFilter.setFragmentShader(shaderFragName);
		//if (!shaderVertName.equals("")) glFilter.setVertexShader(shaderVertName);

		//glFilter.bind();	// force compilation when loaded to save hassle later
		//glFilter.unbind();
		glFilter.init();

		glFilter.set("src_tex_unit0", in());
		glFilter.set("dest_tex_size_x", (float)sc.w);
		glFilter.set("dest_tex_size_y", (float)sc.h);
		glFilter.set("dest_tex_size",  new PVector((float)sc.w,(float)sc.h));
		customPass = this.getPassForShader(glFilter,out(),in()); //, shaderFragName, shaderVertName);
		//customPass = this.getPassForShader(glFilter,temporary_canvas.getSurf(),src); //, shaderFragName, shaderVertName);
		//glFilter.set("bottomSampler", out);

		//if (glFilter.hasParameter("width")) glFilter.setParameterValue("width", sc.w);
		//if (glFilter.hasParameter("height")) glFilter.setParameterValue("height", sc.h);

	}

	transient private HashMap<PShader,Pass> passes = new HashMap<PShader,Pass>();

	synchronized protected CustomPass getPassForShader(PShader tf, PGraphics out, PGraphics src) {
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
		super.initialise();
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
		temporary_canvas =
				this.sc.host.createCanvas(this.getPath()+"/tmp", this.getFilterLabel());
				//this.sc.host.createCanvas("/shaderfilter/"+this.getFilterName(), this.getFilterLabel());

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
	synchronized public boolean applyMeatToBuffers() {
		//t.copy(src.getTexture());
		//c.getSurf().image(src,0,0);

		//c.getSurf().rect(400, 20, 20, 400);
		
		if (customPass==null || customPass.shader==null) {
			println("null pass/shader in applyMeatToBuffers!");
			return false;
		}

		//t.filter(glFilter,out);//.getTexture());	// TODO POSTFX
		//println("About to apply " + this.shaderFragName);
		customPass.shader.set("src_tex_unit0", in());
		temporary_canvas.getSurf().beginDraw();
		
	    if (true) {	// this version is faster (when used in blenddrawer anyway? no proof its beneficial here)
	        temporary_canvas.getSurf().image(in(),0,0,sc.w,sc.h);	// WORKING 2017-10-29 //draw what's currently in the output buffer onto the temporary output buffer
	    	//c.getSurf().resetShader();
	        temporary_canvas.getSurf().shader(customPass.shader);
	    } else {
	    	this.filter(in(), customPass, temporary_canvas.getSurf()); //c.getSurf()); //c.getSurf(), tf);	// filter the temporary input buffer using src as input
	    }
		//this.filter(src/*c.getSurf()*/, customPass, temporary_canvas.getSurf()); //out);
		//c.getSurf().resetShader();
		//c.getSurf().shader(customPass.shader);
		temporary_canvas.getSurf().endDraw();

		PGraphics out = out();
		out.beginDraw();
		out.imageMode(APP.getApp().CORNERS);
		out.image(temporary_canvas.getSurf(),0,0,sc.w,sc.h);
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
		filter(source, shader, out());
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
		//if (src==null) setInputCanvas(canvas_in);
		//if (out==null) setOutputCanvas(canvas_out);
		//if (glFilter==null) glFilter = APP.getApp().loadShader(shaderName); //new GLTextureFilter(APP.getApp(), shaderName);
	}

	public void endDraw() {
		//super.endDraw();
		//out.loadPixels(); // makes no difference apparently
		//out.updatePixels(); // stops form working
		//out.loadTexture(); // this line stops this from working...
	}

}
