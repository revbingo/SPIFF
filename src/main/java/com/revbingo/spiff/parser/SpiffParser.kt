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
