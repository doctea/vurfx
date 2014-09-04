/*
 * Created on Apr 4, 2008
 *
 * Copyright 2008-2010 nootropic design
 */

package com.nootropic.processing.layers;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * This class represents a layer in a sketch.  The Layer object added to an AppletLayers
 * object which manages the layers for a Processing sketch.
 * 
 * This class contains all the drawing primitives of a PApplet object in order to propagate those
 * drawing calls to the underlying PGraphics object.  For example, the method line(x1, y1, x2, y2)
 * calls the corresponding method in the PGraphics object to perform the drawing.
 * @author Michael Krumpus
 *
 */
public abstract class Layer {
    /**
     * The PGraphics object on which all drawing methods are invoked.
     */
    public PGraphics g;
    /**
     * The parent PApplet.
     */
    public PApplet parent;
    protected AppletLayers container;
    private boolean visible = true;
    
    /**
     * Used to specify clipping rectangle for enhanced performance.
     */
    protected int clipX, clipY, clipWidth, clipHeight;

    /**
     * Optional recorder PGraphics for this Layer.
     */
    public PGraphics recorder;

    /**
     * Array of pixels for this Layer.
     */
    public int pixels[];

    /**
     * Create a Layer object in the specified PApplet using default renderer JAVA2D.
     * @param parent the PApplet to which this Layer belongs
     */
    public Layer(PApplet parent) {
        this(parent, PApplet.JAVA2D);
    }

    /**
     * Create a Layer object in the specified PApplet using the specified renderer.
     * @param parent the PApplet to which this Layer belongs
     * @param renderer the Processing renderer (JAVA2D, P2D, P3D) for this layer.
     */
    public Layer(PApplet parent, String renderer) {
        this.parent = parent;
        clipX = clipY = 0;
        clipWidth = parent.width;
        clipHeight = parent.height;
        g = parent.createGraphics(parent.width, parent.height, renderer);
    }

    /**
     * Abstract method implemented by Layer subclasses to draw on the layer.
     */
    public abstract void draw();

    /**
     * Override this method to perform setup operations for this Layer.
     * DO NOT call size(w, h) in the setup() method to set the size of a layer.  All layers
     * are automatically the same size as the containing PApplet.
     * This method is invoked when the Layer is added to an AppletLayers object.
     */
    public void setup() {
    }

    /**
     * Method provided only to throw RuntimeException since calling size(w, h) in a layer is illegal.
     * @param w
     * @param h
     * @throws RuntimeException always.
     */
    public void size(int w, int h) {
        size(w, h, null, null);
    }

    /**
     * Method provided only to throw RuntimeException since calling size(w, h) in a layer is illegal.
     * @param w
     * @param h
     * @param renderer
     * @throws RuntimeException always.
     */
    public void size(int w, int h, String renderer) {
        size(w, h, null, null);
    }
    
    /**
     * Method provided only to throw RuntimeException since calling size(w, h) in a layer is illegal.
     * @param iwidth
     * @param iheight
     * @param irenderer
     * @param ipath
     * @throws RuntimeException always.
     */
    public void size(final int iwidth, final int iheight, String irenderer, String ipath) {
        throw new RuntimeException("Cannot call size() method from a layer.  All layers will automatically be the same size as the containing PApplet.");
    }
    
    /**
     * Get the parent PApplet.
     * @return the parent
     */
    public PApplet getParent() {
        return parent;
    }
    
    /**
     * Get the containing AppletLayers object.
     * @return the containing AppletLayers object.
     */
    public AppletLayers getContainer() {
        return container;
    }

