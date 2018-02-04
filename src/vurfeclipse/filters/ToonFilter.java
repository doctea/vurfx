package vurfeclipse.filters;


import vurfeclipse.scenes.Scene;

public class ToonFilter extends ShaderFilter {
  public ToonFilter(Scene sc) {
    super(sc,"ToonFrag.glsl");
  }

  public boolean initialise() {
	  super.initialise();
	  float[] huelevels = new float[] { 0.0f, 80.0f, 160.0f, 240.0f, 320.0f, 360.0f };
	  float[] satlevels = new float[] { 0.0f, 0.15f, 0.3f, 0.45f, 0.6f, 0.8f, 1.0f };
	  float[] vallevels = new float[] { 0.0f, 0.3f, 0.6f, 1.0f };
	  glFilter.set("HueLevels", huelevels);//, huelevels.length);
	  glFilter.set("SatLevels", satlevels);//, satlevels.length);
	  glFilter.set("ValLevels", vallevels);//, vallevels.length);
	  return true;
  }
  
  /*public boolean initialise() {
    // set up inital variables or whatevs
    //temp =  new int[sc.w*sc.h];

    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    glFilter = APP.getApp().loadShader(shaderName); //new GLTextureFilter(APP.getApp(), shaderName); //"Edges.xml");

    //if (glFilter.hasParameter("width")) glFilter.setParameterValue("width", sc.w);
    //if (glFilter.hasParameter("height")) glFilter.setParameterValue("height", sc.h);

    t = this.sc.host.createCanvas("/kaleido_buffer", this.getFilterLabel()).getSurf(); //new GLTexture(APP.getApp(),sc.w,sc.h);

    return true;
  }*/

}
