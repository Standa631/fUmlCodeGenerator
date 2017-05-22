package net.belehradek.fuml.codegenerator.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modeldriven.alf.uml.Action;
import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.ActivityEdge;
import org.modeldriven.alf.uml.ActivityNode;
import org.modeldriven.alf.uml.ActivityParameterNode;
import org.modeldriven.alf.uml.Behavior;
import org.modeldriven.alf.uml.CallBehaviorAction;
import org.modeldriven.alf.uml.CallOperationAction;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.CreateObjectAction;
import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.ForkNode;
import org.modeldriven.alf.uml.InputPin;
import org.modeldriven.alf.uml.LiteralBoolean;
import org.modeldriven.alf.uml.LiteralInteger;
import org.modeldriven.alf.uml.LiteralString;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.ObjectFlow;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.OutputPin;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Parameter;
import org.modeldriven.alf.uml.Pin;
import org.modeldriven.alf.uml.Property;
import org.modeldriven.alf.uml.StructuredActivityNode;
import org.modeldriven.alf.uml.Type;
import org.modeldriven.alf.uml.ValueSpecification;
import org.modeldriven.alf.uml.ValueSpecificationAction;

import net.belehradek.fuml.codegenerator.Global;

public class UmlFrameworkWrapper extends UmlWrapper {

	public static String[] libraryPrefixes = { "Model::Alf", "Model::FoundationalModelLibrary", "Alf::Library",
			"FoundationalModelLibrary", "PrimitiveTypes" };

	public static String activityGeneral = "Model::ActivityLibrary::Activity";
	public static String persistentGeneral = "Model::PersistentLibrary::Persistent";

	public static String startActivityOperation = "startActivity";

	public static boolean isLibrary(NamedElement elem) {
		if (elem != null && elem.getQualifiedName() != null) {
			for (String lib : libraryPrefixes) {
				if (elem.getQualifiedName().equals(lib) || elem.getQualifiedName().startsWith(lib + "::"))
					return true;
			}
		}
		return false;
	}

	public static boolean isActivity(Class_ c) {
		// return c.getName().toLowerCase().contains("activity");
		return isGeneralized(c, activityGeneral);
	}

	public static boolean isStartActivity(Operation o) {
		// return o.getName().toLowerCase().matches("go.*activity");
		return o.getName().equals(startActivityOperation);
	}

	public static boolean isPersistent(Class_ c) {
		// return c.getName().toLowerCase().contains("persistent");
		return isGeneralized(c, persistentGeneral);
	}

	// -------------------------------------------------------------------------

	public static Class_ getStartActivity(Operation o) {
		Parameter p = o.getOwnedParameter().get(0);
		Type t = p.getType();
		if (t instanceof Class_) {
			return (Class_) t;
		}
		return null;
	}

	public static List<Class_> getAllClasses(Package pack, boolean library) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<Class_> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
			if (isLibrary(elem))
				continue;

