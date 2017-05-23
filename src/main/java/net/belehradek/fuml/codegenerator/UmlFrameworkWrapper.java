package net.belehradek.fuml.codegenerator;

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
import org.modeldriven.alf.uml.AddStructuralFeatureValueAction;
import org.modeldriven.alf.uml.Behavior;
import org.modeldriven.alf.uml.CallAction;
import org.modeldriven.alf.uml.CallBehaviorAction;
import org.modeldriven.alf.uml.CallOperationAction;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.Clause;
import org.modeldriven.alf.uml.ConditionalNode;
import org.modeldriven.alf.uml.CreateObjectAction;
import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.ExecutableNode;
import org.modeldriven.alf.uml.ForkNode;
import org.modeldriven.alf.uml.InputPin;
import org.modeldriven.alf.uml.InvocationAction;
import org.modeldriven.alf.uml.LiteralBoolean;
import org.modeldriven.alf.uml.LiteralInteger;
import org.modeldriven.alf.uml.LiteralString;
import org.modeldriven.alf.uml.LoopNode;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.ObjectFlow;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.OutputPin;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Parameter;
import org.modeldriven.alf.uml.Pin;
import org.modeldriven.alf.uml.PrimitiveType;
import org.modeldriven.alf.uml.Property;
import org.modeldriven.alf.uml.StructuredActivityNode;
import org.modeldriven.alf.uml.TestIdentityAction;
import org.modeldriven.alf.uml.Type;
import org.modeldriven.alf.uml.TypedElement;
import org.modeldriven.alf.uml.ValueSpecification;
import org.modeldriven.alf.uml.ValueSpecificationAction;

public class UmlFrameworkWrapper extends UmlWrapper {

	public static String[] libraryPrefixes = { 
			"Model::Alf", 
			"Model::FoundationalModelLibrary", 
			"Alf::Library",
			"FoundationalModelLibrary", 
			"PrimitiveTypes",
			"Model::PrimitiveTypes",
			
			"Model::ActivityLibrary",
			"Model::PersistentLibrary",
			"Model::App"};

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
		if (o.getOwnedParameter().size() != 2)
			return false;
		Parameter p1 = o.getOwnedParameter().get(0);
		if (p1.getLower() != 0 || p1.getUpper() != 0)
			return false;
		if (!(p1.getType() instanceof Class_))
			return false;
		Class_ c = (Class_) p1.getType();
		if (!isGeneralized(c, activityGeneral))
			return false;
		Parameter p2 =  o.getOwnedParameter().get(1);
		if (p2.getLower() != 0 || p2.getUpper() != -1)
			return false;
		if (!(p2.getType() instanceof PrimitiveType))
			return false;
		if (!p2.getType().getName().equals("String"))
			return false;
		return true;
	}

	public static boolean isPersistent(Class_ c) {
		// return c.getName().toLowerCase().contains("persistent");
		return isGeneralized(c, persistentGeneral);
	}

	// -------------------------------------------------------------------------
	
	public static String getQualifiedTypeName(TypedElement elem, String rootNamespace) {
		return getQualifiedTypeName(elem.getType(), rootNamespace);
	}
	
	public static String getQualifiedTypeName(Type t, String rootNamespace) {
		String out;
		if (isLibrary(t))
			out = t.getName();
		else {
			out =  t.getQualifiedName();
			out = out.replaceAll("::", ".");
			if (rootNamespace != null)
				out = out.replace("Model.", rootNamespace + ".");
		}

		return out;
	}

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

	public static String activityNodeToString(ActivityNode node, int level, boolean io) {
		String space = String.join("", Collections.nCopies(level, " "));
		String out = "";

		if (!io) {
			out = node.getName() + "  " + node.getClass().getName() + "\n";
			return out;
		}

		// strukturovana aktivita - ma piny
		if (node instanceof StructuredActivityNode) {
			StructuredActivityNode san = (StructuredActivityNode) node;
			for (InputPin ip : san.getInput()) {
				out += space + "-IP: " + inPinToString(ip) + "\n";
				for (ActivityEdge ie : ip.getIncoming()) {
					out += space + "--IE: " + inEdgeToString(ie) + "\n";
				}
			}
			for (OutputPin op : san.getOutput()) {
				out += space + "-OP: " + outPinToString(op) + "\n";
				for (ActivityEdge oe : op.getOutgoing()) {
					out += space + "--OE: " + inEdgeToString(oe) + "\n";
				}
			}
		}

		for (ActivityEdge ie : node.getIncoming()) {
			out += space + "-IE: " + inEdgeToString(ie) + "\n";
		}
		for (ActivityEdge oe : node.getOutgoing()) {
			out += space + "-OE: " + outEdgeToString(oe) + "\n";
		}

		if (node instanceof CallAction) {
			CallAction cba = (CallAction) node;
			for (InputPin ip : cba.getArgument()) {
				out += space + "-IA: " + inPinToString(ip) + "\n";
				for (ActivityEdge ie : ip.getIncoming()) {
					out += space + "--IE: " + inEdgeToString(ie) + "\n";
				}
			}
			for (OutputPin op : cba.getResult()) {
				out += space + "-OA: " + outPinToString(op) + "\n";
				for (ActivityEdge oe : op.getOutgoing()) {
					out += space + "--OE: " + inEdgeToString(oe) + "\n";
				}
			}
		}

		return out;
	}

	public static String inPinToString(InputPin pin) {
		String out = pin.getName() + "  " + pin.getClass().getName();
		return out;
	}

	public static String outPinToString(OutputPin pin) {
		return pin.getName() + "  " + pin.getClass().getName();
	}

	public static String inEdgeToString(ActivityEdge edge) {
		return edge.getSource().getName() + "  " + edge.getClass().getName();
	}

	public static String outEdgeToString(ActivityEdge edge) {
		return edge.getTarget().getName() + "  " + edge.getClass().getName();
	}

	public static void getActivityBodyStringRecurse(StringBuilder out, int level, ActivityNode node) {
		boolean doit = true;
		if (done.contains(node))
			doit = false;
		done.add(node);

		String space = String.join("", Collections.nCopies(level, " "));
		out.append(space + (doit ? "" : "*") + activityNodeToString(node, level, false));

		if (!doit)
			return;

		out.append(activityNodeToString(node, level, true));
	}

	// --------------------------

	
}
