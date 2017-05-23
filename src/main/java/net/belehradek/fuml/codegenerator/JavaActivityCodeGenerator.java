package net.belehradek.fuml.codegenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.ActivityEdge;
import org.modeldriven.alf.uml.ActivityNode;
import org.modeldriven.alf.uml.ActivityParameterNode;
import org.modeldriven.alf.uml.AddStructuralFeatureValueAction;
import org.modeldriven.alf.uml.CallAction;
import org.modeldriven.alf.uml.CallOperationAction;
import org.modeldriven.alf.uml.Clause;
import org.modeldriven.alf.uml.ClearStructuralFeatureAction;
import org.modeldriven.alf.uml.ConditionalNode;
import org.modeldriven.alf.uml.CreateObjectAction;
import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.ExecutableNode;
import org.modeldriven.alf.uml.ForkNode;
import org.modeldriven.alf.uml.InputPin;
import org.modeldriven.alf.uml.LoopNode;
import org.modeldriven.alf.uml.MultiplicityElement;
import org.modeldriven.alf.uml.ObjectFlow;
import org.modeldriven.alf.uml.OutputPin;
import org.modeldriven.alf.uml.Pin;
import org.modeldriven.alf.uml.ReadSelfAction;
import org.modeldriven.alf.uml.ReadStructuralFeatureAction;
import org.modeldriven.alf.uml.StructuredActivityNode;
import org.modeldriven.alf.uml.TestIdentityAction;
import org.modeldriven.alf.uml.Type;
import org.modeldriven.alf.uml.ValueSpecificationAction;

public class JavaActivityCodeGenerator {

	protected String namespacePrefix;

	protected Set<ActivityNode> generatedNode = new HashSet<>();

	public JavaActivityCodeGenerator(String namespacePrefix) {
		this.namespacePrefix = namespacePrefix;
	}

	public String getActivityBodyCode(Activity activity) {
		StringBuilder s = new StringBuilder();

		// getActivityBodyCodeRecurse(s, 0, activity.getNode());

		generatedNode.clear();
		getActivityBodyCodeInputFirst(s, activity.getNode());

		return s.toString();
	}

	public void getActivityBodyCodeRecurse(StringBuilder out, int level, List<ActivityNode> nodes) {
		for (ActivityNode n : nodes) {
			// getActivityBodyCodeRecurse(out, level + 1, n);
			if (n instanceof StructuredActivityNode) {
				StructuredActivityNode sn = (StructuredActivityNode) n;
				getActivityBodyCodeRecurse(out, level + 1, sn.getNode());
			}
			nodeToCode(out, n);
		}
	}

	public void getActivityBodyCodeInputFirst(StringBuilder out, List<ActivityNode> nodes) {
		getActivityBodyCodeInputFirst(out, nodes, null);
	}

	public void getActivityBodyCodeInputFirst(StringBuilder out, List<ActivityNode> nodes, Clause forCond) {
		for (ActivityNode node : nodes) {
			getActivityBodyCodeInputFirst(out, node, forCond);
		}
	}

	public void getActivityBodyCodeInputFirst(StringBuilder out, ActivityNode node) {
		getActivityBodyCodeInputFirst(out, node, null);
	}

