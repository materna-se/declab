package de.materna.dmn.tester.drools;

import de.materna.jdec.DecisionSession;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;

import java.util.LinkedHashMap;
import java.util.Map;

public class DroolsDebugger {
	private static final Logger log = Logger.getLogger(DroolsDebugger.class);
	private DecisionSession decisionSession;
	private Map<String, Map<String, Object>> context;
	private DMNRuntimeEventListener listener;

	public DroolsDebugger(DecisionSession decisionSession) {
		this.decisionSession = decisionSession;
	}

	public void start() {
		context = new LinkedHashMap<>();
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
				if (--level != 0) {
					return;
				}

				String key = event.getVariableName();
				if (key.equals("__RESULT__")) {
					return;
				}

				context.get(decision).put(key, cleanResult(event.getExpressionResult()));
			}
		};
		decisionSession.getRuntime().addListener(listener);
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

	public Map<String, Map<String, Object>> stop() {
		decisionSession.getRuntime().removeListener(listener);

		return context;
	}
}
