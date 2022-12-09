package de.materna.dmn.tester.servlets.merger.comparisons;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.materna.dmn.tester.enums.DifferenceErrorSeverity;
import de.materna.dmn.tester.servlets.merger.entities.DifferenceError;
import de.materna.dmn.tester.servlets.merger.entities.DifferenceResult;

public class ComparisonCollection {
	private static ComparisonCollection instance;
	private final List<Function<DifferenceResult, List<DifferenceError>>> attributeAdditionComparisons = new ArrayList<>();
	private final List<Function<DifferenceResult, List<DifferenceError>>> attributeChangeComparisons = new ArrayList<>();
	private final List<Function<DifferenceResult, List<DifferenceError>>> attributeRemovalComparisons = new ArrayList<>();
	private final List<Function<DifferenceResult, List<DifferenceError>>> entityAdditionComparisons = new ArrayList<>();
	private final List<Function<DifferenceResult, List<DifferenceError>>> entityRemovalComparisons = new ArrayList<>();
	private final List<Function<DifferenceResult, List<DifferenceError>>> entityChangeComparisons = new ArrayList<>();

	private final String parentsTags[] = { "definitions", "itemComponent", "decision", "businessKnowledgeModel",
			"variable" };
	private final String childrenTags[] = { "name", "typeRef" };

	private boolean tagEquals(Node node, String tag) {
		return node != null && node.getLocalName().equals(tag);
	}