	public void getActivityBodyCodeInputFirst(StringBuilder out, ActivityNode node, Clause forCond) {
		if (generatedNode.contains(node)) {
			// out.append("Skip1: " + node + "\n");
			return;
		}
		// if (forCond == null && isInCond(node.getOwner())) {
		// out.append("Skip2: " + node + "\n");
		// return;
		// }
		if (forCond != null && !isParent(node.getOwner(), forCond)) {
			// out.append("Skip3: " + node + "\n");
			return;
		}
		generatedNode.add(node);

		// out.append("Start: " + node + "\n");

		// test
		// if (node.getOwner() != null && node.getOwner() instanceof
		// ActivityNode)
		// getActivityBodyCodeInputFirst(out, (ActivityNode)node.getOwner(),
		// forCond);

		// vstupy
		for (ActivityEdge ie : node.getIncoming()) {
			getActivityBodyCodeInputFirst(out, ie.getSource(), forCond);
		}
		if (node instanceof StructuredActivityNode) {
			StructuredActivityNode san = (StructuredActivityNode) node;
			for (InputPin ip : san.getInput()) {
				for (ActivityEdge ie : ip.getIncoming()) {
					getActivityBodyCodeInputFirst(out, ie.getSource(), forCond);
				}
			}
		}
		if (node instanceof ConditionalNode) {
			ConditionalNode cn = (ConditionalNode) node;
			for (Clause clause : cn.getClause()) {
				getActivityBodyCodeInputFirst(out, clause.getDecider(), forCond);
			}
		}

		// jsem vystupni pin - prvne generovat vlastnika
		if (node instanceof OutputPin) {
			Element e = node.getOwner();
			if (e instanceof ActivityNode)
				getActivityBodyCodeInputFirst(out, (ActivityNode) e);
		}

		// jsem vstupni pin - generovat vstupy
		if (node instanceof InputPin) {
			InputPin ip = (InputPin) node;
			for (ActivityEdge ed : ip.getIncoming()) {
				getActivityBodyCodeInputFirst(out, ed.getSource());
			}
		}

		if (node instanceof ReadStructuralFeatureAction) {
			ReadStructuralFeatureAction t = (ReadStructuralFeatureAction) node;
			getActivityBodyCodeInputFirst(out, t.getObject());
		}

		if (node instanceof AddStructuralFeatureValueAction) {
			AddStructuralFeatureValueAction t = (AddStructuralFeatureValueAction) node;
			getActivityBodyCodeInputFirst(out, t.getObject());
		}

		// parametry
		if (node instanceof CallAction) {
			CallAction t = (CallAction) node;
			getActivityBodyCodeInputFirst(out, listInputPinToActivityNodeList(t.getArgument()));
		}

		//TODO
		//out.append("Code: " + node + "\n");
		nodeToCode(out, node);

		// vnitrni nody - ne u podminky (generovano jinde)
		if (node instanceof StructuredActivityNode && !(node instanceof ConditionalNode)) {
			StructuredActivityNode san = (StructuredActivityNode) node;
			getActivityBodyCodeInputFirst(out, san.getNode(), forCond);
		}

		// vystupy
		for (ActivityEdge oe : node.getOutgoing()) {
			getActivityBodyCodeInputFirst(out, oe.getTarget(), forCond);
		}
		if (node instanceof StructuredActivityNode) {
			StructuredActivityNode san = (StructuredActivityNode) node;
			for (OutputPin op : san.getOutput()) {
				for (ActivityEdge oe : op.getOutgoing()) {
					getActivityBodyCodeInputFirst(out, oe.getTarget(), forCond);
				}
			}
		}
		// if (node instanceof ConditionalNode) {
		// ConditionalNode cn = (ConditionalNode) node;
		// for (Clause clause : cn.getClause()) {
		// getActivityBodyCodeInputFirst(out,
		// listToActivityNodeList(clause.getBody()));
		// }
		// }
	}

	public boolean isParent(Element node, Element parent) {
		if (node == parent)
			return true;
		if (node.getOwner() != null)
			return isParent(node.getOwner(), parent);
		return false;
	}

	public boolean isInCond(Element node) {
		if (node instanceof ConditionalNode)
			return true;
		if (node.getOwner() != null)
			return isInCond(node.getOwner());
		return false;
	}

	public List<ActivityNode> listToActivityNodeList(List<ExecutableNode> list) {
		List<ActivityNode> lan = new ArrayList<>();
		for (ExecutableNode en : list) {
			lan.add(en);
		}
		return lan;
	}

	public List<ActivityNode> listInputPinToActivityNodeList(List<InputPin> list) {
		List<ActivityNode> lan = new ArrayList<>();
		for (InputPin en : list) {
			for (ActivityEdge edge : en.getIncoming())
				lan.add(edge.getSource());
		}
		return lan;
	}

