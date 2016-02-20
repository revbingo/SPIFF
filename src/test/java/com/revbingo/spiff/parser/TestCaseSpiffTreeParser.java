/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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

		assertThat(node.jjtGetNumChildren(), is(1));
		assertThat(node.jjtGetChild(0), instanceOf(SimpleNode.class));
		List<SimpleNode> expressionNodes = findNodes(node, ASTexpression.class);
		assertThat(expressionNodes.size(), is(1));
	}

	@Test
	public void expressionsWithMultipleEmbeddedExpressionsProduceSingleNode() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(new StringReader(".jump x+3*(x/(9+2))\n"));
		SimpleNode node = parser.adf();

		assertThat(node.jjtGetNumChildren(), is(1));
		assertThat(node.jjtGetChild(0), instanceOf(SimpleNode.class));
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
