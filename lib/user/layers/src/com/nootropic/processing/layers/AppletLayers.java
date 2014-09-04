/*
 * Created on Apr 4, 2008
 *
 * Copyright 2008-2010 nootropic design
 */

package com.nootropic.processing.layers;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphicsJava2D;

/**
 * This class is a container for the Layer objects of a Processing sketch.  The AppletLayers object manages
 * the rendering of Layers in the correct order, and allows mouse and keyboard events to be propagated to all
 * layers.  All created Layer objects must be added to an AppletLayers object in order to be drawn in the sketch.
 * @author Michael Krumpus
 *
 */
public class AppletLayers {
    PApplet parent;
    ArrayList layers = new ArrayList();
    PGraphicsJava2D masterGraphics;
    public int pixels[];

    /**
     * Create an AppletLayers object for the specified PApplet.
     * @param parent
     */
    public AppletLayers(PApplet parent) {
        this.parent = parent;
        parent.registerDraw(this);
        parent.registerMouseEvent(this);
        parent.registerKeyEvent(this);
        masterGraphics = (PGraphicsJava2D) parent.createGraphics(parent.width, parent.height, PApplet.JAVA2D);
    }

    /**
     * Add a layer to this AppletLayers object.  Layers are added "on top" of the sketch.
     * 
     * @param layer the layer to add
     */
    public void addLayer(Layer layer) {
        layers.add(layer);
        layer.container = this;
        layer.setup();// should this be called on construction of the layer?
    }
    
    /**
     * Get the number of layers in this AppletLayers object.
     * 
     * @return the number of layers
     */
    public int numLayers() {
        return layers.size();
    }

    /**
     * Get a layer at a specified index.  Index 0 is the bottom-most layer in the sketch.
     * The highest numbered index (one less than the value returned by numLayers()) is the
     * top-most layer in the sketch.
     * @param i the index of the layer to get
     * @return the layer at index i
     */
    public Layer getLayer(int i) {
        return (Layer) layers.get(i);
    }
    
    /**
     * Get a list iterator for the list of layers.
     * @return a list iterator for the list of layers.
     */
    public ListIterator getListIterator() {
        return layers.listIterator();
    }

    /**
     * Draw each Layer managed by this AppletLayers object.  This method is invoked
     * automatically by the parent PApplet.  Layers are drawn by calling their draw()
     * methods, starting with the bottom-most layer and ending with the top-most layer.
     */
    public void draw() {
        Layer layer;
        Iterator i = layers.iterator();
        while (i.hasNext()) {
            layer = (Layer) i.next();
            layer.g.beginDraw();
            layer.draw();
            layer.g.endDraw();
        }
    }

    /**
     * Paint the set of managed Layers to the screen.  This method
     * MUST be called by the paint(Graphics g) method of your sketch.
     * @param pApplet the parent PApplet
     */
    public void paint(PApplet pApplet) {
        try {
            Graphics screen = pApplet.getGraphics();
            if (screen != null) {
                paint(screen);
            }
            Toolkit.getDefaultToolkit().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pApplet.g != null) {
                pApplet.g.dispose();
            }
        }
    }

    synchronized private void paint(Graphics screen) {
        if (parent.frameCount == 0) {
            return;
        }
        Layer layer;

        // Clear the destination drawing surface.
        masterGraphics.background(0);

        // Draw the main PApplet drawing surface on the target Graphics.
        if (parent.g.image != null) {
            // If there is an Image then just use AWT drawing.
            // For the main drawing surface, this is true for P3D and P2D as
            // well as JAVA2D.
            masterGraphics.g2.drawImage(parent.g.image, 0, 0, null);
        } else {
            // Otherwise, use Processing blending as implemented in PImage.
            // [I don't think this case can ever actually occur.]
            masterGraphics.blend(parent.g, 0, 0, parent.width, parent.height, 0, 0, parent.width, parent.height, PConstants.BLEND);
        }

        for (int i = 0; i < layers.size(); i++) {
            layer = (Layer) layers.get(i);
            if (layer.isVisible() && (layer.g != null)) {
                // Get the images from all front Layers and composite them on
                // top of one another.
                if (layer.g.image != null) {
                    // If there is an Image then just use AWT drawing. This is
                    // faster.
                    // This is possible if the Layer is using the JAVA2D
                    // renderer.
                    masterGraphics.g2.drawImage(layer.g.image, layer.clipX, layer.clipY, layer.clipX + layer.clipWidth, layer.clipY + layer.clipHeight,
                            layer.clipX, layer.clipY, layer.clipX + layer.clipWidth, layer.clipY + layer.clipHeight, null);
                } else {
                    // Otherwise, use Processing blending as implemented in
                    // PImage.
                    masterGraphics.blend(layer.g, layer.clipX, layer.clipY, layer.clipWidth, layer.clipHeight, layer.clipX, layer.clipY, layer.clipWidth,
                            layer.clipHeight, PConstants.BLEND);
                }
            }
        }
        screen.drawImage(masterGraphics.image, 0, 0, null);
    }
    
    
    /**
     * Load the pixels from all layers to the variable pixels.
     */
    public void loadPixels() {
      masterGraphics.loadPixels();
      pixels = masterGraphics.pixels;
    }

    /**
     * Process mouse events for the set of Layer objects managed by this
     * AppletLayers object.  This method will be called automatically by
     * the parent PApplet when a mouse event occurs.  The role of the
     * AppletLayers class is to propagate the event to all Layers.
     * @param event the MouseEvent
     */
    public void mouseEvent(MouseEvent event) {
        int id = event.getID();
        
        switch (id) {
        case MouseEvent.MOUSE_PRESSED:
          mousePressed();
          break;
        case MouseEvent.MOUSE_RELEASED:
          mouseReleased();
          break;
        case MouseEvent.MOUSE_CLICKED:
          mouseClicked();
          break;
        case MouseEvent.MOUSE_DRAGGED:
          mouseDragged();
          break;
        case MouseEvent.MOUSE_MOVED:
          mouseMoved();
          break;
        }
    }


    /**
     * Process key events for the set of Layer objects managed by this
     * AppletLayers object.  This method will be called automatically by
     * the parent PApplet when a key event occurs.  The role of the
     * AppletLayers class is to propagate the event to all Layers.
     * @param event the KeyEvent
     */
    public void keyEvent(KeyEvent event) {
        switch (event.getID()) {
        case KeyEvent.KEY_PRESSED:
          keyPressed();
          break;
        case KeyEvent.KEY_RELEASED:
          keyReleased();
          break;
        case KeyEvent.KEY_TYPED:
          keyTyped();
          break;
        }
    }
    
    private void mousePressed() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).mousePressed();
        }
    }

    private void mouseReleased() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).mouseReleased();
        }
    }

    private void mouseClicked() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).mouseClicked();
        }
    }

    private void mouseDragged() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).mouseDragged();
        }
    }

    private void mouseMoved() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).mouseMoved();
        }
    }

    private void keyPressed() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).keyPressed();
        }
    }

    private void keyReleased() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).keyReleased();
        }
    }

    private void keyTyped() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ((Layer) layers.get(i)).keyTyped();
        }
    }
}
