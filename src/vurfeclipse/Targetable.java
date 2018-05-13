package vurfeclipse;

import java.util.HashMap;

public interface Targetable {
	abstract Object target(String path, Object payload);
	//abstract Map<String,Targetable> getTargetURLs();

	abstract public HashMap<String, Targetable> getTargetURLs();
}