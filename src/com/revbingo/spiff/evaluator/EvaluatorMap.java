package com.revbingo.spiff.evaluator;

import gnu.jel.DVMap;

import java.util.HashMap;

public class EvaluatorMap extends DVMap {

	private HashMap<String, Object> varMap = new HashMap<String, Object>();	
	
	@Override
	public String getTypeName(String var) {
		Object o = varMap.get(var);
		if(o == null) return null;
		return o.getClass().getSimpleName();
	}
	
	public void addVariable(String var, Object obj) throws IllegalArgumentException {
		varMap.put(var, obj);
	}
	
	public long getLongProperty(String name){
		return getProperty(name, Long.class);
	}
	
	public int getIntegerProperty(String name){
		return getProperty(name, Integer.class);
	}
	
	public short getShortProperty(String name){
		return getProperty(name, Short.class);
	}
	
	public byte getByteProperty(String name){
		return getProperty(name, Byte.class);
	}
	
	public double getDoubleProperty(String name){
		return getProperty(name, Double.class);
	}
	
	public float getFloatProperty(String name){
		return getProperty(name, Float.class);
	}
	
	public String getStringProperty(String name){
		return getProperty(name, String.class);
	}
	
	public Boolean getBooleanProperty(String name) {
		return getProperty(name, Boolean.class);
	}
	
	public <T> T getProperty(String name, Class<T> type) {
		Object o = varMap.get(name);
		return (o == null) ? null : type.cast(o);
	}
}
