package vurfeclipse.streams;

import java.io.Serializable;


/*interface ParameterCallback {
  public void call (Object value);
}*/
public class ParameterCallback implements Serializable {
  /*public void call(String eventName, int time, Object value) {
    call(value);
  }*/
	
  
  boolean shouldDie = false;
  
  
  public void call(Object value) {
    
  }
}


