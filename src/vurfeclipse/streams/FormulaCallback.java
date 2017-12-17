package vurfeclipse.streams;

import java.math.BigDecimal;

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
	
	public FormulaCallback setTargetPath(String targetPath) {
		this.targetPath = targetPath;
		return this;
	}

	@Override
	public void call(Object value) {
		if (value instanceof Float || value instanceof Double) {
			e.setVariable("input", BigDecimal.valueOf((float)value));
		} else if (value instanceof Integer || value instanceof Long) {
			e.setVariable("input", BigDecimal.valueOf((Integer)value));
		} 
		Targetable target = (Targetable) APP.getApp().pr.getObjectForPath(targetPath);
		//Targetable source = (Targetable) APP.getApp().pr.getObjectForPath(sourcePath);
		
		if (value instanceof Float || value instanceof Double) {
			target.target(targetPath, e.eval().floatValue());
		} else if (value instanceof Integer || value instanceof Long) {
			target.target(targetPath, e.eval().intValue());
		} else if (value instanceof Boolean) {
			target.target(targetPath, e.eval().floatValue()==1.0f);
		}
	}
	
	public String toString() {
		return this.getClass().getSimpleName()+ " { expression => '"+expression+"', targetPath => '"+targetPath+"' } ";
	}
	
}
