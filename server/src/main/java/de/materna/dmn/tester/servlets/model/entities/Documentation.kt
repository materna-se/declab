package de.materna.dmn.tester.servlets.model.entities

import org.apache.commons.lang3.StringUtils
import org.kie.dmn.api.core.DMNModel
import org.kie.dmn.api.core.DMNType
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode
import org.kie.dmn.api.core.ast.DecisionNode
import org.kie.dmn.model.api.*
import org.kie.dmn.model.v1_3.TLiteralExpression
import java.util.*

open class DMNElementDocumentation(element: DMNElement) {
	var description: String? = null

	init {
		this.description = element.description
	}
}

class ModelDocumentation() {
	var name: String? = null
	var namespace: String? = null
	var description: String? = null
	var itemDefinitions: ArrayList<ItemDefinitionDocumentation>? = null
	var decisions: ArrayList<DecisionDocumentation>? = null
	var knowledgeModels: ArrayList<KnowledgeModelDocumentation>? = null
	var dependencies: ArrayList<String>? = null

	constructor(model: DMNModel) : this() {
		name = model.definitions.name
		namespace = model.definitions.namespace
		description = model.definitions.description

		itemDefinitions = ArrayList()
		for (itemDefinition in model.itemDefinitions) {
			itemDefinitions!!.add(ItemDefinitionDocumentation(itemDefinition.name, itemDefinition.type))
		}

		decisions = ArrayList()
		for (decision in model.decisions) {
			decisions!!.add(DecisionDocumentation(decision))
		}

		knowledgeModels = ArrayList()
		for (knowledgeModel in model.businessKnowledgeModels) {
			knowledgeModels!!.add(KnowledgeModelDocumentation(knowledgeModel))
		}

		dependencies = ArrayList()
		for (import in model.definitions.import) {
			dependencies!!.add(import.namespace)
		}
	}
}

class ItemDefinitionDocumentation(name: String, type: DMNType) {
	var name: String? = null
	var isCollection: Boolean? = null
	var allowedValues: String? = null
	var type: String? = null
	var children: ArrayList<ItemDefinitionDocumentation>? = null

	init {
		this.name = name
		this.isCollection = type.isCollection
		if (type.allowedValues.size > 0) {
			this.allowedValues = StringUtils.join(type.allowedValues, ", ")
		}
		val baseType = getBaseType(type).name
		if (baseType != name) {
			this.type = baseType;
		}

		children = ArrayList()
		for (field in type.fields) {
			children!!.add(ItemDefinitionDocumentation(field.key, field.value))
		}
	}
}

private fun getBaseType(type: DMNType): DMNType {
	if (type.baseType == null) {
		return type
	}

	return getBaseType(type.baseType)
}

class DecisionDocumentation(decisionNode: DecisionNode) {
	var name: String? = null
	var description: String? = null
	var returnType: String? = null
	var question: String? = null
	var answers: List<String>? = null
	var expression: ExpressionDocumentation? = null

	init {
		val decision = decisionNode.decision

		this.name = decision.name
		this.description = decision.description

		this.returnType = decision.variable.typeRef?.localPart
		this.question = decision.question
		this.answers = decision.allowedAnswers?.split("\n")
		this.expression = resolveExpression(decision.expression)
	}
}

class KnowledgeModelDocumentation(knowledgeModel: BusinessKnowledgeModelNode) {
	var name: String? = null
	var description: String? = null
	var returnType: String? = null
	var parameters: ArrayList<FunctionParameterDocumentation>? = null
	var expression: ExpressionDocumentation? = null

	init {
		val knowledgeModel = knowledgeModel.businessKnowledModel

		this.name = knowledgeModel.name
		this.description = knowledgeModel.description
		this.returnType = knowledgeModel.variable.typeRef?.localPart

		this.parameters = ArrayList()
		for (parameter in knowledgeModel.encapsulatedLogic.formalParameter) {
			this.parameters!!.add(FunctionParameterDocumentation(parameter.name, parameter.typeRef?.localPart))
		}

		this.expression = resolveExpression(knowledgeModel.encapsulatedLogic.expression)
	}
}

