package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;


public class FilterChain extends Filter {

  int numFilters = 8;
  Filter[] filters = new Filter[numFilters];

  /***
   **  CONSTRUCTORS
  ****/

  public FilterChain(Scene sc) {
    super(sc);
  }
  public FilterChain(Scene sc, String filterLabel) {
    this(sc);
    this.filterLabel = filterLabel;
  }





  int fCount = 0;
  public void addFilter(Filter f) {
    filters[fCount] = f;
    fCount++;
  }
  public FilterChain addFilterDefaults(Filter f) {
    //f.setBuffers(out,src);
    f.setAliases(alias_out, alias_in);
    this.addFilter(f);
    return this;
  }

  public Filter nextMode () {
    //quick nasty hack implementation - does a nextMode on all the subfilters too
    for (int i = 0 ; i < filters.length ; i++) {
      if (filters[i]!=null)
        filters[i].nextMode();
    }
    return this;
  }


  public boolean initialise () {
    for (int i = 0 ; i < filters.length ; i++) {
      if (filters[i]!=null && !filters[i].initialise()) return false;
    }
    //this.start();
    //return true;
    //return this.start();
    return true;
  }

  public boolean applyMeatToBuffers() {
    boolean frameExited = true;
    for (int i = 0 ; i < filters.length && frameExited ; i++) {
      if (filters[i]!=null && !filters[i].muted) {
        //System.out.println("processing filterchain subfilter " + filters[i]);
        filters[i].beginDraw();
        filters[i].applyToBuffers();
        filters[i].endDraw();
        if (!filters[i].drewFrame) {
          //System.out.println(this.getDescription() + " is bailing out because " + filters[i] + " didn't return true from applytobuffers.");
          frameExited = false;
        }
      }
    }
    return true;
  }

  public void beginDraw() {}
  public void endDraw() {}

  public void meat_beginDraw() {
    for (int i = 0 ; i < filters.length ; i++) {
      if (filters[i]!=null) {
        filters[i].beginDraw();
      }
    }
  }

  public void meat_endDraw() {
    for (int i = 0 ; i < filters.length ; i++) {
      if (filters[i]!=null) {
        filters[i].endDraw();
      }
    }
  }

}
