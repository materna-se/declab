package de.materna.dmn.tester.servlets.model.entities

import org.apache.commons.lang3.StringUtils
import org.kie.dmn.api.core.DMNModel
import org.kie.dmn.api.core.DMNType
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode
import org.kie.dmn.api.core.ast.DecisionNode
import org.kie.dmn.api.core.ast.DecisionServiceNode
import org.kie.dmn.api.core.ast.InputDataNode
import org.kie.dmn.model.api.*
import org.kie.dmn.model.v1_3.TInformationItem
import org.kie.dmn.model.v1_3.TLiteralExpression
import org.kie.dmn.model.v1_3.TOutputClause
import org.slf4j.LoggerFactory
import javax.xml.namespace.QName

private val log = LoggerFactory.getLogger(ModelDocumentation::class.java)

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
	var inputs: ArrayList<InputDocumentation>? = null
	var decisions: ArrayList<DecisionDocumentation>? = null
	var knowledgeModels: ArrayList<KnowledgeModelDocumentation>? = null
	var decisionServices: ArrayList<DecisionServiceDocumentation>? = null
	var dependencies: ArrayList<ModelDependency>? = null

	constructor(model: DMNModel) : this() {
		name = model.definitions.name
		namespace = model.definitions.namespace
		description = model.definitions.description

		itemDefinitions = ArrayList()
		for (itemDefinition in model.itemDefinitions) {
			if (itemDefinition.modelNamespace == model.definitions.namespace) {
				itemDefinitions!!.add(ItemDefinitionDocumentation(itemDefinition.name, itemDefinition.type))
			}
		}

		inputs = ArrayList()
		for (input in model.inputs) {
			if (input.modelNamespace == model.definitions.namespace) {
				inputs!!.add(InputDocumentation(input))
			}
		}

		decisions = ArrayList()
		for (decision in model.decisions) {
			if (decision.modelNamespace == model.definitions.namespace) {
				decisions!!.add(DecisionDocumentation(decision))
			}
		}

		knowledgeModels = ArrayList()
		for (knowledgeModel in model.businessKnowledgeModels) {
			if (knowledgeModel.modelNamespace == model.definitions.namespace) {
				knowledgeModels!!.add(KnowledgeModelDocumentation(knowledgeModel))
			}
		}

		decisionServices = ArrayList()
		for (decisionService in model.decisionServices) {
			if (decisionService.modelNamespace == model.definitions.namespace) {
				decisionServices!!.add(DecisionServiceDocumentation(decisionService))
			}
		}

		dependencies = ArrayList()
		for (import in model.definitions.import) {
			dependencies!!.add(ModelDependency(import.namespace, import.name))
		}
	}
}

class ModelDependency(namespace: String, alias: String) {
	var namespace: String
	var alias: String

	init {
		this.namespace = namespace
		this.alias = alias
	}
}

class ItemDefinitionDocumentation(name: String, type: DMNType) {
	var name: String? = null
	var isCollection: Boolean? = null
	var allowedValues: String? = null
	var type: String? = null
	var children: ArrayList<ItemDefinitionDocumentation>? = null

	init {
		// It seems like the id is not always transformed correctly, it is sometimes null.
		// We are using the name as a workaround.
		this.name = name
		this.isCollection = type.isCollection

		if (type.allowedValues.size > 0) {
			this.allowedValues = StringUtils.join(type.allowedValues, ", ")
		}

		val baseType = getBaseType(type).name
		// If the name of the baseType equals the name of the item definition, it is a structure.
		// In these cases, we want to resolve the children as well.
		if (baseType == name) {
			children = ArrayList()
			for (field in type.fields) {
				children!!.add(ItemDefinitionDocumentation(field.key, field.value))
			}
		}
		else {
			this.type = baseType
		}
	}
}

class InputDocumentation(inputNode: InputDataNode) {
	var id: String
	var name: String
	var returnType: String? = null

	init {
		this.id = inputNode.id
		this.name = inputNode.name
		this.returnType = inputNode.type.name
	}
}

class DecisionDocumentation(decisionNode: DecisionNode) {
	var id: String
	var name: String
	var description: String? = null
	var returnType: String? = null
	var question: String? = null
	var answers: List<String>? = null
	var expression: ExpressionDocumentation? = null

	init {
		val decision = decisionNode.decision

		this.id = decision.id
		this.name = decision.name
		this.description = decision.description

		this.returnType = decision.variable.typeRef?.localPart
		this.question = decision.question
		this.answers = decision.allowedAnswers?.split("\n")
		this.expression = resolveExpression(decision.variable, decision.expression)
	}
}

class KnowledgeModelDocumentation(knowledgeModelNode: BusinessKnowledgeModelNode) {
	var id: String
	var name: String
	var description: String? = null
	var returnType: String? = null
	var parameters: ArrayList<FunctionParameterDocumentation>? = null
	var expression: ExpressionDocumentation? = null

	init {
		val knowledgeModel = knowledgeModelNode.businessKnowledModel

		this.id = knowledgeModel.id
		this.name = knowledgeModel.name
		this.description = knowledgeModel.description
		this.returnType = knowledgeModel.variable.typeRef?.localPart

		this.parameters = ArrayList()
		for (parameter in knowledgeModel.encapsulatedLogic.formalParameter) {
			this.parameters!!.add(FunctionParameterDocumentation(parameter.name, parameter.typeRef?.localPart))
		}

		this.expression = resolveExpression(knowledgeModel.variable, knowledgeModel.encapsulatedLogic.expression)
	}
}

