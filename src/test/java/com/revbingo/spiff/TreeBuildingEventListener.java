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
package com.revbingo.spiff;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;

public class TreeBuildingEventListener implements EventListener {

	DefaultTreeModel model;
	MutableTreeNode currentNode;
	
	public TreeBuildingEventListener() {
		currentNode = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(currentNode);
	}
	
	@Override
	public void notifyData(Datatype ins) {
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
