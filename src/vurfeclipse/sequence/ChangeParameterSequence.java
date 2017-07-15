package vurfeclipse.sequence;

import java.util.ArrayList;

import vurfeclipse.APP;
import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ChangeParameterSequence extends Sequence {
	String filterPath, parameterName;
	Object value;
	
	public ChangeParameterSequence(Scene host, String filterPath, String parameterName, Object value, int length) {
		//super(host,length);
		super(host,length);
		this.filterPath = filterPath;
		this.parameterName = parameterName;
		this.value = value;
	}

	@Override public ArrayList<Mutable> getMutables () {
		this.mutables = new ArrayList<Mutable> ();
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
