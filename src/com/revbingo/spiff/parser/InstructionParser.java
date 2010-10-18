/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.parser;

import java.util.List;

import com.revbingo.spiff.instructions.Instruction;

public interface InstructionParser {

	public void start() throws ParseException;
	public List<Instruction> getInstructions();
}