			if (elem instanceof Package) {
				Package p = (Package) elem;
				out.addAll(getAllClasses(p, library));
			} else if (elem instanceof Class_) {
				Class_ c = (Class_) elem;
				out.add(c);
			}
		}
		return out;
	}

	public static List<Package> getAllPackages(Package pack, boolean library) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<Package> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
			if (isLibrary(elem))
				continue;

			if (elem instanceof Package) {
				Package p = (Package) elem;
				out.add(p);
				out.addAll(getAllPackages(p, library));
			}
		}
		return out;
	}

	public static String getActivityChange(Operation o) {
		Pattern pattern = Pattern.compile("go(.*Activity)");
		Matcher matcher = pattern.matcher(o.getName());
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	// ---------------------------------------------------------------------EDGE

	public static ActivityNode getEdgeSource(ActivityEdge edge) {
		return edge.getSource();
	}

	public static ActivityNode getEdgeTarget(ActivityEdge edge) {
		return edge.getTarget();
	}

	// ---------------------------------------------------------------------NODE

	public static List<ActivityEdge> getActivityNodeIn(ActivityNode node) {
		return node.getIncoming();
	}

	public static List<ActivityEdge> getActivityNodeOut(ActivityNode node) {
		return node.getOutgoing();
	}

	// -----------------------------------------------------------------ACTIVITY

	public static List<Class_> getAllActivityClasses(Package pack) {
		List<Class_> out = new ArrayList<>();
		for (Class_ elem : getAllClasses(pack, false)) {
			if (isActivity(elem)) {
				out.add(elem);
			}
		}
		return out;
	}

	public static List<ActivityNode> getActivityAllLeafNodes(Activity activity) {
		List<ActivityNode> nodes = new ArrayList<>(getActivityAllNodes(activity));
		nodes.removeIf(i -> !i.getIsLeaf());
		return nodes;
	}

	public static List<ActivityNode> getActivityAllNodes(Activity activity) {
		List<ActivityNode> nodes = new ArrayList<>();
		getActivityAllNodesRecursive(nodes, activity.getNode());
		return nodes;
	}

	public static void getActivityAllNodesRecursive(List<ActivityNode> nodes, List<ActivityNode> in) {
		for (ActivityNode node : in) {
			if (node instanceof StructuredActivityNode) {
				nodes.add(node);
				StructuredActivityNode sn = (StructuredActivityNode) node;
				getActivityAllNodesRecursive(nodes, sn.getNode());
			} else {
				nodes.add(node);
			}
		}
	}

	// -------------------------------------------------------------------------

	static List<ActivityNode> done = new ArrayList<>();

	public static String getActivityBodyString(Activity activity) {
		done.clear();

		StringBuilder s = new StringBuilder();
		getActivityBodyStringRecurse(s, 0, activity.getNode());
		return s.toString();
	}

	public static void getActivityBodyStringRecurse(StringBuilder out, int level, List<ActivityNode> nodes) {
		for (ActivityNode n : nodes) {
			getActivityBodyStringRecurse(out, level + 1, n);
			if (n instanceof StructuredActivityNode) {
				StructuredActivityNode sn = (StructuredActivityNode) n;
				getActivityBodyStringRecurse(out, level + 1, sn.getNode());
			}
		}
	}

	public static void getActivityBodyStringRecurse(StringBuilder out, int level, ActivityNode node) {
		boolean doit = true;
		if (done.contains(node))
			doit = false;
		done.add(node);

		String space = String.join("", Collections.nCopies(level, " "));
		out.append(space + (doit ? "" : "*") + node.getName() + "  " + node.getClass().getName() + "\n");

		if (!doit)
			return;

		for (ActivityEdge inEdge : node.getIncoming()) {
			ActivityNode source = inEdge.getSource();

			if (source instanceof Pin) {
				Pin p = (Pin) source;
				ActivityNode e = (ActivityNode) p.getOwner();
				out.append(space + "-IN: -> " + e.getName() + "  " + e.getClass().getName() + "\n");
				if (doit)
					getActivityBodyStringRecurse(out, level + 1, e);
			} else {
				out.append(space + "-IN: " + source.getName() + "  " + source.getClass().getName() + "\n");
				if (doit)
					getActivityBodyStringRecurse(out, level + 1, source);
			}
		}

		for (ActivityEdge outEdge : node.getOutgoing()) {
			ActivityNode target = outEdge.getTarget();

			out.append(space + "-OU: " + target.getName() + "  " + target.getClass().getName() + "\n");
			getActivityBodyStringRecurse(out, level + 1, target);

			if (target instanceof Pin) {
				Pin p = (Pin) target;
				ActivityNode e = (ActivityNode) p.getOwner();
				out.append(space + "-OU: -> " + e.getName() + "  " + e.getClass().getName() + "\n");
				if (doit)
					getActivityBodyStringRecurse(out, level + 1, e);
			} else {
				out.append(space + "-OU: " + target.getName() + "  " + target.getClass().getName() + "\n");
				if (doit)
					getActivityBodyStringRecurse(out, level + 1, target);
			}
		}

		// TODO:
		/*
		 * if (node instanceof StructuredActivityNode) { StructuredActivityNode
		 * snode = (StructuredActivityNode) node; for (InputPin inPin :
		 * snode.getStructuredNodeInput()) { inPin.getIncoming()
		 * 
		 * ActivityNode source = inEdge.getSource();
		 * 
		 * if (source instanceof Pin) { Pin p = (Pin) source; ActivityNode e =
		 * (ActivityNode) p.getOwner(); out.append(space + "-IN: -> " +
		 * e.getName() + "  " + e.getClass().getName() + "\n"); if (doit)
		 * getActivityBodyStringRecurse(out, level + 1, e); } else {
		 * out.append(space + "-IN: " + source.getName() + "  " +
		 * source.getClass().getName() + "\n"); if (doit)
		 * getActivityBodyStringRecurse(out, level + 1, source); } }
		 * 
		 * for (ActivityEdge outEdge : node.getOutgoing()) { ActivityNode target
		 * = outEdge.getTarget();
		 * 
		 * out.append(space + "-OU: " + target.getName() + "  " +
		 * target.getClass().getName() + "\n");
		 * getActivityBodyStringRecurse(out, level + 1, target);
		 * 
		 * if (target instanceof Pin) { Pin p = (Pin) target; ActivityNode e =
		 * (ActivityNode) p.getOwner(); out.append(space + "-OU: -> " +
		 * e.getName() + "  " + e.getClass().getName() + "\n"); if (doit)
		 * getActivityBodyStringRecurse(out, level + 1, e); } else {
		 * out.append(space + "-OU: " + target.getName() + "  " +
		 * target.getClass().getName() + "\n"); if (doit)
		 * getActivityBodyStringRecurse(out, level + 1, target); } } }
		 */
	}

	// --------------------------

	// public static ActivityNode getSourceNode(ActivityEdge edge) {
	// ActivityNode source = edge.getSource();
	// //if (source.getin)
	// }
	//
	// public static ActivityNode getTargetNodes(ActivityEdge edge) {
	//
	// }

	static int tmpCounter = 0;
	static Map<String, String> varType = new HashMap<>();
	static Map<Element, String> tmpVar = new HashMap<>();

	public static String getTmpVar() {
		return "_tmp_" + tmpCounter++;
	}

	public static String getActivityBodyCode(Activity activity) {
		varType.clear();
		tmpVar.clear();
		tmpCounter = 0;

		StringBuilder s = new StringBuilder();
		getActivityBodyCodeRecurse(s, 0, activity.getNode());
		return s.toString();
	}

	public static void getActivityBodyCodeRecurse(StringBuilder out, int level, List<ActivityNode> nodes) {
		for (ActivityNode n : nodes) {
			getActivityBodyCodeRecurse(out, level + 1, n);
			if (n instanceof StructuredActivityNode) {
				StructuredActivityNode sn = (StructuredActivityNode) n;
				getActivityBodyCodeRecurse(out, level + 1, sn.getNode());
			}
		}
	}

	public static void getActivityBodyCodeRecurse(StringBuilder out, int level, ActivityNode node) {
		nodeToCode(out, node);
	}

	public static String objectToVar(Object o) {
		return "_" + System.identityHashCode(o);
	}

	public static String getInBracket(String in) {
		Pattern pattern = Pattern.compile("[^(]\\(([^)]*)\\)");
		Matcher matcher = pattern.matcher(in);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getInBracket(String in, String start) {
		Pattern pattern = Pattern.compile(start + "\\(([^)]*)\\)");
		Matcher matcher = pattern.matcher(in);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String transformFunctionName(String name) {
		if (name.equals("Div")) {
			return "/";
		} else if (name.equals("Concat")) {
			return "+";
		}
		return name;
	}
	
	public static boolean isInfix(String name) {
		return name.equals("+") || name.equals("-") || name.equals("*") || name.equals("/");
	}

	public static String getForkIn(ForkNode node) {
		return "";
	}

	public static void nodeToCode(StringBuilder out, ActivityNode node) {
		boolean showClean = true;

		if (!showClean)
			out.append("//-" + node.getName() + "  " + node.getClass().getName() + "\n");

		// try {
		if (node instanceof ValueSpecificationAction) {
			/*
			 * ValueSpecificationAction vsa = (ValueSpecificationAction) node;
			 * ValueSpecification vs = vsa.getValue(); //TODO: boolean, integer,
			 * null, real, string, unlimitedNatural String var =
			 * objectToVar(vsa.getOutput().get(0).getOutgoing().get(0).
			 * getTarget()); out.append(var + " = "); if (vs instanceof
			 * LiteralString) { LiteralString ls = (LiteralString) vs;
			 * out.append("\"" + ls.getValue() + "\""); } else if (vs instanceof
			 * LiteralInteger) { LiteralInteger li = (LiteralInteger) vs;
			 * out.append("" + li.getValue()); } else {
			 * out.append(vs.toString()); } out.append(";"); out.append("\n");
			 */
		} else if (node instanceof CallBehaviorAction || node instanceof CallOperationAction
				|| node instanceof CreateObjectAction) {
			Action ac = null;
			String name = null;

			if (node instanceof CallBehaviorAction) {
				CallBehaviorAction cba = (CallBehaviorAction) node;
				ac = cba;
				Behavior b = cba.getBehavior();
				name = b.getName();
			}

			if (node instanceof CallOperationAction) {
				CallOperationAction cba = (CallOperationAction) node;
				ac = cba;
				Operation b = cba.getOperation();
				name = b.getName();
			}

			if (node instanceof CreateObjectAction) {
				CreateObjectAction cba = (CreateObjectAction) node;
				ac = cba;
				Classifier b = cba.getClassifier();
				name = b.getName();
			}

			String ret = "";
			StructuredActivityNode parent = (StructuredActivityNode) ac.getOwner();
			List<OutputPin> outPins = parent.getStructuredNodeOutput();
			if (outPins.size() > 0) {
				List<ActivityEdge> edges = outPins.get(0).getOutgoing();
				if (edges.size() > 0) {
					ActivityNode retNode = edges.get(0).getTarget();
					ret = retNode.getName();
				} else {
					Global.log("No edges");
				}
			} else {
				Global.log("No out pins");
			}
			
			if (ret.startsWith("Fork(")) {
				// promenna - Fork(x)@14984
				ret = getInBracket(ret);
			} else if (ret.startsWith("Call(")) {
				// docasna promenna - Call(+).arggument(x)
				// Element elm = retNode.getOwner().getOwner();
				String var = getTmpVar();

				Element elm = node.getOwner();
				tmpVar.put(elm, var);
				Global.log("Put tmp: " + elm + " -> " + var);

				ret = "var " + var;
			}
			
			if (!ret.isEmpty())
				out.append(ret + " = ");
			
			name = transformFunctionName(name);

			if (!isInfix(name))
				out.append(name + "(");

			boolean first = true;
			for (InputPin ip : ac.getInput()) {
				if (!first) {
					if (isInfix(name))
						out.append(" " + name + " ");
					else
						out.append(", ");
				}
				first = false;

				ActivityNode n = ip.getIncoming().get(0).getSource();
				if (n instanceof OutputPin) {
					OutputPin outPin = (OutputPin) n;
					String paramName = null;
					if (outPin.getName().startsWith("Value")) {
						paramName = getInBracket(outPin.getName());
					} else {
						Element elm = outPin.getOwner();
						paramName = tmpVar.get(elm);
						Global.log("Get tmp: " + elm + " -> " + paramName);
					}
					out.append(paramName);
				} else if (n instanceof ForkNode) {
					ForkNode fn = (ForkNode) n;
					String paramName = getInBracket(fn.getName());
					out.append(paramName);
				}
			}

			if (!isInfix(name))
				out.append(")");

			out.append(";");
			out.append("\n");
		} else if (node instanceof ForkNode) {
			ForkNode fn = (ForkNode) node;
			ActivityNode source = fn.getIncoming().get(0).getSource();

			// parametr do forku - nic negenerovat
			if (source instanceof InputPin) {

			} else if (source instanceof ForkNode || // z promenne
					(source instanceof OutputPin && source.getName().startsWith("Value(")) // z literalu
			) {
				String var = getInBracket(fn.getName());
				String typ = "";
				if (!varType.containsKey(var)) {
					//TODO: typ
					typ = "var";
					varType.put(var, typ);
					typ += " ";
				}
				
				//promenna v objektu -> cil v OUT:
				if (var.startsWith("LeftHandSide@")) {
					InputPin write = (InputPin) fn.getOutgoing().get(0).getTarget();
					typ = "";
					var = ((NamedElement)write.getOwner()).getName();
					var = "this." + getInBracket(var).replace("::", ".");
				}
				
				out.append(typ + var + " = " + getInBracket(source.getName()) + ";");
			}
			out.append("\n");
		} else if (node instanceof StructuredActivityNode) {
			StructuredActivityNode sn = (StructuredActivityNode) node;

			// deklarace lokalni promenne
			if (node.getName().startsWith("LocalNameDeclarationStatement@")) {

			} else if (node.getName().startsWith("ExpressionStatement@")) {

			} else if (node.getName().startsWith("RightHandSide@")) {
				Global.log("RightHandSide");
			}

		} else if (!showClean) {
			out.append("//<NO-CODE-FOR>\n");
			if (node.getName().startsWith("Tuple")) {
				Global.log("Tuple");
			}
		}
		// } catch (Exception e) {
		// out.append("//<ERROR> " + e.toString() + "\n");
		// }
	}
}
