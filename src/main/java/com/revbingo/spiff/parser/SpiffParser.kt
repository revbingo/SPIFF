/*
 * Copyright Mark Piper 2016
 *
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
 */

package com.revbingo.spiff.parser

import com.revbingo.spiff.instructions.Instruction
import com.revbingo.spiff.parser.gen.ParseException
import com.revbingo.spiff.parser.gen.SpiffTreeParser
import java.io.InputStream
import java.util.*

interface InstructionParser {

    @Throws(ParseException::class)
    fun parse(): List<Instruction>
}


class SpiffParser(private val input: InputStream) : InstructionParser {

    @Throws(ParseException::class)
    override fun parse(): List<Instruction> {
        val parser = SpiffTreeParser(input)
        val rootNode = parser.adf()
        val visitor = SpiffVisitor()

        return rootNode.jjtAccept(visitor, ArrayList<Instruction>())
    }

}
