package vurfeclipse.streams;

public class DieAfterParameterCallback extends ParameterCallback {
	  
	  
	  int limit = 5;
	  int counter = 0;
	  protected Object initial = (Integer)0;
	  
	  public DieAfterParameterCallback (int l) {
	    super();
	    this.limit = l;
	  }
	  

	synchronized public void call(Object value) {
	    if ((Integer)initial!=0) initial = value;
	    counter++;
	    if (counter>=limit) {
	      shouldDie = true;
	    }
	  }
	}