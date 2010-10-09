package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventListener;

public interface Instruction {

	void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException;
}
