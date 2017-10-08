package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import processing.core.PGraphics;

public class MirrorFilter extends Filter {
  
  int numBlobs = 500;
  
  int offsetx=0, offsety=0;
  
  public MirrorFilter(Scene sc) {
    super(sc);
  }
  
  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
  transient PGraphics t;
  int[] temp;
  public boolean initialise() {
    // set up inital variables or whatevs 
    temp = new int[sc.w*sc.h];
    //t = new GLTexture(APP.getApp(),sc.w,sc.h);
    t = this.sc.host.createCanvas("/shaderfilter/"+this.getFilterName(), this.getFilterLabel()).getSurf();
    return true;
  }
  
  /*
  public void beginDraw() {
    //System.out.println("beginDraw in " + this);
    //src.updatePixels();
    //src.loadPixels(); 
    //if (out!=src) out.loadPixels();
    src.loadPixels();
    if (out!=src) out.loadPixels();
  }
  public void endDraw() {
    out.updatePixels();
    //out.loadTexture();
    //out.loadTexture();
    //out.loadTexture();
    //out.loadTexture();
    //out.updateTexture();
  }
  */
  
  public void applyMeatToBuffers_disabled() {
    
  }
  
  public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    
    // just copy src to out
    //out.image(src,0,0);
    //out.pixels = src.pixels;
    
    //int[] temp = new int[sc.w*sc.h]; // hmm disabling this line does an actual vertical mirror effect...?!
    
    /*src.loadTexture();
    int[] s = src.getTexture().pixels;*/
    
    //src.loadPixels();
    //src.loadTexture();
    
    /*
    for (int x = 0 ; x < sc.w-1 ; x++) {
      for (int y = 0 ; y < sc.h ; y++) {
        int offset_src = (int)(sc.w*y) + (sc.w-x-1) ; //-1);
        int offset_dst = (int)(sc.w*y) + x+1;
        temp[offset_dst] = src.getTexture().pixels[offset_src];
        //temp[offset_dst] = s[offset_src];
        if (temp[offset_dst]!=0) 
          System.out.println("got colour " + temp[offset_dst]);
      }
    }
    //temp = src.pixels;
    //out.pixels = temp;
    for (int p = 400 ; p < 2000 ; p++) {
      temp[p] = (int)random(255*255*255);
    }
    arrayCopy(temp, out.pixels);
    */
    
    //out.beginDraw();

    //GLTexture t = new GLTexture(APP,sc.w,sc.h);
    //t.copy(src.getTexture());
	t.image(src,0,0,sc.w,sc.h);
    
    out.pushMatrix();
    //out.scale(-1,1);
    Boolean flip_x = (Boolean)getParameterValue("mirror_x");
    Boolean flip_y = (Boolean)getParameterValue("mirror_y");
    out.scale(
      flip_x?-1:1, 
      flip_y?-1:1);

    //out.clear(random(255));
    out.noTint();
    out.image(t,
      flip_x? -out.width : 0, //out.width,
      flip_y? -out.height : 0
      //flip_y && !flip_x? -out.height : out.height
    );
    //out.image(t,((Boolean)getParameterValue("mirror_x")?out.width:0) - out.width,0);
    /*out.image(t,
      ((Boolean)getParameterValue("mirror_x")?out.width:-out.width),
      ((Boolean)getParameterValue("mirror_y")?out.height:-out.height) //* out.heig
    );*/
    //out.popMatrix();
    //out.fill(random(255));
    //out.rect(-100,100,100,100);
    out.popMatrix();

    //out.endDraw();
    //out.loadPixels();
    
    return true;
    
  }
  
  public void setParameterDefaults() {
    super.setParameterDefaults();
    addParameter("mirror_x", new Boolean(true)); //-1, -1, 1);
    addParameter("mirror_y", new Boolean(false)); //1, -1, 1);
  }
  
}
