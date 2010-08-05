package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventDispatcher;

public abstract class Instruction {

	public abstract void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException;
}
