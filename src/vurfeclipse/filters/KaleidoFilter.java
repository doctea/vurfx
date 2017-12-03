package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class KaleidoFilter extends ShaderFilter {
  boolean flipHorizontal = true, flipVertical = true;
  int d_value = 2;

  //transient PShader glFilter;
  //transient PGraphics t;

  public KaleidoFilter(Scene sc) {
    super(sc,"KaleidoScope.glsl");
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
  
  @Override
  public void setParameterDefaults () {
	    super.setParameterDefaults();

	    addParameter("mirror_x", new Boolean(false));
	    addParameter("mirror_y", new Boolean(false));

	    addParameter("half_x", new Boolean(false));
	    addParameter("half_y", new Boolean(false));
  }

  public Filter nextMode() {
    if (flipHorizontal&&flipVertical) {
      flipVertical = false;
      //glFilter.set("mirror_y",0);
      this.changeParameterValue("mirror_y", false);
    } else if (flipHorizontal&&!flipVertical) {
      flipHorizontal = false;
      flipVertical = true;
      this.changeParameterValue("mirror_y", true);
      this.changeParameterValue("mirror_x", false);
      //glFilter.set("mirror_y",1);
      //glFilter.set("mirror_x",0);
    } else if (!flipHorizontal&&flipVertical) {
      flipVertical = true;
      flipHorizontal = true;
      //glFilter.set("mirror_y",1);
      //glFilter.set("mirror_x",1);
      this.changeParameterValue("mirror_y", true);
      this.changeParameterValue("mirror_x", true);
    } else {
      println("wtf");
    }
    return this;
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
