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
