package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class PhaseRGBFilter extends ShaderFilter {
  boolean flipHorizontal = true, flipVertical = true;
  static String shaderName = "PhaseRGB.xml";
  
  int d_value = 2;
    
  transient GLTextureFilter glFilter;
  transient GLTexture t;
    
  public PhaseRGBFilter(Scene sc) {
    super(sc,shaderName);
  }
  
  @Override
  public void setParameterDefaults() {
	  super.setParameterDefaults();
	  this.addParameter("rshift", new Float(1), 0.2f, 1.5f);
	  this.addParameter("gshift", new Float(1), 0.2f, 1.5f);
	  this.addParameter("bshift", new Float(1), 0.2f, 1.5f);
  }
}
