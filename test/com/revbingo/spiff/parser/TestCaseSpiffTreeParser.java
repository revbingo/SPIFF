package com.revbingo.spiff.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.StringReader;

import org.junit.Test;

public class TestCaseSpiffTreeParser {

	@Test
	public void expressionsProduceSingleNode() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(new StringReader(".jump x+3\n"));
		SimpleNode node = parser.adf();

		assertThat(node.jjtGetNumChildren(), is(2));
		assertThat(node.jjtGetChild(1), instanceOf(SimpleNode.class));
		SimpleNode expressionNode = findNode(node, "expression");
		assertThat(expressionNode, is(notNullValue()));
		assertThat(expressionNode.jjtGetNumChildren(), is(0));
	}

	private SimpleNode findNode(SimpleNode rootNode, String expectedName) {
		SimpleNode n = rootNode;
		for(int i=0; i < n.jjtGetNumChildren(); i++) {
			SimpleNode childNode = (SimpleNode) n.jjtGetChild(i);
			if(childNode.jjtGetNumChildren() > 0) return findNode(childNode, expectedName);
			if(expectedName.equals(SpiffTreeParserTreeConstants.jjtNodeName[(childNode).id])) {
				return childNode;
			}
		}
		return null;
	}
}
