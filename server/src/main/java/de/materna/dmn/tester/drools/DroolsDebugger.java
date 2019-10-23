package de.materna.dmn.tester.drools;

import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.servlets.model.beans.ExecutionContext;
import de.materna.dmn.tester.servlets.model.beans.ExecutionKnowledgeModel;
import de.materna.jdec.DecisionSession;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.event.*;

import java.util.*;

public class DroolsDebugger {
	private static final Logger log = Logger.getLogger(DroolsDebugger.class);

	private DecisionSession decisionSession;

	private Map<String, ExecutionContext> context;
	private DMNRuntimeEventListener listener;

	public DroolsDebugger(DecisionSession decisionSession) {
		this.decisionSession = decisionSession;
	}

	public void start() {
		context = new LinkedHashMap<>();
		listener = new DMNRuntimeEventListener() {
			private ExecutionContext decision;
			private int decisionContextLevel;

			private Stack<ExecutionKnowledgeModel> knowledgeModels = new Stack<>();

			/**
			 * We need to create a new execution context for every decision.
			 * This will be later filled with all context entries.
			 */
			@Override
			public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
				decision = new ExecutionContext(new HashMap<>(), new LinkedList<>());
				context.put(event.getDecision().getName(), decision);
			}

			@Override
			public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {
				knowledgeModels.push(new ExecutionKnowledgeModel(event.getBusinessKnowledgeModel().getBusinessKnowledModel().getName(), new ExecutionContext(), 0));
			}

			/**
			 * We only want the root contexts of the decision.
			 * To achieve this, we will store the context level and add only level 0 entries to the execution context.
			 */
			@Override
			public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {
				if (knowledgeModels.empty()) {
					++decisionContextLevel;
					return;
				}

				knowledgeModels.peek().incrementContextLevel();
			}

			@Override
			public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
				// We only want the root context as it includes the child context.
				if (knowledgeModels.empty()) {
					// The context entry is attached to a decision.
					if (--decisionContextLevel != 0) {
						return;
					}

					String key = event.getVariableName();
					if (!key.equals("__RESULT__")) {
						decision.getContext().put(key, DroolsHelper.removeFunctionDefinitions(event.getExpressionResult()));
					}
					return;
				}

				// The context entry is attached to a knowledge model.
				ExecutionKnowledgeModel knowledgeModel = knowledgeModels.peek();
				if (knowledgeModel.decrementContextLevel() != 0) {
					return;
				}
				knowledgeModel.getContext().getContext().put(event.getVariableName(), DroolsHelper.removeFunctionDefinitions(event.getExpressionResult()));
			}

			@Override
			public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
				knowledgeModels.pop();
			}
		};
		decisionSession.getRuntime().addListener(listener);
	}

	public Map<String, ExecutionContext> stop() {
		decisionSession.getRuntime().removeListener(listener);

		return context;
	}
}
