package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class KaleidoFilter extends ShaderFilter {
  boolean flipHorizontal = true, flipVertical = true;
  static String shaderName = "KaleidoScope.xml";

  int d_value = 2;

  transient PShader glFilter;
  transient PGraphics t;

  public KaleidoFilter(Scene sc) {
    super(sc,shaderName);
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/

  public Filter nextMode() {
    if (flipHorizontal&&flipVertical) {
      flipVertical = false;
      glFilter.set("mirror_y",0);
    } else if (flipHorizontal&&!flipVertical) {
      flipHorizontal = false;
      flipVertical = true;
      glFilter.set("mirror_y",1);
      glFilter.set("mirror_x",0);
    } else if (!flipHorizontal&&flipVertical) {
      flipVertical = true;
      flipHorizontal = true;
      glFilter.set("mirror_y",1);
      glFilter.set("mirror_x",1);
    } else {
      println("wtf");
    }
    return this;
  }

  public boolean initialise() {
    // set up inital variables or whatevs
    //temp =  new int[sc.w*sc.h];

    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    glFilter = APP.getApp().loadShader(shaderName); //new GLTextureFilter(APP.getApp(), shaderName); //"Edges.xml");

    //if (glFilter.hasParameter("width")) glFilter.setParameterValue("width", sc.w);
    //if (glFilter.hasParameter("height")) glFilter.setParameterValue("height", sc.h);

    t = this.sc.host.createCanvas("/kaleido_buffer", this.getFilterLabel()).getSurf(); //new GLTexture(APP.getApp(),sc.w,sc.h);

    return true;
  }

  public void beginDraw() {
    /*src.loadPixels();
    src.loadTexture();
    out.loadTexture();*/
    //super.beginDraw();
    //if (t==null) t = new GLTexture(APP.getApp(),sc.w,sc.h);
    if (src==null) setInputCanvas(canvas_in);
    if (out==null) setOutputCanvas(canvas_out);
    if (glFilter==null) glFilter = APP.getApp().loadShader(shaderName); //new GLTextureFilter(APP.getApp(), shaderName);
  }
  public void endDraw() {
  }

  //int[] temp;
  public boolean applyMeatToBuffers() {
    //println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    t.image(src,0,0); //.getTexture());

    //t.filter(glFilter,out); // TODO POSTFX
    this.filter(glFilter, ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_out));

    // just copy src to out
    //out.image(src,0,0);
    //out.pixels = src.pixels;
    //int[] temp = new int[sc.w*sc.h];
    /*
    for (int x = 0 ; x < sc.w ; x++) {
      for (int y = 0 ; y < sc.h ; y++) {

        int src_x = x, src_y = y;
        //src_x = (x/2) % (sc.w/2);

        if (flipHorizontal &&
          x>=(int)sc.w/d_value)
          src_x = sc.w-x;
        else if (!flipHorizontal)
          src_x = x;
        else
          src_x = (int)(x) % ((int)sc.w/d_value);

        if (flipVertical &&
          y<(int)sc.h/d_value)
          src_y = (int)(y) % ((int)sc.h/d_value);
        else if (!flipVertical)
          src_y = y;
        else
          src_y = sc.h-y;//-1;

        int offset_src = (sc.w*src_y) + (src_x);
        int offset_dst = (sc.w*y) + x;
        temp[offset_dst] = src.pixels[offset_src];
      }
    }
    //out.pixels = temp;
    arrayCopy(temp, out.pixels);
    */
    return true;
  }


}
