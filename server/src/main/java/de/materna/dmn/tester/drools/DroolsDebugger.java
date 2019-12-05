package de.materna.dmn.tester.drools;

import de.materna.jdec.DecisionSession;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.event.*;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DroolsDebugger {
	private DecisionSession decisionSession;
	private Map<String, Map<String, Object>> context;
	private List<String> messages;
	private DMNRuntimeEventListener listener;

	public DroolsDebugger(DecisionSession decisionSession) {
		this.decisionSession = decisionSession;
	}

	public void start() {
		context = new LinkedHashMap<>();
		messages = new LinkedList<>();
		listener = new DMNRuntimeEventListener() {
			private String decision;
			private int level;

			@Override
			public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
				decision = event.getDecision().getName();
				context.put(decision, new LinkedHashMap<>());
			}

			@Override
			public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {
				++level;
			}

			@Override
			public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
				// We only want the root context as it includes the child context.
				if (decision == null || --level != 0) {
					return;
				}

				String key = event.getVariableName();
				if (key.equals("__RESULT__")) {
					return;
				}

				context.get(decision).put(key, cleanResult(event.getExpressionResult()));
			}

			@Override
			public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
				decision = null;

				for (DMNMessage message : event.getResult().getMessages()) {
					messages.add(message.getFeelEvent().getMessage());
				}
			}
		};
		decisionSession.getRuntime().addListener(listener);
	}

	public void stop() {
		decisionSession.getRuntime().removeListener(listener);
	}

	public Map<String, Map<String, Object>> getContext() {
		return context;
	}

	public List<String> getMessages() {
		return messages;
	}

	private Object cleanResult(Object result) {
		if (result instanceof Map) {
			Map<String, Object> results = (Map<String, Object>) result;

			Map<String, Object> cleanedResults = new LinkedHashMap<>();
			for (Map.Entry<String, Object> entry : results.entrySet()) {
				cleanedResults.put(entry.getKey(), cleanResult(entry.getValue()));
			}
			return cleanedResults;
		}

		if (result instanceof DMNFunctionDefinitionEvaluator.DMNFunction) {
			return null;
		}

		return result;
	}
}
