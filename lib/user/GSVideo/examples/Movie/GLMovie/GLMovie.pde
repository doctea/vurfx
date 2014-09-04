import processing.opengl.*;
import javax.media.opengl.*;
import codeanticode.gsvideo.*;
import java.lang.reflect.*;
import java.nio.*;

GLMovie glm;

void setup() {
  size(640, 480, OPENGL);
  background(0);
    
  glm = new GLMovie("station.mov", this);
}

void draw() {
}

public class GLMovie {
  GSMovie mov;
  int[] texID = {0};
  PGraphicsOpenGL pgl;
  GL gl;
  Method disposePixelsMethod = null;
  Object diposePixelsHandler = null;
  LinkedList<PixelData> pixelBuffer = null;  
  int maxBuffSize = 3;
  boolean disposeFramesWhenPixelBufferFull = false;  

  public GLMovie (String videoFile, PApplet app) {
    app.registerDraw(this); 
    mov = new GSMovie(app, videoFile);
    mov.setPixelDest(this);
    mov.loop();
    pgl = (PGraphicsOpenGL) app.g;
  }

  public void draw () {        
    gl = pgl.beginGL();
   
    if (mov.ready()) {
      mov.read();    
      if (putPixelsIntoTexture()) {
        gl.glViewport(0, 0, width, height);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);        

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glOrtho(0.0, width, 0.0, height, -10, 10);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
        gl.glBegin(GL.GL_QUADS);        
        gl.glTexCoord2f(0.0, 1.0); gl.glVertex2f(0.0, 0.0);
        gl.glTexCoord2f(1.0, 1.0); gl.glVertex2f(width, 0.0);
        gl.glTexCoord2f(1.0, 0.0); gl.glVertex2f(width, height);
        gl.glTexCoord2f(0.0, 0.0); gl.glVertex2f(0.0, height);   
        gl.glEnd();     
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();        
      }
    }

    pgl.endGL();
  }
  
  /**
   * Sets obj as the pixel source for this texture. The object must have
   * a public method called disposeBuffer(Object obj) that the texture will
   * use to release the int buffers after they are copied to the texture.
   */  
  public void setPixelSource(Object obj) {
    diposePixelsHandler = obj;
    try {
      disposePixelsMethod = obj.getClass().getMethod("disposeBuffer", new Class[] { Object.class});
    } catch (Exception e) {
      e.printStackTrace();
    }    
  }

  /**
   * If there are frames stored in the buffer, it removes the last
   * and copies the pixels to the texture. It returns true if that
   * is the case, otherwise it returns false.
   */    
  public boolean putPixelsIntoTexture() {
    if (pixelBuffer != null && 0 < pixelBuffer.size() && disposePixelsMethod != null) {
      PixelData data = pixelBuffer.remove(0);
      
      gl.glEnable(GL.GL_TEXTURE_2D);
      if (texID[0] == 0) { 
        gl.glGenTextures(1, texID, 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);        
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, data.w, data.h, 0, GL.GL_RGBA, gl.GL_UNSIGNED_BYTE, null);      
      } else {
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
      }
  
      gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, data.w, data.h, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, data.rgbBuf);
      gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
      
      return true;
    }
    return false;
  }

  /**
   * Sets the size of the pixel buffer, in number of frames. When this size is 
   * reached, new frames are dropped.
   */  
  public void setPixelBufferSize(int n) {
    maxBuffSize = n;
  }
  
  /**
   * Returns how many frames are currently stored in the pixel buffer.
   */    
  public int getPixelBufferUse() {
    return pixelBuffer.size();
  }  

  /**
   * Determines the behavior of the buffering. When the argument is true,
   * then new frames are disposed when the buffer is already full, otherwise
   * they are not. In the later case, and depending on how the buffer generation
   * method works, might result in the buffers being stored somewhere else
   * and being resent later.
   */    
  public void delPixelsWhenBufferFull(boolean v) { 
    disposeFramesWhenPixelBufferFull = v;
  }

  /**
   * This is the method used by the pixel source object
   * to add frames to the buffer.
   */  
  public void addPixelsToBuffer(Object natBuf, IntBuffer rgbBuf, int w, int h) {
    if (pixelBuffer == null) {
      pixelBuffer = new LinkedList<PixelData>();
    }

    if (pixelBuffer.size() + 1 <= maxBuffSize) {
      pixelBuffer.add(new PixelData(natBuf, rgbBuf, w, h));
    } else if (disposeFramesWhenPixelBufferFull) {            
      // The buffer reached the maximum size, so we just dispose the new frame.
      try {
        disposePixelsMethod.invoke(diposePixelsHandler, new Object[] { natBuf });
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
  }
  
  protected class PixelData {    
    int w, h;
    Object natBuf; // Native buffer object.
    IntBuffer rgbBuf; // Buffer viewed as int.
    
    PixelData(Object nat, IntBuffer rgb, int w, int h) {
      natBuf = nat;
      rgbBuf = rgb;
      this.w = w;
      this.h = h;
    }
    
    void dispose() {
      try {
        // Disposing the native buffer.        
        disposePixelsMethod.invoke(diposePixelsHandler, new Object[] { natBuf });
        natBuf = null;
        rgbBuf = null;
      } catch (Exception e) {
        e.printStackTrace();
      }      
    }
  }  
}

