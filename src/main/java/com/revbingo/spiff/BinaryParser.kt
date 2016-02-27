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

package com.revbingo.spiff

import com.revbingo.spiff.events.EventListener
import com.revbingo.spiff.instructions.Instruction
import com.revbingo.spiff.parser.InstructionParser
import com.revbingo.spiff.parser.SpiffParser
import com.revbingo.spiff.parser.gen.ParseException
import com.revbingo.spiff.vm.NullEventListener
import com.revbingo.spiff.vm.SpiffVm
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer

class BinaryParser(private val eventDispatcher: EventListener = NullEventListener()) {

    @Throws(AdfFormatException::class, ExecutionException::class)
    fun parse(adfFile: File, parseFile: File) {
        val instructions = parseAdf(adfFile)
        read(parseFile, instructions)
    }

    @Throws(AdfFormatException::class)
    fun parseAdf(adfFile: File): List<Instruction> {
        try {
            val fis = FileInputStream(adfFile)
            return parseAdf(SpiffParser(fis))
        } catch (e: FileNotFoundException) {
            throw AdfFormatException("File ${adfFile.absolutePath} does not exist")
        }
    }

    @Throws(AdfFormatException::class)
    fun parseAdf(parser: InstructionParser): List<Instruction> {
        try {
            return parser.parse()
        } catch (e: ParseException) {
            throw AdfFormatException("Error in adf file", e)
        }

    }

    @Throws(ExecutionException::class)
    fun read(binaryFile: File, instructions: List<Instruction>) {
        try {
            val fc = FileInputStream(binaryFile).channel
            val buffer = ByteBuffer.allocate(binaryFile.length().toInt())
            fc.read(buffer)
            buffer.flip()

            val vm = SpiffVm(instructions, buffer, eventDispatcher)

            vm.start()

            fc.close()
        } catch (e: IOException) {
            throw ExecutionException("Could not read file ${binaryFile.absolutePath}", e)
        }

    }
}
