package com.revbingo.spiff.parser

import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.HashMap

import com.revbingo.spiff.AdfFormatException
import com.revbingo.spiff.datatypes.*
import com.revbingo.spiff.instructions.*
import com.revbingo.spiff.parser.gen.*
import com.revbingo.spiff.parser.gen.SpiffTreeParserConstants.*

class SpiffVisitor : SpiffTreeParserVisitor {

    private var defaultEncoding = Charset.defaultCharset().displayName()

    private val defines = HashMap<String, List<Instruction>>()
    private val datatypes = HashMap<String, Class<Datatype>>()

    fun SimpleNode.getLastTokenImage(): String = this.jjtGetLastToken().image
    fun SimpleNode.getFirstTokenImage(): String = this.jjtGetFirstToken().image

    fun SimpleNode.findTokenValue(kind: Int, count: Int = 1): String? {
        var count = count
        var t = this.jjtGetFirstToken()
        do {
            if (t.kind == kind && --count == 0) return t.image
            t = t.next
        } while (t !== this.jjtGetLastToken().next)
        return null
    }

    fun SimpleNode.getExpression(): String {
        val exprNode = this.jjtGetChild(0) as ASTexpression
        var t = exprNode.jjtGetFirstToken()
        val expression = StringBuffer()
        do {
            var tokenText = t.image
            if (t.kind == SpiffTreeParserConstants.ID_ADDRESS) {
                tokenText = tokenText.substring(1, tokenText.length) + ".address"
            }
            expression.append(tokenText)
            t = t.next
        } while (t !== exprNode.jjtGetLastToken().next)
        return expression.toString()
    }


    override fun visit(node: SimpleNode, data: List<Instruction>): List<Instruction> {
        node.childrenAccept(this, data)
        return data
    }

    override fun visit(node: ASTadf, data: List<Instruction>): List<Instruction> {
        node.childrenAccept(this, data)
        return data
    }

    override fun visit(node: ASTdatatypeDef, data: List<Instruction>): List<Instruction> {
        val className = node.findTokenValue(CLASS)
        try {
            val datatypeClass = Class.forName(className) as Class<Datatype>
            if (!Datatype::class.java.isAssignableFrom(datatypeClass)) {
                throw AdfFormatException("Custom datatype $datatypeClass does not extend com.revbingo.spiff.datatypes.Datatype")
            }
            val identifier = node.findTokenValue(IDENTIFIER) ?: throw AdfFormatException("Can't find identifier for ${node.getLastTokenImage()}")
            datatypes.put(identifier, datatypeClass)
        } catch (e: ClassNotFoundException) {
            throw AdfFormatException("Unknown datatype class " + className)
        }

        return data
    }

    override fun visit(node: ASTlist, data: List<Instruction>): List<Instruction> {
        node.childrenAccept(this, data)
        return data
    }

    override fun visit(node: ASTuserDefinedType, data: MutableList<Instruction>): List<Instruction> {
        val typeName = node.findTokenValue(IDENTIFIER, 1)
        val identifier = node.findTokenValue(IDENTIFIER, 2)
        val userType = datatypes[typeName] ?: throw AdfFormatException("Undefined datatype " + typeName)
        try {
            val inst = userType.newInstance()
            inst.name = identifier
            return decorateAndAdd(node, inst, data)
        } catch (e: InstantiationException) {
            throw AdfFormatException("Custom datatype ${userType.name} does not have a no-args constructor or threw an exception")
        } catch (e: IllegalAccessException) {
            throw AdfFormatException("Custom datatype ${userType.name} does not have a publically accessible no args constructor")
        }
    }

    override fun visit(node: ASTbits, data: MutableList<Instruction>): List<Instruction> {
        val inst = BitsInstruction()
        inst.name = node.getLastTokenImage()
        inst.numberOfBitsExpr = node.getExpression()
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTbytes, data: MutableList<Instruction>): List<Instruction> {
        val inst = BytesInstruction()
        inst.name = node.getLastTokenImage()
        inst.lengthExpr = node.getExpression()

        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTincludeInstruction,
                       data: MutableList<Instruction>): List<Instruction> {
        val include = defines[node.getLastTokenImage()] ?: throw AdfFormatException("Could not get defined block with name " + node.getLastTokenImage())
        data.addAll(include)
        return data
    }

    override fun visit(node: ASTdefineInstruction,
                       data: List<Instruction>): List<Instruction> {
        val nestedInstructions = ArrayList<Instruction>()
        node.childrenAccept(this, nestedInstructions)

        val identifier = node.findTokenValue(IDENTIFIER)!!
        defines.put(identifier, nestedInstructions)
        return data
    }

