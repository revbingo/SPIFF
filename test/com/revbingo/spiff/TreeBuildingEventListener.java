/*******************************************************************************
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
