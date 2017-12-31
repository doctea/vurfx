package vurfeclipse.streams;

import java.math.BigDecimal;
import java.util.HashMap;

import com.udojava.evalex.*;

import vurfeclipse.APP;
import vurfeclipse.Targetable;

public class FormulaCallback extends ParameterCallback {

	String targetPath;
	//String sourcePath;
	
	String expression;
	
	com.udojava.evalex.Expression e;
	
	public FormulaCallback() {
		e = new com.udojava.evalex.Expression(expression);
	}
	
	public FormulaCallback setExpression(String expression) {
		this.expression = expression;
		e = new com.udojava.evalex.Expression(expression);
		return this;
	}
	public String getExpression () {
		return expression;
	}
	
	public FormulaCallback setTargetPath(String targetPath) {
		this.targetPath = targetPath;
		return this;
	}
	
	@Override
	public HashMap<String,Object> collectParameters () {
		HashMap<String,Object> params = super.collectParameters();
		params.put("expression",  this.expression);
		params.put("targetPath", this.targetPath);
		return params;
	}
	
	public void readParameters(HashMap<String, Object> input) {
		this.setTargetPath((String) input.get("targetPath"));
		this.setExpression((String) input.get("expression"));
	}

	@Override
	public void call(Object value) {
		if (value instanceof Float || value instanceof Double) {
			e.setVariable("input", BigDecimal.valueOf((Float)value));
		} else if (value instanceof Integer || value instanceof Long) {
			e.setVariable("input", BigDecimal.valueOf((Integer)value));
		} 
		Targetable target = (Targetable) APP.getApp().pr.getObjectForPath(targetPath);
		//Targetable source = (Targetable) APP.getApp().pr.getObjectForPath(sourcePath);
		
		if (target==null) {
			System.err.println("Caught a null target for path " + targetPath + " in " + this + "!");
		}
		
		if (value instanceof Float || value instanceof Double) {
			target.target(targetPath, e.eval().floatValue());
		} else if (value instanceof Integer || value instanceof Long) {
			target.target(targetPath, e.eval().intValue());
		} else if (value instanceof Boolean) {
			target.target(targetPath, e.eval().floatValue()==1.0f);
		}
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+ " { expression => '"+expression+"', targetPath => '"+targetPath+"' } ";
	}
	
	
	
}
