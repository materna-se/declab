package de.materna.dmn.tester.servlets.merger.entities;

import org.w3c.dom.Node;

public class DifferenceResult {
	Node specificationNode;
	Node implementationNode;

	Node specificationParentNode;
	Node implementationParentNode;

	public DifferenceResult(Node specificationNode, Node implementationNode, Node specificationParentNode,
			Node implementationParentNode) {
		this.specificationNode = specificationNode;
		this.implementationNode = implementationNode;
		this.specificationParentNode = specificationParentNode;
		this.implementationParentNode = implementationParentNode;
	}

	public Node getSpecificationNode() {
		return specificationNode;
	}

	public Node getImplementationNode() {
		return implementationNode;
	}

	public Node getSpecificationParentNode() {
		return specificationParentNode;
	}

	public Node getImplementationParentNode() {
		return implementationParentNode;
	}
}