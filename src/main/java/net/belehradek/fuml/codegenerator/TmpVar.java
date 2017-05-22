package net.belehradek.fuml.codegenerator;

import java.util.HashMap;
import java.util.Map;

import org.modeldriven.alf.uml.ActivityNode;
import org.modeldriven.alf.uml.Type;

public class TmpVar {
	public static int tmpCounter = 0;
	public static Map<ActivityNode, TmpVar> nodeToVar = new HashMap<>();

	public String name;
	public Type type;

	public TmpVar(Type t) {
		type = t;
		name = getTmpVar();
	}

	public static String getTmpVar() {
		return "_tmp_" + tmpCounter++;
	}

	public static TmpVar getVar(ActivityNode node) {
		return nodeToVar.get(node);
	}

	public static TmpVar getOrSetVar(ActivityNode node) {
		return getOrSetVar(node, null);
	}

	public static TmpVar getOrSetVar(ActivityNode node, Type type) {
		if (nodeToVar.containsKey(node)) {
			TmpVar out = nodeToVar.get(node);
			// Global.log("Get tmp: " + node + " -> " + out);
			return out;
		} else {
			TmpVar var = new TmpVar(type);
			// Global.log("Put tmp: " + node + " -> " + var.name);
			nodeToVar.put(node, var);
			return var;
		}
	}
}
