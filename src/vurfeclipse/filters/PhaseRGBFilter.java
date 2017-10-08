package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class PhaseRGBFilter extends ShaderFilter {
  boolean flipHorizontal = true, flipVertical = true;
  static String shaderName = "PhaseRGB.xml";
  
  int d_value = 2;
    
  transient PShader glFilter;
  transient PGraphics t;
    
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
