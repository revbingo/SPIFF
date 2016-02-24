/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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

	//These methods are called by the evaluation engine
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
	
	public void clear() {
		varMap.clear();
	}
}