	private ComparisonCollection() {
		attributeAdditionComparisons.add(differenceResult -> {
			final List<DifferenceError> diffErrors = new ArrayList<>();
			for (final String parentTag : parentsTags) {
				final Node specEntityNode = differenceResult.getSpecificationParentNode();
				final Node implEntityNode = differenceResult.getImplementationParentNode();
				if (tagEquals(specEntityNode, parentTag) && tagEquals(implEntityNode, parentTag)) {
					for (final String childTag : childrenTags) {
						final Node implChildNode = implEntityNode.getAttributes().getNamedItem(childTag);
						if (differenceResult.getSpecificationNode() == null && tagEquals(implChildNode, childTag)) {
							final String implEntityTag = implEntityNode.getParentNode().getLocalName();
							final String implEntityName = implEntityNode.getParentNode().getAttributes()
									.getNamedItem("name").getNodeValue();
							final String implText = implChildNode.getTextContent();
							diffErrors.add(new DifferenceError(DifferenceErrorSeverity.INFO,
									String.format(
											"The implementation has an additional, undemanded %s \"%s\" in %s \"%s\".",
											childTag, implText, implEntityTag, implEntityName)));
						}
					}
				}
			}
			return diffErrors;
		});

		attributeChangeComparisons.add(differenceResult -> {
			final List<DifferenceError> diffErrors = new ArrayList<>();
			for (final String parentTag : parentsTags) {
				final Node specEntityNode = differenceResult.getSpecificationParentNode();
				final Node implEntityNode = differenceResult.getImplementationParentNode();
				if (tagEquals(specEntityNode, parentTag) && tagEquals(implEntityNode, parentTag)) {
					for (final String childTag : childrenTags) {
						final Node specChildNode = specEntityNode.getAttributes().getNamedItem(childTag);
						final Node implChildNode = implEntityNode.getAttributes().getNamedItem(childTag);
						if (tagEquals(specChildNode, childTag) && tagEquals(implChildNode, childTag)) {
							final String specEntityTag = specEntityNode.getLocalName();
							final NamedNodeMap attributes = specEntityNode.getParentNode().getAttributes();
							if (attributes != null) {
								final String specEntityName = attributes.getNamedItem("name").getNodeValue();
								final String specText = differenceResult.getSpecificationNode().getTextContent();
								final String implText = differenceResult.getImplementationNode().getTextContent();
								diffErrors.add(new DifferenceError(DifferenceErrorSeverity.WARNING,
										String.format("The %s \"%s\" changed its %s from \"%s\" to \"%s\".",
												specEntityTag, specEntityName, childTag, specText, implText)));
							}
						}
					}
				}
			}
			return diffErrors;
		});

		attributeRemovalComparisons.add(differenceResult -> {
			final List<DifferenceError> diffErrors = new ArrayList<>();
			for (final String parentTag : parentsTags) {
				final Node specEntityNode = differenceResult.getSpecificationParentNode();
				final Node implEntityNode = differenceResult.getImplementationParentNode();
				if (tagEquals(specEntityNode, parentTag) && tagEquals(implEntityNode, parentTag)) {
					for (final String childTag : childrenTags) {
						final Node specChildNode = specEntityNode.getAttributes().getNamedItem(childTag);
						if (tagEquals(specChildNode, childTag) && differenceResult.getImplementationNode() == null) {
							final String specEntityTag = specEntityNode.getParentNode().getLocalName();
							final String specEntityName = specEntityNode.getParentNode().getAttributes()
									.getNamedItem("name").getNodeValue();
							final String specText = specChildNode.getTextContent();
							diffErrors.add(new DifferenceError(DifferenceErrorSeverity.ERROR,
									String.format("The %s \"%s\" is missing its demanded %s \"%s\".", specEntityTag,
											specEntityName, childTag, specText)));
						}
					}
				}
			}
			return diffErrors;
		});

		entityAdditionComparisons.add(differenceResult -> {
			final List<DifferenceError> diffErrors = new ArrayList<>();
			for (final String parentTag : parentsTags) {
				if (differenceResult.getSpecificationNode() == null && differenceResult.getImplementationNode() != null
						&& tagEquals(differenceResult.getImplementationNode(), parentTag)) {
					final String implEntityName = differenceResult.getImplementationNode().getAttributes()
							.getNamedItem("name").getNodeValue();
					diffErrors.add(new DifferenceError(DifferenceErrorSeverity.INFO, String.format(
							"The implementation has an additional, undemanded %s \"%s\".", parentTag, implEntityName)));
				}
			}
			return diffErrors;
		});

		entityChangeComparisons.add(differenceResult -> {
			final List<DifferenceError> diffErrors = new ArrayList<>();
			for (final String parentTag : parentsTags) {
				final Node specEntityNode = differenceResult.getSpecificationParentNode();
				final Node implEntityNode = differenceResult.getImplementationParentNode();
				if (tagEquals(specEntityNode.getParentNode(), parentTag)
						&& tagEquals(implEntityNode.getParentNode(), parentTag)) {
					for (final String childTag : childrenTags) {
						if (tagEquals(specEntityNode, childTag) && tagEquals(implEntityNode, childTag)) {
							final String specEntityTag = specEntityNode.getLocalName();
							final String specEntityName = specEntityNode.getParentNode().getAttributes()
									.getNamedItem("name").getNodeValue();
							final String specText = differenceResult.getSpecificationNode().getTextContent();
							final String implText = differenceResult.getImplementationNode().getTextContent();
							diffErrors.add(new DifferenceError(DifferenceErrorSeverity.WARNING,
									String.format("The %s \"%s\" changed its %s from \"%s\" to \"%s\".", specEntityTag,
											specEntityName, childTag, specText, implText)));
						}
					}
				}
			}
			return diffErrors;
		});

		entityRemovalComparisons.add(differenceResult -> {
			final List<DifferenceError> diffErrors = new ArrayList<>();
			for (final String parentTag : parentsTags) {
				if (differenceResult.getSpecificationNode() != null && differenceResult.getImplementationNode() == null
						&& tagEquals(differenceResult.getSpecificationNode(), parentTag)) {
					final String specEntityName = differenceResult.getSpecificationNode().getAttributes()
							.getNamedItem("name").getNodeValue();
					diffErrors.add(new DifferenceError(DifferenceErrorSeverity.ERROR, String.format(
							"The implementation is missing the demanded %s \"%s\".", parentTag, specEntityName)));
				}
			}
			return diffErrors;
		});
	}

	public static ComparisonCollection getInstance() {
		if (instance == null) {
			instance = new ComparisonCollection();
		}
		return instance;
	}

	public List<Function<DifferenceResult, List<DifferenceError>>> getAttributeAdditionComparisons() {
		return attributeAdditionComparisons;
	}

	public List<Function<DifferenceResult, List<DifferenceError>>> getAttributeChangeComparisons() {
		return attributeChangeComparisons;
	}

	public List<Function<DifferenceResult, List<DifferenceError>>> getAttributeRemovalComparisons() {
		return attributeRemovalComparisons;
	}

	public List<Function<DifferenceResult, List<DifferenceError>>> getEntityAdditionComparisons() {
		return entityAdditionComparisons;
	}

	public List<Function<DifferenceResult, List<DifferenceError>>> getEntityChangeComparisons() {
		return entityChangeComparisons;
	}

	public List<Function<DifferenceResult, List<DifferenceError>>> getEntityRemovalComparisons() {
		return entityRemovalComparisons;
	}
}
