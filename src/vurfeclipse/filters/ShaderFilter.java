package vurfeclipse.filters;


import processing.core.PVector;
import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class ShaderFilter extends Filter {
  
  int mode = 0;
  String shaderName;
  transient GLTexture t;
  
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
		if (value instanceof Float ) {
			//println("setting GLFilter parameter " + paramName + " " + value);
			glFilter.setParameterValue(paramName, ((Float)value).floatValue());
		} else if (value instanceof Integer) { 
			//println("setting GLFilter parameter " + paramName + " " + value);
			glFilter.setParameterValue(paramName, ((Integer)value).intValue());
		} else {
			println("ShaderFilter#updateParameterValue doesn't know what to do with passed value for " + paramName);
		}
	}
    //return this;
  }
    
  public void initShader(String shaderName) {
	  glFilter = new GLTextureFilter(APP.getApp(), shaderName);
	  
	  //if (glFilter.hasParameter("width")) glFilter.setParameterValue("width", sc.w);
	  //if (glFilter.hasParameter("height")) glFilter.setParameterValue("height", sc.h);
	  
  }
  
  GLTextureFilter glFilter;
  public boolean initialise() {
    // set up inital variables or whatevs 
    //temp = new int[sc.w*sc.h];
    pixelCount = sc.w*sc.h;
    
    
    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    //glFilter = new GLTextureFilter(APP.getApp(), shaderName); //"Edges.xml");
    initShader(shaderName);

    t = new GLTexture(APP.getApp(),sc.w,sc.h);
    
    return true;
  }
  
  public void nextMode() {
    mode++;
    if(mode>4) mode = 0;
    //initShader(shaderName);
    //this.updateAllParameterValues();
  }
  
  int a_bitshift = 255<<24;
  
  int[] temp;


  boolean autoChange = false;
  int autoCount = 0;
  int autoLimit = 5;

  int pixelCount;  
  public boolean applyMeatToBuffers() {
    t.copy(src.getTexture());
    
    t.filter(glFilter,out.getTexture());
    
    return true;
  }
  
  public void beginDraw() {
    //System.out.println("beginDraw in " + this);
  }
  public void endDraw() {
    //out.loadPixels(); // makes no difference apparently
    //out.updatePixels(); // stops form working
    //out.loadTexture(); // this line stops this from working...
  }
  
}
