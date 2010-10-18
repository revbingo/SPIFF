/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

public abstract class FixedLengthUnsignedNumber extends ReferencedInstruction {

	protected int[] convertBytesToInts(byte[] bytes){
		int[] ubytes=new int[bytes.length];
		for(int i=0;i<bytes.length;i++){
			ubytes[i] = (0x000000FF & ((int)bytes[i]));
		}
		return ubytes;
	}

}
