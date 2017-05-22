package net.belehradek.fuml.codegenerator.old;

import java.util.ArrayList;
import java.util.List;

import org.modeldriven.alf.uml.Stereotype;
import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.ActivityNode;
import org.modeldriven.alf.uml.Behavior;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.Model;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Parameter;
import org.modeldriven.alf.uml.Property;
import org.modeldriven.alf.uml.StructuredActivityNode;

import net.belehradek.fuml.codegenerator.Global;

import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Operation;

public class UmlWrapper {
	
	public static List getAllOfType(List in, Class<?> type) {
		List out = new ArrayList<>(in);
		for (Object o : in) {
			//if (type.isInstance(o)) { //bere i subclass
			if (type.equals(o.getClass())) { //bere jen shodnou tridu
				Global.log(o + " instanceof " + type);
				out.add(o);
			}
		}
		return out;
	}
	
	public static boolean isGeneralized(Classifier classifier, String generalQualifiedName) {
		for (Classifier c : classifier.getGeneral()) {
			String qn = c.getQualifiedName();
			if (qn.equals(generalQualifiedName))
				return true;
		}
		return false;
	}
	
	public static List<Class_> getClasses(Package pack) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<Class_> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
			if (elem instanceof Class_) {
				Class_ c = (Class_) elem;
				out.add(c);
			}
		}
		return out;
		
//		return getAllOfType(pack.getOwnedMember(), Class_.class);
	}
	
	public static List<Class_> getAllClasses(Package pack) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<Class_> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
//			if (elem instanceof Model) {
//				
//			} else 
			if (elem instanceof Package) {
				Package p = (Package) elem;
				out.addAll(getAllClasses(p));
			} else if (elem instanceof Class_) {
				Class_ c = (Class_) elem;
				out.add(c);
			}
		}
		return out;
	}
	
	public static List<Package> getAllPackages(Package pack) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<Package> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
			if (elem instanceof Package) {
				Package p = (Package) elem;
				out.add(p);
				out.addAll(getAllPackages(p));
			}
		}
		return out;
	}
	
	public static Class_ findClassByName(Package pack, String name) {
		List<Class_> cls = getAllClasses(pack);
		for (Class_ cl : cls) {
			if (cl.getName().contains(name))
				return cl;
		}
		return null;
	}
	
	public static List<NamedElement> getAllClassesAndModels(Package pack) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<NamedElement> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
			if (elem instanceof Model) {
				out.add((NamedElement)elem);
			} else if (elem instanceof Package) {
				Package p = (Package) elem;
				out.addAll(getAllClassesAndModels(p));
			} else if (elem instanceof Stereotype) {
				Global.log("???????????" + elem.getClass().getName());
			} else if (elem instanceof Class_) {
				Class_ c = (Class_) elem;
				out.add(c);
			} else {
				Global.log("???????????" + elem.getClass().getName());
			}
		}
		return out;
//		return getAllOfType(pack.getOwnedMember(), Class_.class);
	}
	
	public static List<Package> getPackages(Package pack) {
		List<NamedElement> classifiers = pack.getOwnedMember();
		List<Package> out = new ArrayList<>();
		for (NamedElement elem : classifiers) {
			if (elem instanceof Model) {
				
			} else if (elem instanceof Package) {
				Package c = (Package) elem;
				out.add(c);
			}
		}
		return out;
//		return getAllOfType(pack.getOwnedMember(), Package.class);
	}

	public static List<Property> getAttributes(Class_ c) {
		return c.getOwnedAttribute();
	}

	public static List<Operation> getOperations(Class_ c) {
		return c.getOwnedOperation();
	}

	public static List<Activity> getActivities(Class_ c) {
		List<Activity> out = new ArrayList<>();
		for (Classifier elem : c.getNestedClassifier()) {
			if (elem instanceof Activity) {
				Activity a = (Activity) elem;
				out.add(a);
			}
		}
		return out;
	}

	public static List<Parameter> getParameters(Operation o) {
		return o.getOwnedParameter();
	}

	public static Parameter getReturnParameter(Operation o) {
		for (Parameter p : o.getOwnedParameter()) {
			if (p.getDirection().toLowerCase().equals("return"))
				return p;
		}
		return null;
	}

	public static List<Parameter> getNonReturnParameters(Operation o) {
		List<Parameter> out = new ArrayList<>(getParameters(o));
		out.removeIf(p -> p.getDirection().toLowerCase().equals("return"));
		return out;
	}

	public static List<Parameter> getParameters(Activity o) {
		return o.getOwnedParameter();
	}

	public static Parameter getReturnParameter(Activity o) {
		for (Parameter p : o.getOwnedParameter()) {
			if (p.getDirection().toLowerCase().equals("return"))
				return p;
		}
		return null;
	}

	public static List<Parameter> getNonReturnParameters(Activity o) {
		List<Parameter> out = new ArrayList<>(getParameters(o));
		out.removeIf(p -> p.getDirection().toLowerCase().equals("return"));
		return out;
	}

	public static String getAttributeString(Property property) {
		String visibility = "";
		return property.getType().getName() + " " + property.getName();
	}

	public static String getOperationString(Operation operation) {
		Parameter ret = getReturnParameter(operation);
		String out = "";
		if (ret != null) {
			out = ret.getType().getName() + " ";
		}
		out += operation.getName();
		out += "(";
		for (Parameter p : getNonReturnParameters(operation)) {
			out += p.getType().getName() + " " + p.getName() + ", ";
		}
		out += ")";
		return out;
	}

	public static String getActivityString(Activity activity) {
		Parameter ret = getReturnParameter(activity);
		String out = "";
		if (ret != null) {
			out = ret.getType().getName() + " ";
		}
		out += activity.getName();
		out += "(";
		for (Parameter p : getNonReturnParameters(activity)) {
			out += p.getType().getName() + " " + p.getName() + ", ";
		}
		out += ")";
		return out;
	}

	public static void printBehavior(Behavior behavior) {
		if (behavior instanceof Activity) {
			Activity a = (Activity) behavior;
			System.out.println("Activity: " + a.getName());

			for (StructuredActivityNode n : a.getStructuredNode()) {
				System.out.println("Action: " + n.getName());
				printAction(n);
			}
		}
	}

	public static void printAction(StructuredActivityNode node) {
		for (ActivityNode a : node.getNode()) {
			if (a instanceof StructuredActivityNode) {
				printAction((StructuredActivityNode) a);
			} else {
				System.out.println("Activity node: " + a.getName() + " - " + a);
			}
		}
	}
}
