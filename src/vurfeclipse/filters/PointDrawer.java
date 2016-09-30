package vurfeclipse.filters;

import vurfeclipse.Blob;
import vurfeclipse.scenes.Scene;


class PointDrawer extends Filter {

  int r = 25;

  int numBlobs = 4000;
  Blob blobs[] = new Blob[numBlobs];

  int offsetx=0,offsety=0;

  boolean tinted = false;

  boolean changePosition = true;

  PointDrawer(Scene sc) {
    super(sc);
  }

  public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }

  int[] src_temp;
  public boolean initialise() {
    // set up inital variables or whatevs
    for (int i = 0 ; i < numBlobs ; i++) {
      blobs[i] = new Blob();
    }

    src_temp = new int[sc.w*sc.h];
    return true;
  }

  public Filter nextMode() {
    r = (int)random(50);
    this.changePosition = (random(50)>25)?true:false;
    this.tinted = (random(50)>25)?true:false;
    //numBlobs = 10+(int)random(2000)/(r/2);
    numBlobs = 10 + (r*80);
    if (numBlobs>4000) numBlobs = 4000;
    for (int i = 0 ; i < numBlobs ; i++) {
      blobs[i].nextShape();
      blobs[i].setRadius(r);
      blobs[i].setScaleRelative(random(10)>5);//!blobs[i].scaleRelative;
      blobs[i].setRotation ((int)random(60));
      //blobs[i].edge = ((int)random(2)>1)?true:false; //!blobs[i].edge;//((int)random(2))==0?true:false;//!blobs[i].edge;
      blobs[i].setEdge(((int)random(10)>5?true:false));//!blobs[i].edge;
      blobs[i].setStrokeSize((int)random(5)+1);
      blobs[i].setTint((int)random(10)>5?0:128);
    }
    return this;
  }

  public void beginDraw() {
    // we copy from src so loadPixels / loadTexture
    //src.loadPixels();
    //src.loadTexture();
    //src.loadPixels();
    //src.loadTexture();
    //src.loadPixels();
    //src.loadPixels();
    //src.getTexture().loadPixels();
    //src.loadTexture();
    out.beginDraw(); // need this for 'snow'
  }
  public void endDraw () {
    //out.loadPixels();
    //out.loadTexture();
    out.endDraw(); // need this for 'snow'
  }

  int skipFrame = 0, skipLimit = 10;
  public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    //PGraphics img = src;
    //src.loadPixels();

    /*GLTexture s = src.getTexture();
    s.loadPixels();
    s.loadTexture();*/
    //s.loadTexture();
    //GLTexture t = src.getTexture();
    //int[] src_temp = new int[sc.w*sc.h];
    //if (skipFrame++>2) {
      src.getTexture().getBuffer(src_temp);
      //skipFrame = 0;
      //return;
    //}

    for (int b = 0 ; b<numBlobs; b++) {
      int x = sc.w/2, y = sc.h/2;
      if (changePosition) {
        x = (int)(random(sc.w-1));
        y = (int)(random(sc.h-1));//buffer.height;

        blobs[b].setXY(x,y);
      }


      //y = sc.h+1 - (offsety);

      //color pix = img.pixels[(width*y-1)+(width-x)];      mirror
      //System.out.println("offsetx is " + offsetx + ", sc.w is " + sc.w);
      //color pix = img.pixels[sc.w*y-1+((sc.w+offsetx)-x-1)];
      //color pix = src.pixels[(sc.w*blobs[b].y)+blobs[b].x];
      //color pix = src.pixels[(sc.w*blobs[b].y)+blobs[b].x];
      int pix = src_temp[(sc.w*blobs[b].y)+blobs[b].x];

      //if (tinted) out.tint(255); else out.noTint();

      //System.out.println("got colour " + pix);
      blobs[b].setColour(pix);

      blobs[b].draw(out);
      /*
      out.noStroke();
      out.fill(pix);

      out.ellipse(x,y,15,15);
      //out.rect(x-15,y-15,15,15);
      */

    }
    return true;
  }

}
