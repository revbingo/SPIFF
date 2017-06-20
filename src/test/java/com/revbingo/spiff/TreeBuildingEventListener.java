package com.revbingo.spiff;

import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

public class TreeBuildingEventListener implements EventListener {

	DefaultTreeModel model;
	MutableTreeNode currentNode;
	
	public TreeBuildingEventListener() {
		currentNode = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(currentNode);
	}
	
	@Override
	public void notifyData(Datatype ins) {
		MutableTreeNode thisNode = new DefaultMutableTreeNode("<html><i>"  + ins.getName() + "</i> : <b>" + ins.getValue() + "</b></html>");
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
