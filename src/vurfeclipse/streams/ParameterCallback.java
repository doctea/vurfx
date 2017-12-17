package vurfeclipse.streams;

import java.io.Serializable;


/*interface ParameterCallback {
  public void call (Object value);
}*/
public abstract class ParameterCallback implements Serializable {
  /*public void call(String eventName, int time, Object value) {
    call(value);
  }*/
	
  
  boolean shouldDie = false;
  
  
  abstract public void call(Object value);
}


