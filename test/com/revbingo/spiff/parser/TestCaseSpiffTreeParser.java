package com.revbingo.spiff.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.parser.gen.ASTexpression;
import com.revbingo.spiff.parser.gen.ParseException;
import com.revbingo.spiff.parser.gen.SimpleNode;
import com.revbingo.spiff.parser.gen.SpiffTreeParser;

public class TestCaseSpiffTreeParser {

	@Test
	public void simpleExpressionsProduceSingleNode() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(new StringReader(".jump x+3/4\n"));
		SimpleNode node = parser.adf();

		assertThat(node.jjtGetNumChildren(), is(2));
		assertThat(node.jjtGetChild(1), instanceOf(SimpleNode.class));
		List<SimpleNode> expressionNodes = findNodes(node, ASTexpression.class);
		assertThat(expressionNodes.size(), is(1));
	}

	@Test
	public void expressionsWithMultipleEmbeddedExpressionsProduceSingleNode() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(new StringReader(".jump x+3*(x/(9+2))\n"));
		SimpleNode node = parser.adf();

		node.dump("");
		assertThat(node.jjtGetNumChildren(), is(2));
		assertThat(node.jjtGetChild(1), instanceOf(SimpleNode.class));
		List<SimpleNode> expressionNodes = findNodes(node, ASTexpression.class);
		assertThat(expressionNodes.size(), is(1));
	}

	private List<SimpleNode> findNodes(SimpleNode rootNode, Class expectedType) {
		List<SimpleNode> foundNodes = new ArrayList<SimpleNode>();
		SimpleNode n = rootNode;
		for(int i=0; i < n.jjtGetNumChildren(); i++) {
			SimpleNode childNode = (SimpleNode) n.jjtGetChild(i);
			if(childNode.jjtGetNumChildren() > 0) foundNodes.addAll(findNodes(childNode, expectedType));
			if(childNode.getClass().equals(expectedType)) {
				foundNodes.add(childNode);
			}
		}
		return foundNodes;
	}
}
