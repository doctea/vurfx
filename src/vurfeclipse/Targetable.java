package vurfeclipse;

public interface Targetable {
	abstract Object target(String path, Object payload);
	//abstract Map<String,Targetable> getTargetURLs();
}