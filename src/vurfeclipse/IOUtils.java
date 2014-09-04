package vurfeclipse;

import java.io.*;

public class IOUtils {
  static String path = /*"D:\\code\\processing\\sketches\\vurf\\*/  APP.sketchPath("output/");// + "output\\";
  static public void makeProjectFolder(String filename) {
      java.io.File file = new java.io.File(path + filename);
      file.mkdir();
  }    
  
  static public void serialize(String filename, Serializable obj) {
    try {
      FileOutputStream f_out = new FileOutputStream(path + filename);
      serialize(f_out,obj);
      f_out.close();
    } catch (IOException e) {
      throw new IOError(e);
    }
  }    
   
  static public void serialize(OutputStream out,Serializable obj)
  {
    try {
      ObjectOutputStream stream = new ObjectOutputStream(out);
      stream.writeObject(obj);
    } catch(IOException e) {
      throw new IOError(e);
    }
  }
  
  static public <T> T deserialize(String filename, Class<? extends T> c) {
    try {
      FileInputStream f_in = new FileInputStream(path + filename);
      T r = deserialize(f_in, c);
      f_in.close();
      return r;
    } catch (IOException e) {
      throw new IOError(e);
    }
  }
  static public <T> T deserialize(InputStream in,Class<? extends T> c)
  {
    try {
      ObjectInputStream stream=new
        ObjectInputStream(in);
      T obj=c.cast(stream.readObject());
      return obj;
    } catch(IOException e) {
      throw new IOError(e);
    } catch(ClassNotFoundException e) {
      throw new IOError(e);
    }
  }
}