class DecisionServiceDocumentation(decisionServiceNode: DecisionServiceNode) {
	var id: String
	var name: String
	var description: String? = null
	var inputData: ArrayList<DecisionServiceReference>? = null
	var inputDecisions: ArrayList<DecisionServiceReference>? = null
	var encapsulatedDecisions: ArrayList<DecisionServiceReference>? = null
	var outputDecisions: ArrayList<DecisionServiceReference>? = null

	init {
		val decisionService = decisionServiceNode.decisionService

		this.id = decisionService.id
		this.name = decisionService.name
		this.description = decisionService.description

		this.inputData = ArrayList()
		for (inputData in decisionService.inputData) {
			this.inputData!!.add(DecisionServiceReference(inputData))
		}

		this.inputDecisions = ArrayList()
		for (inputDecision in decisionService.inputDecision) {
			this.inputDecisions!!.add(DecisionServiceReference(inputDecision))
		}

		this.encapsulatedDecisions = ArrayList()
		for (encapsulatedDecision in decisionService.encapsulatedDecision) {
			this.encapsulatedDecisions!!.add(DecisionServiceReference(encapsulatedDecision))
		}

		this.outputDecisions = ArrayList()
		for (outputDecision in decisionService.outputDecision) {
			this.outputDecisions!!.add(DecisionServiceReference(outputDecision))
		}
	}
}

class DecisionServiceReference(reference: DMNElementReference) {
	var namespace: String? = null
	var id: String? = null

	init {
		val split = reference.href.split("#", limit = 2)
		this.namespace = if (split[0] == "") null else split[0]
		this.id = split[1]
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

class FunctionDefinitionDocumentation(variable: InformationItem, functionDefinition: FunctionDefinition) : ExpressionDocumentation(ExpressionType.FUNCTION_DEFINITION, functionDefinition) {
	var parameters: ArrayList<FunctionParameterDocumentation>? = null
	var expression: ExpressionDocumentation? = null

	init {
		this.parameters = ArrayList()
		for (parameter in functionDefinition.formalParameter) {
			this.parameters!!.add(FunctionParameterDocumentation(parameter.name, parameter.typeRef?.localPart))
		}

		this.expression = resolveExpression(variable, functionDefinition.expression)
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
			expressions!!.add(resolveExpression(null, expression))
		}
	}
}

class ListDocumentation(list: org.kie.dmn.model.api.List) : ExpressionDocumentation(ExpressionType.LIST, list) {
	var expressions: ArrayList<ExpressionDocumentation>? = null

	init {
		this.expressions = ArrayList()
		for (expression in list.expression) {
			expressions!!.add(resolveExpression(null, expression))
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
		this.expression = resolveExpression(binding.parameter, binding.expression)
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
		this.expression = resolveExpression(contextEntry.variable, contextEntry.expression)
	}
}

class DecisionTableDocumentation(variable: InformationItem, decisionTable: DecisionTable) : ExpressionDocumentation(ExpressionType.DECISION_TABLE, decisionTable) {
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
		// TODO: Do we always have at least one output?
		if (decisionTable.output.size > 1) {
			for (output in decisionTable.output) {
				println(output.javaClass.name)
				outputs!!.add(DecisionTableOutputDocumentation(output))
			}
		}
		else {
			// If the output is simple, we inherit the name and type of the parent.
			val clause = TOutputClause()
			clause.name = variable.name
			clause.description = decisionTable.output[0].description
			clause.typeRef = variable.typeRef
			outputs!!.add(DecisionTableOutputDocumentation(clause))
		}

		annotations = ArrayList()
		// Rule annotations were not supported in version 1.1 of the specification.
		// We simply ignore them in this case.
		try {
			for (annotation in decisionTable.annotation) {
				annotations!!.add(DecisionTableAnnotationDocumentation(annotation))
			}
		}
		catch (e: Exception) {
			log.warn("Ignoring rule annotations as they are not supported in version 1.1 of the specification...")
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
		// Rule annotations were not supported in version 1.1 of the specification.
		// We simply ignore them in this case.
		try {
			for (entry in rule.annotationEntry) {
				entries!!.add(DecisionTableRuleEntryDocumentation(entry))
			}
		}
		catch (e: Exception) {
			log.warn("Ignoring rule annotations as they are not supported in version 1.1 of the specification...")
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

private fun getBaseType(type: DMNType): DMNType {
	if (type.baseType == null) {
		return type
	}

	return getBaseType(type.baseType)
}

/**
 * TODO: We can't always determine the variable name (for example inside of a List)?
 *       In such cases, we would not be able to resolve the expression.
 */
fun resolveExpression(_variable: InformationItem?, expression: Expression): ExpressionDocumentation {
	var variable = _variable
	if (variable == null) {
		variable = TInformationItem()
		variable.name = ""
		variable.typeRef = null
	}

	if (expression is LiteralExpression) {
		return LiteralExpressionDocumentation(expression)
	}

	if (expression is Context) {
		return ContextDocumentation(expression)
	}

	if (expression is DecisionTable) {
		return DecisionTableDocumentation(variable, expression)
	}

	if (expression is FunctionDefinition) {
		return FunctionDefinitionDocumentation(variable, expression)
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