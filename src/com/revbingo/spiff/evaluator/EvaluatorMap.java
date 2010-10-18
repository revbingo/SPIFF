/*******************************************************************************
 * This file is part of SPIFF.
 * 
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
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