enum class ExpressionType {
	LITERAL_EXPRESSION,
	CONTEXT,
	CONTEXT_ENTRY,
	DECISION_TABLE,
	FUNCTION_DEFINITION,
	RELATION,
	LIST,
	INVOCATION,
}

open class ExpressionDocumentation(expressionType: ExpressionType, expression: DMNElement) : DMNElementDocumentation(expression) {
	var expressionType: ExpressionType? = null

	init {
		this.expressionType = expressionType
	}
}

class FunctionParameterDocumentation(name: String, type: String?) {
	var name: String? = null
	var type: String? = null

	init {
		this.name = name
		this.type = type
	}
}

class FunctionDefinitionDocumentation(functionDefinition: FunctionDefinition) : ExpressionDocumentation(ExpressionType.FUNCTION_DEFINITION, functionDefinition) {
	var parameters: ArrayList<FunctionParameterDocumentation>? = null
	var expression: ExpressionDocumentation? = null

	init {
		this.parameters = ArrayList()
		for (parameter in functionDefinition.formalParameter) {
			this.parameters!!.add(FunctionParameterDocumentation(parameter.name, parameter.typeRef?.localPart))
		}

		this.expression = resolveExpression(functionDefinition.expression)
	}
}

class RelationDocumentation(relation: Relation) : ExpressionDocumentation(ExpressionType.RELATION, relation) {
	var columns: ArrayList<RelationColumnDocumentation>? = null
	var rows: ArrayList<RelationRowDocumentation>? = null

	init {
		this.columns = ArrayList()
		for (column in relation.column) {
			columns!!.add(RelationColumnDocumentation(column))
		}

		this.rows = ArrayList()
		for (row in relation.row) {
			rows!!.add(RelationRowDocumentation(row))
		}
	}
}

class RelationColumnDocumentation(column: InformationItem) : DMNElementDocumentation(column) {
	var name: String? = null
	var type: String? = null

	init {
		this.name = column.name
		this.type = column.typeRef?.localPart
	}
}

class RelationRowDocumentation(row: org.kie.dmn.model.api.List) {
	var expressions: ArrayList<ExpressionDocumentation>? = null

	init {
		this.expressions = ArrayList()
		for (expression in row.expression) {
			expressions!!.add(resolveExpression(expression))
		}
	}
}

class ListDocumentation(list: org.kie.dmn.model.api.List) : ExpressionDocumentation(ExpressionType.LIST, list) {
	var expressions: ArrayList<ExpressionDocumentation>? = null

	init {
		this.expressions = ArrayList()
		for (expression in list.expression) {
			expressions!!.add(resolveExpression(expression))
		}
	}
}

class InvocationDocumentation(invocation: Invocation) : ExpressionDocumentation(ExpressionType.INVOCATION, invocation) {
	var name: String? = null
	var bindings: ArrayList<InvocationBindingDocumentation>? = null

	init {
		this.name = (invocation.expression as LiteralExpression).text

		this.bindings = ArrayList()
		for (binding in invocation.binding) {
			bindings!!.add(InvocationBindingDocumentation(binding))
		}
	}
}

class InvocationBindingDocumentation(binding: Binding) {
	var name: String? = null
	var type: String? = null
	var description: String? = null
	var expression: ExpressionDocumentation? = null

	init {
		this.name = binding.parameter.name
		this.type = binding.parameter.typeRef?.localPart
		this.description = binding.parameter.description
		this.expression = resolveExpression(binding.expression)
	}
}

class RelationCellDocumentation(column: InformationItem) : DMNElementDocumentation(column) {
	var name: String? = null
	var type: String? = null

	init {
		this.name = column.name
		this.type = column.typeRef?.localPart
	}
}

class LiteralExpressionDocumentation(literalExpression: LiteralExpression) : ExpressionDocumentation(ExpressionType.LITERAL_EXPRESSION, literalExpression) {
	var type: String? = null
	var text: String? = null

	init {
		this.type = literalExpression.typeRef?.localPart
		this.text = literalExpression.text
	}
}

class ContextDocumentation(context: Context) : ExpressionDocumentation(ExpressionType.CONTEXT, context) {
	var entries: ArrayList<ContextEntryDocumentation>? = null

	init {
		entries = ArrayList()
		for (entry in context.contextEntry) {
			entries!!.add(ContextEntryDocumentation(entry))
		}
	}
}

