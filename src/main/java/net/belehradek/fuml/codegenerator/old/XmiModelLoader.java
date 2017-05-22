package net.belehradek.fuml.codegenerator.old;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.modeldriven.alf.eclipse.papyrus.execution.Fuml;
//import org.modeldriven.alf.eclipse.papyrus.execution.Fuml.ElementResolutionError;
import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.ActivityNode;
import org.modeldriven.alf.uml.Behavior;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Parameter;
import org.modeldriven.alf.uml.Property;
import org.modeldriven.alf.uml.StructuredActivityNode;

public class XmiModelLoader {// extends Fuml {
	
//	public XmiModelLoader(File libraryDirectory, File modelDirectory) {
//		super();
//		
//		setIsVerbose(true);
//		setUmlLibraryDirectory(libraryDirectory.getAbsolutePath());
//		setUmlDirectory(Lib.toFilePath(modelDirectory.getAbsolutePath()));
//	}
//	
//	public Package loadModel(String name) {
//		try {
//			getResource(name);
//			initializeEnvironment();
//			Package pack = getPackage("Model");
//			return pack;
//		} catch (ElementResolutionError e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/*public List<Class_> getClasses(Package pack) {
//		List<NamedElement> classifiers = pack.getOwnedMember();
//		List<Class_> out = new ArrayList<>();
//		for(NamedElement elem : classifiers) {		    
//		    if (elem instanceof Class_) {
//		    	Class_ c = (Class_) elem;
//		    		out.add(c);
//		    }
//		}
//		return out;
//	}
//	
//	public List<Property> getAttributes(Class_ c) {
//		return c.getOwnedAttribute();
//	}
//
//	public List<Operation> getOperations(Class_ c) {
//		return c.getOwnedOperation();
//	}
//	
//	public List<Classifier> getActivities(Class_ c) {
//		return c.getNestedClassifier();
//	}
//	
//	public List<Parameter> getParameters(Operation o) {
//		return o.getOwnedParameter();
//	}
//	public Parameter getReturnParameter(Operation o) {
//		for (Parameter p : o.getOwnedParameter()) {
//			if (p.getDirection().toLowerCase().equals("return"))
//				return p;
//		}
//		return null;
//	}
//	public List<Parameter> getNonReturnParameters(Operation o) {
//		List<Parameter> out = new ArrayList<>(getParameters(o));
//		out.removeIf(p -> p.getDirection().toLowerCase().equals("return"));
//		return out;
//	}
//	
//	public List<Parameter> getParameters(Activity o) {
//		return o.getOwnedParameter();
//	}
//	public Parameter getReturnParameter(Activity o) {
//		for (Parameter p : o.getOwnedParameter()) {
//			if (p.getDirection().toLowerCase().equals("return"))
//				return p;
//		}
//		return null;
//	}
//	public List<Parameter> getNonReturnParameters(Activity o) {
//		List<Parameter> out = new ArrayList<>(getParameters(o));
//		out.removeIf(p -> p.getDirection().toLowerCase().equals("return"));
//		return out;
//	}
//	
//	public String getAttributeString(Property property) {
//		String visibility = "";
//		return property.getType().getName() + " " + property.getName();
//	}
//	
//	public String getOperationString(Operation operation) {
//		Parameter ret = getReturnParameter(operation);
//		String out = "";
//		if (ret != null) {
//			out = ret.getType().getName() + " ";
//		}
//		out += operation.getName();
//		out += "(";
//		for (Parameter p : getNonReturnParameters(operation)) {
//			out += p.getType().getName() + " " + p.getName() + ", ";
//		}
//		out += ")";
//		return out;
//	}
//	
//	public String getActivityString(Activity activity) {
//		Parameter ret = getReturnParameter(activity);
//		String out = "";
//		if (ret != null) {
//			out = ret.getType().getName() + " ";
//		}
//		out += activity.getName();
//		out += "(";
//		for (Parameter p : getNonReturnParameters(activity)) {
//			out += p.getType().getName() + " " + p.getName() + ", ";
//		}
//		out += ")";
//		return out;
//	}
//		
//	public void printBehavior(Behavior behavior) {
//		if (behavior instanceof Activity) {			
//			Activity a = (Activity) behavior;
//			System.out.println("Activity: " + a.getName());
//			
//			for (StructuredActivityNode n : a.getStructuredNode()) {
//				System.out.println("Action: " + n.getName());
//				printAction(n);
//			}
//		}
//	}
//	
//	public static void printAction(StructuredActivityNode node) {		
//		for (ActivityNode a : node.getNode()) {
//			if (a instanceof StructuredActivityNode) {
//				printAction((StructuredActivityNode)a);
//			} else {
//				System.out.println("Activity node: " + a.getName() + " - " + a);
//			}
//		}
//	}*/
}
