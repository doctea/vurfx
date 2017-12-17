package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.HashMap;

import vurfeclipse.APP;
import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ChangeParameterSequence extends Sequence {
	String filterPath, parameterName;
	Object value;
	
	public ChangeParameterSequence() { super(); }
	
	public ChangeParameterSequence(Scene host, String filterPath, String parameterName, Object value, int length) {
		//super(host,length);
		super(host,length);
		this.filterPath = filterPath;
		this.parameterName = parameterName;
		this.value = value;
	}
	
	@Override
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		params.put("filterPath",  filterPath);
		params.put("parameterName", parameterName);
		params.put("value", this.value);
		return params;
	}
	
	@Override
	public void loadParameters(HashMap<String,Object> params) {
		super.loadParameters(params);
		if (params.containsKey("filterPath")) this.filterPath = (String) params.get("filterPath");
		if (params.containsKey("parameterName")) this.parameterName = (String) params.get("parameterName");
		if (params.containsKey("value")) this.value = (String) params.get("value");
	}

	@Override public ArrayList<Mutable> getMutables () {
		if (this.mutables==null) this.mutables = super.getMutables();//new ArrayList<Mutable> ();
		/*try {
			System.out.println(host.host.getObjectForPath(this.filterPath).toString());
			this.mutables.add((Mutable)host.host.getObjectForPath(this.filterPath));
			System.out.println(host.host.getObjectForPath(this.filterPath).toString());
		} catch (Exception e) {
			System.out.println("caught " + e + " for " + this.filterPath);
			System.exit(1);
		}*/
		//this.mutables.add(host);			
		return this.mutables;
	}
	
	@Override
	public void setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub
		((Filter)host.host
				.getObjectForPath(filterPath))
				.changeParameterValueFromSin(parameterName, (float)Math.sin(pc));		
	}

	@Override
	public void onStart() {
		((Filter)host.host
			.getObjectForPath(filterPath))
			.changeParameterValue(parameterName, value);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		((Filter)host.host
				.getObjectForPath(filterPath))
				.changeParameterValue(parameterName, value);
	}

}
