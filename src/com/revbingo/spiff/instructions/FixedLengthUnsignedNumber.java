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