class ContextEntryDocumentation(contextEntry: ContextEntry) : ExpressionDocumentation(ExpressionType.CONTEXT_ENTRY, contextEntry.variable ?: contextEntry) {
	var name: String? = null
	var type: String? = null
	var expression: ExpressionDocumentation? = null

	init {
		this.name = contextEntry.variable?.name
		this.type = contextEntry.variable?.typeRef?.localPart
		this.expression = resolveExpression(contextEntry.expression)
	}
}

class DecisionTableDocumentation(decisionTable: DecisionTable) : ExpressionDocumentation(ExpressionType.DECISION_TABLE, decisionTable) {
	var hitPolicy: String? = null
	var inputs: ArrayList<DecisionTableInputDocumentation>? = null
	var outputs: ArrayList<DecisionTableOutputDocumentation>? = null
	var annotations: ArrayList<DecisionTableAnnotationDocumentation>? = null
	var rules: ArrayList<DecisionTableRuleDocumentation>? = null

	init {
		this.hitPolicy = decisionTable.hitPolicy.name

		inputs = ArrayList()
		for (input in decisionTable.input) {
			inputs!!.add(DecisionTableInputDocumentation(input))
		}

		outputs = ArrayList()
		for (output in decisionTable.output) {
			outputs!!.add(DecisionTableOutputDocumentation(output))
		}

		annotations = ArrayList()
		for (annotation in decisionTable.annotation) {
			annotations!!.add(DecisionTableAnnotationDocumentation(annotation))
		}

		rules = ArrayList()
		for (rule in decisionTable.rule) {
			rules!!.add(DecisionTableRuleDocumentation(rule))
		}
	}
}

class DecisionTableInputDocumentation(input: InputClause) : DMNElementDocumentation(input) {
	var name: String? = null
	var type: String? = null

	init {
		this.name = input.inputExpression.text
		this.type = input.inputExpression.typeRef?.localPart
	}
}

class DecisionTableOutputDocumentation(output: OutputClause) : DMNElementDocumentation(output) {
	var name: String? = null
	var type: String? = null

	init {
		this.name = output.name
		this.type = output.typeRef?.localPart
	}
}

class DecisionTableAnnotationDocumentation(annotation: RuleAnnotationClause) {
	var name: String? = null

	init {
		this.name = annotation.name
	}
}

class DecisionTableRuleDocumentation(rule: DecisionRule) {
	var entries: ArrayList<DecisionTableRuleEntryDocumentation>? = null

	init {
		entries = ArrayList()
		for (entry in rule.inputEntry) {
			entries!!.add(DecisionTableRuleEntryDocumentation(entry))
		}
		for (entry in rule.outputEntry) {
			entries!!.add(DecisionTableRuleEntryDocumentation(entry))
		}
		for (entry in rule.annotationEntry) {
			entries!!.add(DecisionTableRuleEntryDocumentation(entry))
		}
	}
}

class DecisionTableRuleEntryDocumentation {
	var description: String? = null
	var text: String? = null

	constructor(expression: UnaryTests) {
		this.description = expression.description
		this.text = expression.text
	}

	constructor(expression: LiteralExpression) {
		this.description = expression.description
		this.text = expression.text
	}

	constructor(expression: RuleAnnotation) {
		this.text = expression.text
	}
}

fun resolveExpression(expression: Expression): ExpressionDocumentation {
	if (expression is LiteralExpression) {
		return LiteralExpressionDocumentation(expression)
	}

	if (expression is Context) {
		return ContextDocumentation(expression)
	}

	if (expression is DecisionTable) {
		return DecisionTableDocumentation(expression)
	}

	if (expression is FunctionDefinition) {
		return FunctionDefinitionDocumentation(expression)
	}

	if (expression is Relation) {
		return RelationDocumentation(expression)
	}

	if (expression is org.kie.dmn.model.api.List) {
		return ListDocumentation(expression)
	}

	if (expression is Invocation) {
		return InvocationDocumentation(expression)
	}

	val errorExpression = TLiteralExpression()
	errorExpression.text = expression.javaClass.name + " is not implemented yet!"
	return LiteralExpressionDocumentation(errorExpression)
}