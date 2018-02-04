package vurfeclipse.filters;


import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;



public class DebugDrawer extends Filter {
  int x = 5; //width-150
  int y = 300; //width-149

  int mode = 0;
  int modeCount = 4; // standard, filters, console, filters+console

  Scene selectedScene;

  public DebugDrawer(Scene sc) {
    super(sc);
  }

  public Filter nextMode() {
    mode++;
    if (mode>modeCount) mode = 0;
    return this;
  }

  public void setParameterDefaults() {
    super.setParameterDefaults();
    addParameter("beat", 0);
    addParameter("bar", 0);
    addParameter("scene", 0);
    addParameter("test", 0);
  }

  public boolean initialise() {
    super.initialise();
    // set up inital variables or whatevs
    selectedScene = sc.host.getSelectedScene();

    return true;
  }

  int lineCount = 0;
  void drawDebug(String label, Object value) {
    lineCount++;
    out.textAlign(PApplet.LEFT);
    out.fill(128);
    out.text(label +":" + value, x, lineCount*20);
    out.fill(255);
    out.text(label + ":" + value, x-1, (lineCount*20)+1);
    out.fill(0);
    out.text(label + ":" + value, x-2, (lineCount*20)+2);

    //System.out.println("output " + label + "  " + value);
  }

  void drawFilterList() {
    //Iterator i = Arrays.asList(sc.filters).iterator();
    //int c = 0;
    drawDebug("SelectedFilter", selectedScene.getSelectedFilterDescription());
    //while (i.hasNext()) {
      //Filter f = (Filter)i.next();
    for (int c = 0; c<selectedScene.filters.length;c++) {
      Filter f = selectedScene.filters[c];
      if (f!=null) {
        String pad = c==selectedScene.selectedFilter?"*":" ";
        drawDebug(pad + "["+c+"]" + pad,
          f!=null?
            "[" + (f.isMuted()?"M": "...") + "]" + " " +
            f.getFilterLabel() + " (" + f.toString() + ")"
            :"--"
          );
      }
      //c++;
    }
  }

  void drawConsole () {
    int wid = selectedScene.w/selectedScene.BUF_MAX;
    int hei = selectedScene.h/selectedScene.BUF_MAX;
/*    for (int n = 0 ; n < selectedScene.BUF_MAX ; n++) {
      if (null!=selectedScene.buffers[n]
          && n!=selectedScene.BUF_OUT //dont draw BUF_OUT because it will just be a feedback loop in most cases
        ) {
        int x = n*wid, y = height-hei;
        out.image(selectedScene.buffers[n].getTexture(), x, y, wid, hei);
        out.textSize(10);
        out.fill(255);
        out.text(n + ":" + selectedScene.buffers[n], x + 10, y + 10);
      }
    } */
  }

  public boolean applyMeatToBuffers() {
    out.pushStyle();
    lineCount = 0;
    out.textSize(25);

    lineCount++;
    drawDebug("sequence", sc.host.getSequenceName());

    lineCount++;
    drawDebug("fps", (int)((VurfEclipse)APP.getApp()).frameRate);

    if (mode == 3 || mode == 4) {

      drawDebug("test", this.getParameterValue("test"));

      drawDebug("beat", this.getParameterValue("beat"));
      drawDebug("bar",  this.getParameterValue("bar"));

      selectedScene = sc.host.getSelectedScene();
      drawDebug("scene", selectedScene);
    }

    if (mode == 1 || mode == 3) {
      drawFilterList();
    }

    if (mode == 2 || mode == 3) {
      drawConsole();
    }
    out.popStyle();

    return true;
  }
}
