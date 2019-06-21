package de.materna.dmn.tester.drools;

import de.materna.jdec.DecisionSession;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;

import java.util.LinkedHashMap;
import java.util.Map;

public class DroolsDebugger {
	private DecisionSession decisionSession;
	private Map<String, Map<String, Object>> context;
	private String currentDecision;
	private DMNRuntimeEventListener listener;

	public DroolsDebugger(DecisionSession decisionSession) {
		this.decisionSession = decisionSession;
	}

	public void start() {
		context = new LinkedHashMap<>();
		listener = new DMNRuntimeEventListener() {
			@Override
			public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
				currentDecision = event.getDecision().getName();
				context.put(currentDecision, new LinkedHashMap<>());
			}

			@Override
			public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
				String key = event.getVariableName();
				if (key.equals("__RESULT__")) { // We need to merge the output beans with this entry.
					return;
				}
				Object value = event.getExpressionResult();
				if (value instanceof DMNFunctionDefinitionEvaluator.DMNFunction) { // We need to handle this later.
					return;
				}

				context.get(currentDecision).put(key, value);
			}
		};
		decisionSession.getRuntime().addListener(listener);
	}

	public Map<String, Map<String, Object>> stop() {
		decisionSession.getRuntime().removeListener(listener);

		return context;
	}
}