    /**
     * Get the current visibility state of the layer.
     * @return the visibility state
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Set the current visibility state of the layer.
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void beginRecord(PGraphics recorder) {
        this.recorder = recorder;
        recorder.beginDraw();
    }

    public void endRecord() {
        // println("endRecord()");
        // if (!recorderNull) {
        if (recorder != null) {
            // recorder.endRecord();
            recorder.endDraw();
            recorder.dispose();
            recorder = null;
        }
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void loadPixels() {
        g.loadPixels();
        pixels = g.pixels;
    }

    // ////////////////////////////////////////////////////////////

    // everything below this line is automatically generated. no touch.
    // public functions for processing.core

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void imageMode(int mode) {
        if (recorder != null)
            recorder.imageMode(mode);
        g.imageMode(mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void smooth() {
        if (recorder != null)
            recorder.smooth();
        g.smooth();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void noSmooth() {
        if (recorder != null)
            recorder.noSmooth();
        g.noSmooth();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void updatePixels() {
        if (recorder != null)
            recorder.updatePixels();
        g.updatePixels();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void updatePixels(int x1, int y1, int x2, int y2) {
        if (recorder != null)
            recorder.updatePixels(x1, y1, x2, y2);
        g.updatePixels(x1, y1, x2, y2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @return pixel at x,y
     */
    public int get(int x, int y) {
        return g.get(x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param w
     * @param h
     * @return the PImage
     */
    public PImage get(int x, int y, int w, int h) {
        return g.get(x, y, w, h);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @return the PImage
     */
    public PImage get() {
        return g.get();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param c
     */
    public void set(int x, int y, int c) {
        if (recorder != null)
            recorder.set(x, y, c);
        g.set(x, y, c);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param dx
     * @param dy
     * @param src
     */
    public void set(int dx, int dy, PImage src) {
        if (recorder != null)
            recorder.set(dx, dy, src);
        g.set(dx, dy, src);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param alpha
     */
    public void mask(int alpha[]) {
        if (recorder != null)
            recorder.mask(alpha);
        g.mask(alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param alpha
     */
    public void mask(PImage alpha) {
        if (recorder != null)
            recorder.mask(alpha);
        g.mask(alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param kind
     */
    public void filter(int kind) {
        if (recorder != null)
            recorder.filter(kind);
        g.filter(kind);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param kind
     * @param param
     */
    public void filter(int kind, float param) {
        if (recorder != null)
            recorder.filter(kind, param);
        g.filter(kind, param);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     */
    public void copy(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
        if (recorder != null)
            recorder.copy(sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2);
        g.copy(sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param src
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     */
    public void copy(PImage src, int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
        if (recorder != null)
            recorder.copy(src, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2);
        g.copy(src, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param c1
     * @param c2
     * @param mode
     * @return blended color
     */
    static public int blendColor(int c1, int c2, int mode) {
        return PGraphics.blendColor(c1, c2, mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param mode
     */
    public void blend(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2, int mode) {
        if (recorder != null)
            recorder.blend(sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, mode);
        g.blend(sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param src
     * @param sx1
     * @param sy1
     * @param sx2
     * @param sy2
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param mode
     */
    public void blend(PImage src, int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2, int mode) {
        if (recorder != null)
            recorder.blend(src, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, mode);
        g.blend(src, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2, mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param which
     */
    public void hint(int which) {
        if (recorder != null)
            recorder.hint(which);
        g.hint(which);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     */
    public void beginShape() {
        if (recorder != null)
            recorder.beginShape();
        g.beginShape();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param kind
     */
    public void beginShape(int kind) {
        if (recorder != null)
            recorder.beginShape(kind);
        g.beginShape(kind);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param nx
     * @param ny
     * @param nz
     */
    public void normal(float nx, float ny, float nz) {
        if (recorder != null)
            recorder.normal(nx, ny, nz);
        g.normal(nx, ny, nz);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param mode
     */
    public void textureMode(int mode) {
        if (recorder != null)
            recorder.textureMode(mode);
        g.textureMode(mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param image
     */
    public void texture(PImage image) {
        if (recorder != null)
            recorder.texture(image);
        g.texture(image);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param x
     * @param y
     */
    public void vertex(float x, float y) {
        if (recorder != null)
            recorder.vertex(x, y);
        g.vertex(x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param x
     * @param y
     * @param z
     */
    public void vertex(float x, float y, float z) {
        if (recorder != null)
            recorder.vertex(x, y, z);
        g.vertex(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param x
     * @param y
     * @param u
     * @param v
     */
    public void vertex(float x, float y, float u, float v) {
        if (recorder != null)
            recorder.vertex(x, y, u, v);
        g.vertex(x, y, u, v);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param x
     * @param y
     * @param z
     * @param u
     * @param v
     */
    public void vertex(float x, float y, float z, float u, float v) {
        if (recorder != null)
            recorder.vertex(x, y, z, u, v);
        g.vertex(x, y, z, u, v);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * 
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     */
    public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4) {
        if (recorder != null)
            recorder.bezierVertex(x2, y2, x3, y3, x4, y4);
        g.bezierVertex(x2, y2, x3, y3, x4, y4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x2
     * @param y2
     * @param z2
     * @param x3
     * @param y3
     * @param z3
     * @param x4
     * @param y4
     * @param z4
     */
    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        if (recorder != null)
            recorder.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
        g.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     */
    public void curveVertex(float x, float y) {
        if (recorder != null)
            recorder.curveVertex(x, y);
        g.curveVertex(x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void curveVertex(float x, float y, float z) {
        if (recorder != null)
            recorder.curveVertex(x, y, z);
        g.curveVertex(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void breakShape() {
        if (recorder != null)
            recorder.breakShape();
        g.breakShape();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public final void endShape() {
        if (recorder != null)
            recorder.endShape();
        g.endShape();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     */
    public void endShape(int mode) {
        if (recorder != null)
            recorder.endShape(mode);
        g.endShape(mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     */
    public void point(float x, float y) {
        if (recorder != null)
            recorder.point(x, y);
        g.point(x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void point(float x, float y, float z) {
        if (recorder != null)
            recorder.point(x, y, z);
        g.point(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void line(float x1, float y1, float x2, float y2) {
        if (recorder != null)
            recorder.line(x1, y1, x2, y2);
        g.line(x1, y1, x2, y2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
        if (recorder != null)
            recorder.line(x1, y1, z1, x2, y2, z2);
        g.line(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (recorder != null)
            recorder.triangle(x1, y1, x2, y2, x3, y3);
        g.triangle(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     */
    public void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (recorder != null)
            recorder.quad(x1, y1, x2, y2, x3, y3, x4, y4);
        g.quad(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     */
    public void rectMode(int mode) {
        if (recorder != null)
            recorder.rectMode(mode);
        g.rectMode(mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void rect(float x1, float y1, float x2, float y2) {
        if (recorder != null)
            recorder.rect(x1, y1, x2, y2);
        g.rect(x1, y1, x2, y2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     */
    public void ellipseMode(int mode) {
        if (recorder != null)
            recorder.ellipseMode(mode);
        g.ellipseMode(mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public void ellipse(float a, float b, float c, float d) {
        if (recorder != null)
            recorder.ellipse(a, b, c, d);
        g.ellipse(a, b, c, d);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param a
     * @param b
     * @param c
     * @param d
     * @param start
     * @param stop
     */
    public void arc(float a, float b, float c, float d, float start, float stop) {
        if (recorder != null)
            recorder.arc(a, b, c, d, start, stop);
        g.arc(a, b, c, d, start, stop);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param size
     */
    public void box(float size) {
        if (recorder != null)
            recorder.box(size);
        g.box(size);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param w
     * @param h
     * @param d
     */
    public void box(float w, float h, float d) {
        if (recorder != null)
            recorder.box(w, h, d);
        g.box(w, h, d);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param res
     */
    public void sphereDetail(int res) {
        if (recorder != null)
            recorder.sphereDetail(res);
        g.sphereDetail(res);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param r
     */
    public void sphere(float r) {
        if (recorder != null)
            recorder.sphere(r);
        g.sphere(r);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param a
     * @param b
     * @param c
     * @param d
     * @param t
     * @return bezier point
     */
    public float bezierPoint(float a, float b, float c, float d, float t) {
        return g.bezierPoint(a, b, c, d, t);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param a
     * @param b
     * @param c
     * @param d
     * @param t
     * @return bezier tangent
     */
    public float bezierTangent(float a, float b, float c, float d, float t) {
        return g.bezierTangent(a, b, c, d, t);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param detail
     */
    public void bezierDetail(int detail) {
        if (recorder != null)
            recorder.bezierDetail(detail);
        g.bezierDetail(detail);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     */
    public void bezier(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (recorder != null)
            recorder.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
        g.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param x3
     * @param y3
     * @param z3
     * @param x4
     * @param y4
     * @param z4
     */
    public void bezier(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        if (recorder != null)
            recorder.bezier(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
        g.bezier(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param a
     * @param b
     * @param c
     * @param d
     * @param t
     * @return curve point
     */
    public float curvePoint(float a, float b, float c, float d, float t) {
        return g.curvePoint(a, b, c, d, t);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param a
     * @param b
     * @param c
     * @param d
     * @param t
     * @return curve tangent
     */
    public float curveTangent(float a, float b, float c, float d, float t) {
        return g.curveTangent(a, b, c, d, t);
    }
    
    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param detail
     */
    public void curveDetail(int detail) {
        if (recorder != null)
            recorder.curveDetail(detail);
        g.curveDetail(detail);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param tightness
     */
    public void curveTightness(float tightness) {
        if (recorder != null)
            recorder.curveTightness(tightness);
        g.curveTightness(tightness);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     */
    public void curve(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (recorder != null)
            recorder.curve(x1, y1, x2, y2, x3, y3, x4, y4);
        g.curve(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param x3
     * @param y3
     * @param z3
     * @param x4
     * @param y4
     * @param z4
     */
    public void curve(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        if (recorder != null)
            recorder.curve(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
        g.curve(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param image
     * @param x
     * @param y
     */
    public void image(PImage image, float x, float y) {
        if (recorder != null)
            recorder.image(image, x, y);
        g.image(image, x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param image
     * @param x
     * @param y
     * @param c
     * @param d
     */
    public void image(PImage image, float x, float y, float c, float d) {
        if (recorder != null)
            recorder.image(image, x, y, c, d);
        g.image(image, x, y, c, d);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param image
     * @param a
     * @param b
     * @param c
     * @param d
     * @param u1
     * @param v1
     * @param u2
     * @param v2
     */
    public void image(PImage image, float a, float b, float c, float d, int u1, int v1, int u2, int v2) {
        if (recorder != null)
            recorder.image(image, a, b, c, d, u1, v1, u2, v2);
        g.image(image, a, b, c, d, u1, v1, u2, v2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param align
     */
    public void textAlign(int align) {
        if (recorder != null)
            recorder.textAlign(align);
        g.textAlign(align);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param alignX
     * @param alignY
     */
    public void textAlign(int alignX, int alignY) {
        if (recorder != null)
            recorder.textAlign(alignX, alignY);
        g.textAlign(alignX, alignY);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @return text ascent
     */
    public float textAscent() {
        return g.textAscent();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @return text descent
     */
    public float textDescent() {
        return g.textDescent();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param which
     */
    public void textFont(PFont which) {
        if (recorder != null)
            recorder.textFont(which);
        g.textFont(which);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param which
     * @param size
     */
    public void textFont(PFont which, float size) {
        if (recorder != null)
            recorder.textFont(which, size);
        g.textFont(which, size);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param leading
     */
    public void textLeading(float leading) {
        if (recorder != null)
            recorder.textLeading(leading);
        g.textLeading(leading);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     */
    public void textMode(int mode) {
        if (recorder != null)
            recorder.textMode(mode);
        g.textMode(mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param size
     */
    public void textSize(float size) {
        if (recorder != null)
            recorder.textSize(size);
        g.textSize(size);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param c
     * @return text width
     */
    public float textWidth(char c) {
        return g.textWidth(c);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param str
     * @return text width
     */
    public float textWidth(String str) {
        return g.textWidth(str);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param c
     */
    public void text(char c) {
        if (recorder != null)
            recorder.text(c);
        g.text(c);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param c
     * @param x
     * @param y
     */
    public void text(char c, float x, float y) {
        if (recorder != null)
            recorder.text(c, x, y);
        g.text(c, x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param c
     * @param x
     * @param y
     * @param z
     */
    public void text(char c, float x, float y, float z) {
        if (recorder != null)
            recorder.text(c, x, y, z);
        g.text(c, x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param str
     */
    public void text(String str) {
        if (recorder != null)
            recorder.text(str);
        g.text(str);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param str
     * @param x
     * @param y
     */
    public void text(String str, float x, float y) {
        if (recorder != null)
            recorder.text(str, x, y);
        g.text(str, x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param str
     * @param x
     * @param y
     * @param z
     */
    public void text(String str, float x, float y, float z) {
        if (recorder != null)
            recorder.text(str, x, y, z);
        g.text(str, x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param str
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void text(String str, float x1, float y1, float x2, float y2) {
        if (recorder != null)
            recorder.text(str, x1, y1, x2, y2);
        g.text(str, x1, y1, x2, y2);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param s
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param z
     */
    public void text(String s, float x1, float y1, float x2, float y2, float z) {
        if (recorder != null)
            recorder.text(s, x1, y1, x2, y2, z);
        g.text(s, x1, y1, x2, y2, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param num
     * @param x
     * @param y
     */
    public void text(int num, float x, float y) {
        if (recorder != null)
            recorder.text(num, x, y);
        g.text(num, x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param num
     * @param x
     * @param y
     * @param z
     */
    public void text(int num, float x, float y, float z) {
        if (recorder != null)
            recorder.text(num, x, y, z);
        g.text(num, x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param num
     * @param x
     * @param y
     */
    public void text(float num, float x, float y) {
        if (recorder != null)
            recorder.text(num, x, y);
        g.text(num, x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param num
     * @param x
     * @param y
     * @param z
     */
    public void text(float num, float x, float y, float z) {
        if (recorder != null)
            recorder.text(num, x, y, z);
        g.text(num, x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param tx
     * @param ty
     */
    public void translate(float tx, float ty) {
        if (recorder != null)
            recorder.translate(tx, ty);
        g.translate(tx, ty);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param tx
     * @param ty
     * @param tz
     */
    public void translate(float tx, float ty, float tz) {
        if (recorder != null)
            recorder.translate(tx, ty, tz);
        g.translate(tx, ty, tz);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param angle
     */
    public void rotate(float angle) {
        if (recorder != null)
            recorder.rotate(angle);
        g.rotate(angle);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param angle
     */
    public void rotateX(float angle) {
        if (recorder != null)
            recorder.rotateX(angle);
        g.rotateX(angle);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param angle
     */
    public void rotateY(float angle) {
        if (recorder != null)
            recorder.rotateY(angle);
        g.rotateY(angle);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param angle
     */
    public void rotateZ(float angle) {
        if (recorder != null)
            recorder.rotateZ(angle);
        g.rotateZ(angle);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param angle
     * @param vx
     * @param vy
     * @param vz
     */
    public void rotate(float angle, float vx, float vy, float vz) {
        if (recorder != null)
            recorder.rotate(angle, vx, vy, vz);
        g.rotate(angle, vx, vy, vz);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param s
     */
    public void scale(float s) {
        if (recorder != null)
            recorder.scale(s);
        g.scale(s);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param sx
     * @param sy
     */
    public void scale(float sx, float sy) {
        if (recorder != null)
            recorder.scale(sx, sy);
        g.scale(sx, sy);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void scale(float x, float y, float z) {
        if (recorder != null)
            recorder.scale(x, y, z);
        g.scale(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void pushMatrix() {
        if (recorder != null)
            recorder.pushMatrix();
        g.pushMatrix();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void popMatrix() {
        if (recorder != null)
            recorder.popMatrix();
        g.popMatrix();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void resetMatrix() {
        if (recorder != null)
            recorder.resetMatrix();
        g.resetMatrix();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param n00
     * @param n01
     * @param n02
     * @param n10
     * @param n11
     * @param n12
     */
    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        if (recorder != null)
            recorder.applyMatrix(n00, n01, n02, n10, n11, n12);
        g.applyMatrix(n00, n01, n02, n10, n11, n12);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param n00
     * @param n01
     * @param n02
     * @param n03
     * @param n10
     * @param n11
     * @param n12
     * @param n13
     * @param n20
     * @param n21
     * @param n22
     * @param n23
     * @param n30
     * @param n31
     * @param n32
     * @param n33
     */
    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23,
            float n30, float n31, float n32, float n33) {
        if (recorder != null)
            recorder.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
        g.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void printMatrix() {
        if (recorder != null)
            recorder.printMatrix();
        g.printMatrix();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void beginCamera() {
        if (recorder != null)
            recorder.beginCamera();
        g.beginCamera();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void endCamera() {
        if (recorder != null)
            recorder.endCamera();
        g.endCamera();
    }
    
    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void camera() {
        if (recorder != null)
            recorder.camera();
        g.camera();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param eyeX
     * @param eyeY
     * @param eyeZ
     * @param centerX
     * @param centerY
     * @param centerZ
     * @param upX
     * @param upY
     * @param upZ
     */
    public void camera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        if (recorder != null)
            recorder.camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        g.camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void printCamera() {
        if (recorder != null)
            recorder.printCamera();
        g.printCamera();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void ortho() {
        if (recorder != null)
            recorder.ortho();
        g.ortho();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        if (recorder != null)
            recorder.ortho(left, right, bottom, top, near, far);
        g.ortho(left, right, bottom, top, near, far);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void perspective() {
        if (recorder != null)
            recorder.perspective();
        g.perspective();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param fovy
     * @param aspect
     * @param zNear
     * @param zFar
     */
    public void perspective(float fovy, float aspect, float zNear, float zFar) {
        if (recorder != null)
            recorder.perspective(fovy, aspect, zNear, zFar);
        g.perspective(fovy, aspect, zNear, zFar);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param znear
     * @param zfar
     */
    public void frustum(float left, float right, float bottom, float top, float znear, float zfar) {
        if (recorder != null)
            recorder.frustum(left, right, bottom, top, znear, zfar);
        g.frustum(left, right, bottom, top, znear, zfar);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void printProjection() {
        if (recorder != null)
            recorder.printProjection();
        g.printProjection();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @return screenX
     */
    public float screenX(float x, float y) {
        return g.screenX(x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @return screenY
     */
    public float screenY(float x, float y) {
        return g.screenY(x, y);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @return screenX
     */
    public float screenX(float x, float y, float z) {
        return g.screenX(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @return screenY
     */
    public float screenY(float x, float y, float z) {
        return g.screenY(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @return screenZ
     */
    public float screenZ(float x, float y, float z) {
        return g.screenZ(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @return modelX
     */
    public float modelX(float x, float y, float z) {
        return g.modelX(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @return modelY
     */
    public float modelY(float x, float y, float z) {
        return g.modelY(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @return modelZ
     */
    public float modelZ(float x, float y, float z) {
        return g.modelZ(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     */
    public void colorMode(int mode) {
        if (recorder != null)
            recorder.colorMode(mode);
        g.colorMode(mode);
    }
    
    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     * @param max
     */
    public void colorMode(int mode, float max) {
        if (recorder != null)
            recorder.colorMode(mode, max);
        g.colorMode(mode, max);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     * @param maxX
     * @param maxY
     * @param maxZ
     */
    public void colorMode(int mode, float maxX, float maxY, float maxZ) {
        if (recorder != null)
            recorder.colorMode(mode, maxX, maxY, maxZ);
        g.colorMode(mode, maxX, maxY, maxZ);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param mode
     * @param maxX
     * @param maxY
     * @param maxZ
     * @param maxA
     */
    public void colorMode(int mode, float maxX, float maxY, float maxZ, float maxA) {
        if (recorder != null)
            recorder.colorMode(mode, maxX, maxY, maxZ, maxA);
        g.colorMode(mode, maxX, maxY, maxZ, maxA);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param weight
     */
    public void strokeWeight(float weight) {
        if (recorder != null)
            recorder.strokeWeight(weight);
        g.strokeWeight(weight);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param join
     */
    public void strokeJoin(int join) {
        if (recorder != null)
            recorder.strokeJoin(join);
        g.strokeJoin(join);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param cap
     */
    public void strokeCap(int cap) {
        if (recorder != null)
            recorder.strokeCap(cap);
        g.strokeCap(cap);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void noStroke() {
        if (recorder != null)
            recorder.noStroke();
        g.noStroke();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void stroke(int rgb) {
        if (recorder != null)
            recorder.stroke(rgb);
        g.stroke(rgb);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     * @param alpha
     */
    public void stroke(int rgb, float alpha) {
        if (recorder != null)
            recorder.stroke(rgb, alpha);
        g.stroke(rgb, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void stroke(float gray) {
        if (recorder != null)
            recorder.stroke(gray);
        g.stroke(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     * @param alpha
     */
    public void stroke(float gray, float alpha) {
        if (recorder != null)
            recorder.stroke(gray, alpha);
        g.stroke(gray, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void stroke(float x, float y, float z) {
        if (recorder != null)
            recorder.stroke(x, y, z);
        g.stroke(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @param a
     */
    public void stroke(float x, float y, float z, float a) {
        if (recorder != null)
            recorder.stroke(x, y, z, a);
        g.stroke(x, y, z, a);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void noTint() {
        if (recorder != null)
            recorder.noTint();
        g.noTint();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void tint(int rgb) {
        if (recorder != null)
            recorder.tint(rgb);
        g.tint(rgb);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     * @param alpha
     */
    public void tint(int rgb, float alpha) {
        if (recorder != null)
            recorder.tint(rgb, alpha);
        g.tint(rgb, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void tint(float gray) {
        if (recorder != null)
            recorder.tint(gray);
        g.tint(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     * @param alpha
     */
    public void tint(float gray, float alpha) {
        if (recorder != null)
            recorder.tint(gray, alpha);
        g.tint(gray, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void tint(float x, float y, float z) {
        if (recorder != null)
            recorder.tint(x, y, z);
        g.tint(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @param a
     */
    public void tint(float x, float y, float z, float a) {
        if (recorder != null)
            recorder.tint(x, y, z, a);
        g.tint(x, y, z, a);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void noFill() {
        if (recorder != null)
            recorder.noFill();
        g.noFill();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void fill(int rgb) {
        if (recorder != null)
            recorder.fill(rgb);
        g.fill(rgb);
    }
    
    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     * @param alpha
     */
    public void fill(int rgb, float alpha) {
        if (recorder != null)
            recorder.fill(rgb, alpha);
        g.fill(rgb, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void fill(float gray) {
        if (recorder != null)
            recorder.fill(gray);
        g.fill(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     * @param alpha
     */
    public void fill(float gray, float alpha) {
        if (recorder != null)
            recorder.fill(gray, alpha);
        g.fill(gray, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void fill(float x, float y, float z) {
        if (recorder != null)
            recorder.fill(x, y, z);
        g.fill(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @param a
     */
    public void fill(float x, float y, float z, float a) {
        if (recorder != null)
            recorder.fill(x, y, z, a);
        g.fill(x, y, z, a);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void ambient(int rgb) {
        if (recorder != null)
            recorder.ambient(rgb);
        g.ambient(rgb);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void ambient(float gray) {
        if (recorder != null)
            recorder.ambient(gray);
        g.ambient(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void ambient(float x, float y, float z) {
        if (recorder != null)
            recorder.ambient(x, y, z);
        g.ambient(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void specular(int rgb) {
        if (recorder != null)
            recorder.specular(rgb);
        g.specular(rgb);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void specular(float gray) {
        if (recorder != null)
            recorder.specular(gray);
        g.specular(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void specular(float x, float y, float z) {
        if (recorder != null)
            recorder.specular(x, y, z);
        g.specular(x, y, z);
    }
    
    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param shine
     */
    public void shininess(float shine) {
        if (recorder != null)
            recorder.shininess(shine);
        g.shininess(shine);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void emissive(int rgb) {
        if (recorder != null)
            recorder.emissive(rgb);
        g.emissive(rgb);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void emissive(float gray) {
        if (recorder != null)
            recorder.emissive(gray);
        g.emissive(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void emissive(float x, float y, float z) {
        if (recorder != null)
            recorder.emissive(x, y, z);
        g.emissive(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void lights() {
        if (recorder != null)
            recorder.lights();
        g.lights();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void noLights() {
        if (recorder != null)
            recorder.noLights();
        g.noLights();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param red
     * @param green
     * @param blue
     */
    public void ambientLight(float red, float green, float blue) {
        if (recorder != null)
            recorder.ambientLight(red, green, blue);
        g.ambientLight(red, green, blue);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param red
     * @param green
     * @param blue
     * @param x
     * @param y
     * @param z
     */
    public void ambientLight(float red, float green, float blue, float x, float y, float z) {
        if (recorder != null)
            recorder.ambientLight(red, green, blue, x, y, z);
        g.ambientLight(red, green, blue, x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param red
     * @param green
     * @param blue
     * @param nx
     * @param ny
     * @param nz
     */
    public void directionalLight(float red, float green, float blue, float nx, float ny, float nz) {
        if (recorder != null)
            recorder.directionalLight(red, green, blue, nx, ny, nz);
        g.directionalLight(red, green, blue, nx, ny, nz);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param red
     * @param green
     * @param blue
     * @param x
     * @param y
     * @param z
     */
    public void pointLight(float red, float green, float blue, float x, float y, float z) {
        if (recorder != null)
            recorder.pointLight(red, green, blue, x, y, z);
        g.pointLight(red, green, blue, x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param red
     * @param green
     * @param blue
     * @param x
     * @param y
     * @param z
     * @param nx
     * @param ny
     * @param nz
     * @param angle
     * @param concentration
     */
    public void spotLight(float red, float green, float blue, float x, float y, float z, float nx, float ny, float nz, float angle, float concentration) {
        if (recorder != null)
            recorder.spotLight(red, green, blue, x, y, z, nx, ny, nz, angle, concentration);
        g.spotLight(red, green, blue, x, y, z, nx, ny, nz, angle, concentration);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param constant
     * @param linear
     * @param quadratic
     */
    public void lightFalloff(float constant, float linear, float quadratic) {
        if (recorder != null)
            recorder.lightFalloff(constant, linear, quadratic);
        g.lightFalloff(constant, linear, quadratic);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void lightSpecular(float x, float y, float z) {
        if (recorder != null)
            recorder.lightSpecular(x, y, z);
        g.lightSpecular(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     */
    public void background(int rgb) {
        if (recorder != null)
            recorder.background(rgb);
        g.background(rgb);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rgb
     * @param alpha
     */
    public void background(int rgb, float alpha) {
        if (recorder != null)
            recorder.background(rgb, alpha);
        g.background(rgb, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     */
    public void background(float gray) {
        if (recorder != null)
            recorder.background(gray);
        g.background(gray);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param gray
     * @param alpha
     */
    public void background(float gray, float alpha) {
        if (recorder != null)
            recorder.background(gray, alpha);
        g.background(gray, alpha);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     */
    public void background(float x, float y, float z) {
        if (recorder != null)
            recorder.background(x, y, z);
        g.background(x, y, z);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param x
     * @param y
     * @param z
     * @param a
     */
    public void background(float x, float y, float z, float a) {
        if (recorder != null)
            recorder.background(x, y, z, a);
        g.background(x, y, z, a);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param image
     */
    public void background(PImage image) {
        if (recorder != null)
            recorder.background(image);
        g.background(image);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return alpha of given color
     */
    public final float alpha(int what) {
        return g.alpha(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return red component of given color
     */
    public final float red(int what) {
        return g.red(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return green component of given color
     */
    public final float green(int what) {
        return g.green(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return blue component of given color
     */
    public final float blue(int what) {
        return g.blue(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return hue of given color
     */
    public final float hue(int what) {
        return g.hue(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return saturation of given color
     */
    public final float saturation(int what) {
        return g.saturation(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param what
     * @return brightness of given color
     */
    public final float brightness(int what) {
        return g.brightness(what);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param c1
     * @param c2
     * @param amt
     * @return interpolated color
     */
    public int lerpColor(int c1, int c2, float amt) {
        return g.lerpColor(c1, c2, amt);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param c1
     * @param c2
     * @param amt
     * @param mode
     * @return interpolated color
     */
    static public int lerpColor(int c1, int c2, float amt, int mode) {
        return PGraphics.lerpColor(c1, c2, amt, mode);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @param rawGraphics
     */
    public void beginRaw(PGraphics rawGraphics) {
        if (recorder != null)
            recorder.beginRaw(rawGraphics);
        g.beginRaw(rawGraphics);
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     */
    public void endRaw() {
        if (recorder != null)
            recorder.endRaw();
        g.endRaw();
    }

    /**
     * Pass-through method to underlying PGraphics object.
     * @see <a href="http://processing.org/reference/">http://processing.org/reference/</a>
     * @return displayable value of PGraphics object.
     */
    public boolean displayable() {
        return g.displayable();
    }

    // Mouse events
    /**
     * Override this method to process mousePressed events.
     */
    public void mousePressed() {
    }

    /**
     * Override this method to process mouseReleased events.
     */
    public void mouseReleased() {
    }

    /**
     * Override this method to process mouseClicked events.
     */
    public void mouseClicked() {
    }

    /**
     * Override this method to process mouseDragged events.
     */
    public void mouseDragged() {
    }

    /**
     * Override this method to process mouseMoved events.
     */
    public void mouseMoved() {
    }

    // Keyboard events
    /**
     * Override this method to process keyPressed events.
     */
    public void keyPressed() {
    }

    /**
     * Override this method to process keyReleased events.
     */
    public void keyReleased() {
    }

    /**
     * Override this method to process keyTyped events.
     */
    public void keyTyped() {
    }

}
