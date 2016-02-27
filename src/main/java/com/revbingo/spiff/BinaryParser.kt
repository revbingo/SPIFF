package com.revbingo.spiff

import com.revbingo.spiff.events.EventListener
import com.revbingo.spiff.instructions.Instruction
import com.revbingo.spiff.parser.InstructionParser
import com.revbingo.spiff.parser.SpiffParser
import com.revbingo.spiff.parser.gen.ParseException
import com.revbingo.spiff.vm.SpiffVm
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer

class BinaryParser(private val eventDispatcher: EventListener?) {

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
