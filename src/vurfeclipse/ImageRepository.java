package vurfeclipse;

import java.util.*;

import processing.core.PImage;

public class ImageRepository {

  static public ImageRepository IR;
  static boolean debug = false;

  transient HashMap<String, PImage> images = new HashMap<String, PImage> ();

  synchronized public boolean hasCached(String fn, int w, int h) {
    /*if (!images.containsKey(fn)) {
     ArrayList<String> a = new ArrayList<String>();
     a.add(fn);
     preload(a, 2, w, h);
     }*/
    return images.containsKey(fn);
  }

  public PImage cacheLoad(final String fn) {
    return cacheLoad(fn, 0, 0);
  }

  public PImage addToCache(String fn, PImage p) {
	APP.getApp().getGraphics().image(p, 0, 0);
	try {
      images.put(fn, p);
	} catch (Exception e) {
		System.out.println("ImageRepository#addToCache caught " + e);
	}
    return p;
  }

  public PImage getReversePImage( PImage image ) {
  	try {
		  PImage reverse = new PImage( image.width, image.height );
		  image.loadPixels();
		  reverse.updatePixels();
		  reverse.loadPixels();
		  if (debug) System.out.println("ImageRepository#getReversePImage: reversing " + image);
		   /*for (int i = 0; i < image.width; i++) {
			     // Begin loop for height
			     for (int j = 0; j < image.height; j++) {
			       reverse.pixels[j*image.width+i] = image.pixels[(image.width - i - 1) + j*image.width]; // Reversing x to mirror the image
			     }
			   }*/
		  for (int y = 0 ; y < image.height ; y++) {
			  for (int x = 0 ; x < image.width ; x++) {
				  //reverse.pixels[(y * image.width) + x] = image.pixels[(image.height-y-1) + image.height*x];
				  int y_offset = y*image.width;
				  int dest_offset = (image.height-y-1) * image.width;

				  reverse.pixels[y_offset + x] = image.pixels[dest_offset + x];
			  }
		  }
		  reverse.updatePixels();
		  return reverse;
  	} catch (Exception e) {
  		System.out.println("ImageRepository#getReversePImage caught " + e + ", returning null");
  		return null;
  	}
  }

  public PImage cacheLoad(final String fn, final int w, final int h) {
    if (!images.containsKey(fn)) {
      //if (images.size()>50) images.remove(random(0,images.size()));
      if (debug) System.out.println("ImageRepository#cacheImage('" + fn + "', already " + images.size());
      System.out.print(".");
      PImage p = ((VurfEclipse)APP.getApp()).loadImage("data/image-sources/" + fn);
      //PImage p = getReversePImage(((VurfEclipse)APP.getApp()).loadImage("data/image-sources/" + fn));
      if (p!=null) {
    	if (w!=0&&h!=0) p.resize(w, h);

        addToCache(fn,p);
        //images.put(fn, p);
        //GLTexture current_image = new GLTexture(APP,APP.width,APP.height);
        //current_image.putImage(p);
        p = null;
      } else {
        System.out.println("ImageRepository#cacheImage: error loading data/image-sources/" + fn);
        //filenames.remove(filenames.indexOf(fn));
      }
    } else {
      if (debug) System.out.println("ImageRepository#cacheImage: already cached " + fn + " (" + images.size() + " items cached)");
    }
    return images.get(fn);
  }

  public PImage getImageForFilename(final String fn, int w, int h) {
    return cacheLoad(fn, w, h);
  }
  public PImage getImageForFilename(final String fn) {
    return cacheLoad(fn);
  }

  synchronized public void precache(final String fn, final int w, final int h) {
    //new Thread() {
      //public void run () {
        if (!images.containsKey(fn)) {
          if (debug) System.out.println("ImageRepository#precache: precaching " + fn);
          cacheLoad(fn, w, h);
        }
      //}
    //}
    //.start();
  }

  synchronized public void preload(final ArrayList<String> filenames, final int w, final int h) {
    //new Thread() {
     //public void run () {
    Iterator<String> it = filenames.iterator();
    while (it.hasNext ()) { // && count < loadNumber) {
      String fn = (String)it.next();
      //if (!hasCached(fn,w,h)) {
      if (!images.containsKey(fn)) {
        cacheLoad(fn, w, h);
      }
    }
    //}
     //}
     //.start();
  }
}


