package de.materna.dmn.tester.servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.xml.transform.Source;

import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.ElementSelector;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

import de.materna.dmn.tester.servlets.merger.comparisons.ComparisonCollection;
import de.materna.dmn.tester.servlets.merger.entities.DifferenceError;
import de.materna.dmn.tester.servlets.merger.entities.DifferenceResult;

public class Merger {
	private static final XPathEngine pathEngine = new JAXPXPathEngine();

	public static List<DifferenceError> compare(String specContent, String implContent) {
		final List<DifferenceError> diffErrors = new ArrayList<>();

		final Source specSource = Input.fromString(specContent).build();
		final Source implSource = Input.fromString(implContent).build();

		System.out.println("Calculating differences ...");

		final Diff allDiffsSimilarAndDifferent = DiffBuilder.compare(specSource).withTest(implSource).ignoreWhitespace()
				.ignoreComments().withNodeFilter(node -> {
					final List<String> tags = Arrays.asList("definitions", "decision", "inputData", "itemDefinition",
							"itemComponent", "typeRef", "variable");
					return node.getNodeType() == Node.ELEMENT_NODE && tags.contains(node.getLocalName())
							|| node.getNodeType() == Node.TEXT_NODE
									&& tags.contains(node.getParentNode().getLocalName());
				}).withNodeMatcher(new DefaultNodeMatcher((ElementSelector) (controlElement, testElement) -> {
					if (controlElement == null || testElement == null) {
						return false;
					}

					final String controlTag = controlElement.getLocalName();
					final String testTag = testElement.getLocalName();
					if (controlTag.equals(testTag)) {
						final String controlElementName = controlElement.getAttribute("name");
						final String testElementName = testElement.getAttribute("name");
						if (Arrays.asList("decision", "inputData", "itemDefinition", "itemComponent")
								.contains(controlElement.getLocalName()) && !"".equals(controlElementName)
								&& !"".equals(testElementName) && controlElementName.equals(testElementName)) {
							System.out.println("canBeCompared (by name): " + controlTag + " (" + controlElementName
									+ ") vs. " + testTag + " (" + testElementName + ")");
							return true;
						}

						final String controlElementId = controlElement.getAttribute("id");
						final String testElementId = testElement.getAttribute("id");
						if (!"".equals(controlElementId) && !"".equals(testElementId)
								&& controlElementId.equals(testElementId)) {
							System.out.println("canBeCompared (by id): " + controlTag + " (" + controlElementId
									+ ") vs. " + testTag + " (" + testElementId + ")");
							return true;
						}

						System.out.println("canBeCompared (by tag): " + controlElement.getLocalName() + " vs "
								+ testElement.getLocalName());
						return true;
					}
					return false;
				})).withDifferenceEvaluator((comparison, comparisonResult) -> comparisonResult).build();

		System.out.println("Differences calculated.");

		for (final Difference difference : allDiffsSimilarAndDifferent.getDifferences()) {
			System.out.println(difference.getComparison().getType() + ": " + difference);

			final Comparison.Detail specDetails = difference.getComparison().getControlDetails();
			final Comparison.Detail implDetails = difference.getComparison().getTestDetails();

			Node specNode = specDetails.getXPath() == null ? null : resolveNode(specDetails.getXPath(), specSource);
			Node implNode = implDetails.getXPath() == null ? null : resolveNode(implDetails.getXPath(), implSource);

			Node specParentNode = resolveNode(specDetails.getParentXPath(), specSource);
			Node implParentNode = resolveNode(implDetails.getParentXPath(), implSource);

			final DifferenceResult diffResult = new DifferenceResult(specNode, implNode, specParentNode,
					implParentNode);
			switch (difference.getComparison().getType()) {
			case ATTR_NAME_LOOKUP:
				// Erkennt, ob ein Attribut in der Spezifikation vorhanden ist, in der
				// Implementierung allerdings fehlt.
				// Bei ATTR_NAME_LOOKUP scheint es einen Bug zu geben. Die Parent Node wird als
				// Node zurückgegeben, die Node fehlt. Wir beheben das Problem hier, indem wir
				// die Parent Node als Node setzen.
				if (implNode.getLocalName().equals(specParentNode.getLocalName())) {
					System.out.println("Fixing ATTR_NAME_LOOKUP bug ...");
					implParentNode = implNode;
					implNode = null;
				}
				if (specNode.getLocalName().equals(implParentNode.getLocalName())) {
					System.out.println("Fixing ATTR_NAME_LOOKUP bug ...");
					specParentNode = specNode;
					specNode = null;
				}

				for (final Function<DifferenceResult, List<DifferenceError>> comparison : ComparisonCollection
						.getInstance().getAttributeAdditionComparisons()) {
					diffErrors.addAll(comparison.apply(diffResult));
				}

				for (final Function<DifferenceResult, List<DifferenceError>> comparison : ComparisonCollection
						.getInstance().getAttributeRemovalComparisons()) {
					diffErrors.addAll(comparison.apply(diffResult));
				}
				break;
			case ATTR_VALUE:
				// Erkennt, ob ein Attribut in der Spezifikation einen anderen Wert hat, als in
				// der Implementierung.
				for (final Function<DifferenceResult, List<DifferenceError>> comparison : ComparisonCollection
						.getInstance().getAttributeChangeComparisons()) {
					diffErrors.addAll(comparison.apply(diffResult));
				}
				break;
			case CHILD_LOOKUP:
				// Ein Knoten hat an einer Stelle einen Kind-Knoten, an einer anderen nicht.
				for (final Function<DifferenceResult, List<DifferenceError>> comparison : ComparisonCollection
						.getInstance().getEntityAdditionComparisons()) {
					diffErrors.addAll(comparison.apply(diffResult));
				}
				for (final Function<DifferenceResult, List<DifferenceError>> comparison : ComparisonCollection
						.getInstance().getEntityRemovalComparisons()) {
					diffErrors.addAll(comparison.apply(diffResult));
				}
				break;
			case CHILD_NODELIST_LENGTH:
				// Ist nicht so relevant, können wir vorerst überspringen...
				System.out.println(difference.getComparison().getType() + " is not implemented yet.");
				break;
			case ELEMENT_NUM_ATTRIBUTES:
				// Ist nicht so relevant, können wir vorerst überspringen...
				System.out.println(difference.getComparison().getType() + " is not implemented yet.");
				break;
			case TEXT_VALUE:
				// Erkennt, ob ein Wert in der Spezifikation anders ist, als in der
				// Implementierung.
				for (final Function<DifferenceResult, List<DifferenceError>> comparison : ComparisonCollection
						.getInstance().getEntityChangeComparisons()) {
					diffErrors.addAll(comparison.apply(diffResult));
				}
				break;
			default:
				System.out.println(difference.getComparison().getType() + " is not implemented yet.");
			}
		}
		return diffErrors;
	} // compare

	public static String merge(String oldContent, String newContent) {
		return null;
	} // merge

	private static Node resolveNode(String xPath, Source source) {
		final String localizedXPath = xPath.replaceAll("/([a-zA-Z]+)\\[", "/*[local-name() = '$1'][");
		final Iterable<Node> nodes = pathEngine.selectNodes(localizedXPath, source);
		if (!nodes.iterator().hasNext()) {
			throw new RuntimeException("The XPath " + xPath + " could not be resolved.");
		}
		return nodes.iterator().next();
	} // resolveNode
}