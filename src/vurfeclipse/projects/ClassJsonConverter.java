package vurfeclipse.projects;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ClassJsonConverter implements JsonSerializer<Class>, JsonDeserializer<Class> {

    public ClassJsonConverter(){
        super();
    }

    @Override
    public JsonElement serialize(Class src, Type srcType, JsonSerializationContext context) {
        String str = src.getName();
        return new JsonPrimitive(str);
    }

    @Override
    public Class deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {

        Class returnValue = null; 
        try {
            String str = json.getAsString();
            returnValue = Class.forName(str);
            return returnValue;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (ClassNotFoundException e) {
            String mess = "deserialize(): " 
                    + (e.getMessage()!=null?". "+e.getMessage():"")
                    + (e.getCause()!=null?". "+e.getCause():"");
        }

        return returnValue;
    }
}