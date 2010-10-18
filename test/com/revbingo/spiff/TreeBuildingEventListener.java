/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.ReferencedInstruction;

public class TreeBuildingEventListener implements EventListener {

	DefaultTreeModel model;
	MutableTreeNode currentNode;
	
	public TreeBuildingEventListener() {
		currentNode = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(currentNode);
	}
	
	@Override
	public void notifyData(ReferencedInstruction ins) {
		MutableTreeNode thisNode = new DefaultMutableTreeNode("<html><i>"  + ins.name + "</i> : <b>" + ins.value + "</b></html>");
		model.insertNodeInto(thisNode, currentNode, currentNode.getChildCount());
	}

	@Override
	public void notifyGroup(String groupName, boolean start) {
		if(start) {
			MutableTreeNode groupNode = new DefaultMutableTreeNode(groupName);
			model.insertNodeInto(groupNode, currentNode, currentNode.getChildCount());
			currentNode = groupNode;
		} else {
			currentNode = (MutableTreeNode) currentNode.getParent();
		}
	}
	
	public TreeModel getTree() { return model; }
}