	public String getInBracket(String in) {
		Pattern pattern = Pattern.compile("[^(]\\(([^)]*)\\)");
		Matcher matcher = pattern.matcher(in);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public String getInBracketIf(String in, String start) {
		if (in.startsWith(start))
			return getInBracket(in);
		return null;
	}

	public String getCallName(CallAction ca) {
		String name = getInBracketIf(ca.getName(), "Call(");
		if (ca instanceof CallOperationAction) {
			CallOperationAction coa = (CallOperationAction) ca;
			if (name.contains("$initialization$")) {
				name = "new " + name;
			} else if (coa.getResult().size() > 0) {
				String type = coa.getResult().get(0).getType().getName();
				if (name.equals(type)) {
					name = "new " + name;
				}
			}
		} else {
			if (name.equals("Div")) {
				return "/";
			} else if (name.equals("Concat")) {
				return "+";
			} else if (name.equals("WriteLine")) {
				return "Log.d";
			} else if (name.equals("ToString")) {
				return "String.valueOf";
			}
		}
		return name;
	}

	public String getCallParams(CallAction ca) {
		String out = "";
		String name = getCallName(ca);
		boolean first = true;

		if (name.equals("Log.d")) {
			out += "\"fUml\", ";
		}

		for (InputPin ip : ca.getArgument()) {
			if (!first) {
				if (isInfix(name))
					out += " " + name + " ";
				else
					out += ", ";
			}
			first = false;
			out += getEdgeSource(ip.getIncoming().get(0));
		}
		return out;
	}

	public String getTypeName(Type t) {
		// null literal
		if (t == null)
			return "Object";

		String out = UmlFrameworkWrapper.getQualifiedTypeName(t, namespacePrefix);

		if (out.equals("Integer") || out.equals("Natural"))
			out = "Integer";
		else if (out.equals("Real"))
			out = "Double";
		else if (out.equals("Boolean"))
			out = "Boolean";

		return out;
	}

	public boolean isInfix(String name) {
		String tmp = "+-*/<<=>>==!=";
		return tmp.contains(name);
	}

	public String getNodeSource(ActivityNode node) {
		// literal
		if (node instanceof ValueSpecificationAction) {
			return getInBracketIf(node.getName(), "Value(");
		}
		// vstupni parametr
		else if (node instanceof InputPin) {
			String o = getInBracketIf(node.getName(), "Input(");
			if (o == null && node.getName().startsWith("Passthru(")) {
				StructuredActivityNode san = (StructuredActivityNode) node.getOwner();
				ActivityNode an = san.getStructuredNodeInput().get(0).getIncoming().get(0).getSource();
				return getNodeSource(an);
			}
			return o;
		} else if (node instanceof OutputPin) {
			OutputPin op = (OutputPin) node;
			// literal
			if (node.getOwner() instanceof ValueSpecificationAction) {
				return getInBracketIf(node.getName(), "Value(");
			} else if (node.getOwner() instanceof CallAction) {
				return TmpVar.getOrSetVar(node).name;
			} else if (node.getOwner() instanceof TestIdentityAction) {
				return TmpVar.getOrSetVar(node).name;
			} else if (node.getName().startsWith("Output(")) {
				return TmpVar.getOrSetVar(node.getIncoming().get(0).getSource()).name;
			} else if (node.getName().startsWith("ReadSelf.")) {
				return "this";
			} else if (node.getName().startsWith("Read(")) {
				return "this." + getInBracketIf(node.getName(), "Read(");
			} else if (node.getName().startsWith("Passthru(")) {
				StructuredActivityNode san = (StructuredActivityNode) node.getOwner();
				ActivityNode an = san.getStructuredNodeInput().get(0).getIncoming().get(0).getSource();
				return getNodeSource(an);
			} else if (node.getOwner() instanceof ConditionalNode) {
				return "0";
			} else if (node.getOwner() instanceof LoopNode) {
				//return "0";
				TmpVar var = TmpVar.getVar(node);
				if (var == null)
					return "0";
				return var.name;

			}
		}
		// promena
		else if (node instanceof ForkNode) {
			return TmpVar.getOrSetVar(node).name;
		}
		// atribut
		else if (node instanceof ReadStructuralFeatureAction) {
			return TmpVar.getOrSetVar(node).name;
		}
		return null;
	}

	public String getEdgeSource(ActivityEdge edge) {
		ActivityNode source = edge.getSource();

		String s = getNodeSource(source);
		if (s != null) {
			return s;
		} else {
			// Global.log("Cant get source!!! " + source);
			// return "/* " + source + " --- " + source.getOwner() + " */";
		}
		return null;
	}

	public boolean isArray(MultiplicityElement elm) {
		return elm.getUpper() > 1 || elm.getUpper() < 0;
	}

	public String getTmpLeftSide(ActivityNode fn) {
		String type = "";
		Type t = null;

		ActivityNode source = fn;
		if (fn.getIncoming().size() > 0) {
			ActivityEdge inEdge = fn.getIncoming().get(0);
			source = inEdge.getSource();
		}

		if (source instanceof Pin) {
			Pin p = (Pin) source;
			t = p.getType();
			type = getTypeName(t);
			if (isArray(p))
				type += "[]";
			type += " ";
		} else if (source instanceof ForkNode) {
			if (TmpVar.getOrSetVar(source).type != null) {
				type = TmpVar.getOrSetVar(source).type.getName();
			} else
				type = "<NOTYPE>";
			type += " ";
		}
		TmpVar tmpVar = TmpVar.getOrSetVar(fn, t);
		String name = tmpVar.name;
		String typeName = type + name;
		return typeName;
	}

	public String callToCode(CallAction ca) {
		String out = "";
		String type = "";
		String var = "";
		if (ca.getResult().size() > 0) {
			OutputPin op = ca.getResult().get(0);
			Type t = op.getType();
			type = getTypeName(t);
			if (type.equals("Status")) {
				type = "";
			} else {
				if (isArray(op))
					type += "[]";
				type += " ";
				var = TmpVar.getOrSetVar(op, t).name;
				var += " = ";
			}
		}
		String name = getCallName(ca);
		out += type + var;

		if (!isInfix(name)) {
			if (ca instanceof CallOperationAction) {
				CallOperationAction coa = (CallOperationAction) ca;
				ActivityNode target = coa.getTarget().getIncoming().get(0).getSource();
				if (!name.startsWith("new "))
					name = getNodeSource(target) + "." + name;
			}

			out += name + "(";
		}

		out += getCallParams(ca);

		if (!isInfix(name))
			out += ")";
		out += ";\n";
		return out;
	}

	public void nodeToCode(StringBuilder out, ActivityNode node) {
		// parametry aktivity i s return parametrem
		if (node instanceof ActivityParameterNode) {
			ActivityParameterNode apn = (ActivityParameterNode) node;
			if (apn.getName().equals("Return")) {
				ActivityEdge e = apn.getIncoming().get(0).getSource().getIncoming().get(0);
				String source = getEdgeSource(e);
				out.append("return " + source + ";\n");
			}
		}

		if (node instanceof TestIdentityAction) {
			TestIdentityAction tia = (TestIdentityAction) node;
			String first = getEdgeSource(tia.getFirst().getIncoming().get(0));
			String second = getEdgeSource(tia.getFirst().getIncoming().get(0));
			String result = getTmpLeftSide(tia.getResult());
			out.append(result + " = " + first + " == " + second + ";\n");
		}

		if (node instanceof StructuredActivityNode) {
			if (node.getName().startsWith("ReturnStatement@")) {
				String tmp = null;
				StructuredActivityNode san = (StructuredActivityNode) node;
				List<ActivityNode> san2 = san.getNode();
				if (san2.size() != 0) {
					tmp = getNodeSource(san2.get(0));
					if (tmp == null) {
						StructuredActivityNode san3 = (StructuredActivityNode) san2.get(0);
						tmp = getNodeSource(san3.getStructuredNodeOutput().get(0));
					}
				}
				out.append("return " + tmp + ";\n");
			}
		}

		// fork node s object flow - kopirovani promennych
		else if (node instanceof ForkNode) {
			ForkNode fn = (ForkNode) node;
			ActivityEdge inEdge = fn.getIncoming().get(0);
			if (inEdge instanceof ObjectFlow) {
				String typeName = getTmpLeftSide(fn);
				String sourceVar = getEdgeSource(inEdge);
				if (sourceVar != null) {
					out.append(typeName + " = " + sourceVar + ";\n");
				}
				// TODO
				else {
					//out.append(typeName + " = " + sourceVar + ";\n");
				}
			}
		}

		if (node instanceof ReadSelfAction) {

		}

		if (node instanceof AddStructuralFeatureValueAction) {
			AddStructuralFeatureValueAction fva = (AddStructuralFeatureValueAction) node;
			StructuredActivityNode san = (StructuredActivityNode) fva.getOwner();
			String sourceObj = getEdgeSource(san.getStructuredNodeInput().get(0).getIncoming().get(0));

			ClearStructuralFeatureAction ca = (ClearStructuralFeatureAction) fva.getObject().getIncoming().get(0)
					.getSource().getOwner();
			ActivityNode targetNode = (ActivityNode) ca.getObject().getIncoming().get(0).getSource().getOwner();
			String targetObj = "";

			if (targetNode instanceof ReadSelfAction) {
				targetObj = "this";
			} else if (targetNode instanceof StructuredActivityNode
					&& targetNode.getName().startsWith("LocalNameDeclarationStatement@")) {
				StructuredActivityNode sn = (StructuredActivityNode) targetNode;

				getActivityBodyCodeInputFirst(out, sn.getNode().get(0));

				targetObj = getNodeSource(sn.getNode().get(0));
			} else if (targetNode instanceof ReadStructuralFeatureAction) {
				targetObj = getNodeSource(targetNode);
			} else {
				targetObj = "<NO>";
			}

			String sourceAtr = fva.getStructuralFeature().getName();

			out.append(targetObj + "." + sourceAtr + " = " + sourceObj + ";\n");
		}

		if (node instanceof ReadStructuralFeatureAction) {
			ReadStructuralFeatureAction fva = (ReadStructuralFeatureAction) node;

			// if (fva.getName().equals("Read(abc)")) {
			// Global.log("break");
			// }

			TmpVar tmpVar = TmpVar.getOrSetVar(node, fva.getResult().getType());
			String targetObj = getTypeName(tmpVar.type) + " " + tmpVar.name;

			ActivityNode an = fva.getObject().getIncoming().get(0).getSource();
			String sourceObj = getNodeSource(an);

			String sourceAtr = fva.getStructuralFeature().getName();

			out.append(targetObj + " = " + sourceObj + "." + sourceAtr + ";\n");
		}

		if (node instanceof CallAction) {
			CallAction ca = (CallAction) node;
			out.append(callToCode(ca));
		}

		else if (node instanceof ConditionalNode) {
			ConditionalNode cn = (ConditionalNode) node;
			boolean first = true;
			for (Clause clause : cn.getClause()) {
				String var = null;
				if (clause.getDecider().getIncoming().size() == 0) {
					var = getNodeSource(clause.getDecider());
				} else {
					TmpVar tmpVar = TmpVar.getVar(clause.getDecider().getIncoming().get(0).getSource());
					if (tmpVar != null)
						var = tmpVar.name;
					else
						var = "<NO>";
				}
				out.append((!first ? "else " : "") + "if (" + var + ") {\n");
				first = false;

				// getActivityBodyCodeInputFirst(out,
				// listToActivityNodeList(clause.getBody()), clause);
				getActivityBodyCodeInputFirst(out, listToActivityNodeList(clause.getBody()));

				out.append("}\n");
			}
		}

		// TODO
		else if (node instanceof LoopNode) {
			LoopNode ln = (LoopNode) node;

			// SETUP
			int i = 0;
			for (OutputPin op : ln.getLoopVariable()) {
				TmpVar loopVar = TmpVar.getOrSetVar(op, op.getType());
				out.append(getTypeName(loopVar.type) + " " + loopVar.name + " = "
						+ getEdgeSource(ln.getLoopVariableInput().get(i).getIncoming().get(0)) + ";\n");
				getActivityBodyCodeInputFirst(out, listToActivityNodeList(ln.getSetupPart()));
				i++;
			}

			out.append("while (true) {\n");

			// DECIDER
			getActivityBodyCodeInputFirst(out, ln.getDecider());
			String var = null;
			if (ln.getDecider().getIncoming().size() == 0)
				var = getNodeSource(ln.getDecider());
			else
				var = getNodeSource(ln.getDecider().getIncoming().get(0).getSource());
			out.append("if (!(" + var + ")) break;\n");

			// BODY
			getActivityBodyCodeInputFirst(out, listToActivityNodeList(ln.getBodyPart()));

			// PREPIS promennych cyklu
			i = 0;
			for (OutputPin ip : ln.getLoopVariable()) {
				if (ln.getBodyOutput().get(i).getIncoming().size() <= 0) continue;
				String source = getEdgeSource(ln.getBodyOutput().get(i).getIncoming().get(0));
				//ActivityNode outNode = ln.getBodyOutput().get(i).getIncoming().get(0).getSource();
				//TmpVar outVar = TmpVar.getOrSetVar(outNode);
				out.append(getNodeSource(ip) + " = " + source +";\n");
				i++;
			}

			out.append("}\n");
		}
	}
}
