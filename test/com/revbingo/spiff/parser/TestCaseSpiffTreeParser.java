package com.revbingo.spiff.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestCaseSpiffTreeParser {

	@Test
	public void simpleExpressionsProduceSingleNode() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(new StringReader(".jump x+3/4\n"));
		SimpleNode node = parser.adf();

		assertThat(node.jjtGetNumChildren(), is(2));
		assertThat(node.jjtGetChild(1), instanceOf(SimpleNode.class));
		List<SimpleNode> expressionNodes = findNodes(node, "expression");
		assertThat(expressionNodes.size(), is(1));
	}

	@Test
	public void expressionsWithMultipleEmbeddedExpressionsProduceSingleNode() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(new StringReader(".jump x+3*(x/(9+2))\n"));
		SimpleNode node = parser.adf();

		node.dump("");
		assertThat(node.jjtGetNumChildren(), is(2));
		assertThat(node.jjtGetChild(1), instanceOf(SimpleNode.class));
		List<SimpleNode> expressionNodes = findNodes(node, "expression");
		assertThat(expressionNodes.size(), is(1));
	}

	private List<SimpleNode> findNodes(SimpleNode rootNode, String expectedName) {
		List<SimpleNode> foundNodes = new ArrayList<SimpleNode>();
		SimpleNode n = rootNode;
		for(int i=0; i < n.jjtGetNumChildren(); i++) {
			SimpleNode childNode = (SimpleNode) n.jjtGetChild(i);
			if(childNode.jjtGetNumChildren() > 0) foundNodes.addAll(findNodes(childNode, expectedName));
			if(expectedName.equals(SpiffTreeParserTreeConstants.jjtNodeName[(childNode).id])) {
				foundNodes.add(childNode);
			}
		}
		return foundNodes;
	}
}