    override fun visit(node: ASTsetEncodingInstruction,
                       data: List<Instruction>): List<Instruction> {
        this.defaultEncoding = node.findTokenValue(ENCODING)
        return data
    }

    override fun visit(node: ASTsetInstruction,
                       data: MutableList<Instruction>): List<Instruction> {

        val inst = SetInstruction(node.getExpression(), node.findTokenValue(IDENTIFIER))
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTifElseBlock, data: MutableList<Instruction>): List<Instruction> {
        val inst = IfBlock()

        inst.ifExpression = node.getExpression()

        val ifInsts = node.jjtGetChild(1).jjtAccept(this, ArrayList<Instruction>())
        inst.instructions = ifInsts

        if (node.jjtGetNumChildren() == 3) {
            val elseInsts = node.jjtGetChild(2).jjtAccept(this, ArrayList<Instruction>())
            inst.setElseInstructions(elseInsts)
        }
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTsetOrderInstruction,
                       data: MutableList<Instruction>): List<Instruction> {

        val order = when(node.findTokenValue(BYTEORDER)) {
            "LITTLE_ENDIAN" -> ByteOrder.LITTLE_ENDIAN
            else -> ByteOrder.BIG_ENDIAN
        }
        val inst = SetOrderInstruction(order)
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTrepeatInstruction,
                       data: MutableList<Instruction>): List<Instruction> {

        val inst = RepeatBlock()
        inst.repeatCountExpression = node.getExpression()
        val nestedInstructions = ArrayList<Instruction>()
        inst.instructions = nestedInstructions
        node.childrenAccept(this, inst.instructions)
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTgroupInstruction,
                       data: MutableList<Instruction>): List<Instruction> {
        val groupName = node.findTokenValue(IDENTIFIER)
        decorateAndAdd(node, GroupInstruction(groupName!!), data)
        node.childrenAccept(this, data)

        val endGroupInst = EndGroupInstruction(groupName)
        endGroupInst.lineNumber = node.jjtGetLastToken().beginLine
        data.add(endGroupInst)
        return data
    }

    override fun visit(node: ASTjumpInstruction,
                       data: MutableList<Instruction>): List<Instruction> {
        val inst = JumpInstruction(node.getExpression())
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTskipInstruction,
                       data: MutableList<Instruction>): List<Instruction> {
        val inst = SkipInstruction()
        inst.expression = node.getExpression()
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTmarkInstruction,
                       data: MutableList<Instruction>): List<Instruction> {
        val inst = MarkInstruction(node.getLastTokenImage())
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTfixedNumber, data: MutableList<Instruction>): List<Instruction> {
        val insF = FixedLengthNumberFactory()
        val inst = insF.getInstruction(node.getFirstTokenImage())
        if (node.jjtGetNumChildren() == 1) {
            inst.literalExpr = node.getExpression()
        }
        inst.name = node.getLastTokenImage()
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTexpression, data: List<Instruction>): List<Instruction> {
        return data
    }

    private fun decorateAndAdd(node: SimpleNode, inst: Instruction, list: MutableList<Instruction>): List<Instruction> {
        if (inst is AdfInstruction) {
            inst.lineNumber = node.jjtGetFirstToken().beginLine
        }
        list.add(inst)
        return list
    }

    override fun visit(node: ASTfixedString, data: MutableList<Instruction>): List<Instruction> {
        var encoding = node.findTokenValue(ENCODING) ?: defaultEncoding

        val inst = FixedLengthString(encoding)
        inst.lengthExpr = node.getExpression()

        inst.name = node.getLastTokenImage()
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTliteralString, data: MutableList<Instruction>): List<Instruction> {
        var encoding: String = node.findTokenValue(ENCODING) ?: defaultEncoding

        val literal = (node.jjtGetChild(0) as ASTIdentifier).getFirstTokenImage()
        val inst = LiteralStringInstruction(literal, encoding)

        inst.name = node.getLastTokenImage()
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTterminatedString, data: MutableList<Instruction>): List<Instruction> {
        var encoding: String = node.findTokenValue(ENCODING) ?: defaultEncoding

        val inst = TerminatedString(encoding)

        inst.name = node.getLastTokenImage()
        return decorateAndAdd(node, inst, data)
    }

    override fun visit(node: ASTIdentifier, data: List<Instruction>): List<Instruction>? {
        return null
    }
}
