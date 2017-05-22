package net.belehradek.fuml.codegenerator.old;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modeldriven.alf.fuml.mapping.FumlMapping;
import org.modeldriven.alf.syntax.common.ElementReference;
import org.modeldriven.alf.syntax.common.SyntaxElement;
import org.modeldriven.alf.syntax.expressions.QualifiedName;
import org.modeldriven.alf.syntax.units.ClassDefinition;
import org.modeldriven.alf.syntax.units.ImportedMember;
import org.modeldriven.alf.syntax.units.Member;
import org.modeldriven.alf.syntax.units.NamespaceDefinition;
import org.modeldriven.alf.syntax.units.PackageDefinition;
import org.modeldriven.alf.syntax.units.StereotypeAnnotation;
import org.modeldriven.alf.syntax.units.UnitDefinition;
import org.modeldriven.alf.uml.Element;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Profile;

import net.belehradek.fuml.codegenerator.Global;

public class Test2 {

	public static void main(String[] args) {
		new Test2().run();
	}

	public void run() {
		Global.log("Test2");

		// alfParseTestNoMap();
		// alfParseTestMap();
		alfParseTestMapAnnotation();

		Global.log("Done");
	}

	public void alfParseTestNoMap() {
		MyAlfNoMapping alf = new MyAlfNoMapping();
		alf.setIsVerbose(true);
		alf.setIsParseOnly(true);
		alf.setModelDirectory("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf");
		alf.setLibraryDirectory("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries");
		UnitDefinition unit = alf.parse("App", false);
		alf.process(unit);

		List<Member> members = unit.getDefinition().getOwnedMember();
		Global.log("Root");
		Global.log(unit._toString(true));
		Global.log(unit.getDefinition()._toString(true));
		for (Member m : members) {
			logMember(m);
		}
	}

	protected void logMember(Member m) {
		Global.log(m.getName());

		NamespaceDefinition nd = null;
		if (m instanceof PackageDefinition) {
			// Global.log("PackageDefinition");
			PackageDefinition p = (PackageDefinition) m;
			for (Profile ap : p.getAppliedProfile()) {
				Global.log("-- AppliedProfile " + ap.getName());
			}
			nd = p;
		} else if (m instanceof NamespaceDefinition) {
			// Global.log("NamespaceDefinition");
			nd = (NamespaceDefinition) m;
		}

		for (StereotypeAnnotation sa : m.getAnnotation()) {
			QualifiedName name = sa.getStereotypeName();
			Global.log("-- Annotation " + name);
		}

		if (nd != null) {
			for (Member member : nd.getMember()) {
				logMember(member);
			}
		}

		// rekurzivne logovat potomky
		// m.getImpl().
	}

	public void alfParseTestMap() {
		MyAlfMapping alf = new MyAlfMapping();
		alf.setIsVerbose(true);
		alf.setIsParseOnly(false);
		alf.setModelDirectory("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf");
		alf.setLibraryDirectory("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries");
		alf.setUmlDirectory("UML");

		UnitDefinition unit = alf.getUnitDefinition("App");
		logMember(unit.getDefinition());

		// List<Member> members = unit.getDefinition().getOwnedMember();
		// Global.log(unit._toString(true));
		// Global.log(unit.getDefinition()._toString(true));
		// for (Member m : members) {
		//// Global.log(m._toString(true));
		//// Global.log("Position: " + m.getFileName() + "[" + m.getLine() + ":"
		// + m.getColumn() + "]");
		//
		// if (m instanceof PackageDefinition) {
		// PackageDefinition pd = (PackageDefinition) m;
		// for (Profile p : pd.getAppliedProfile()) {
		// Global.log("AppliedProfile " + p.getName() + " on " + m.getName());
		// }
		// }
		// }
		//
		Package p = alf.getModel("App");
		Global.log(p);

		UnitDefinition unit2 = alf.parse("App", false);
		FumlMapping fm = FumlMapping.getMapping(unit2);

		NamedElement c = null;
		NamedElement o = null;
		List<NamedElement> clist = UmlWrapper.getAllClassesAndModels(p);
		for (NamedElement cl : clist) {
			Global.log("--" + cl.getQualifiedName());
			if (cl.getName().equals("AnnotationTest"))
				c = cl;
			if (cl.getName().equals("HelloActivity"))
				o = cl;
		}

		// boolean b = StereotypeApplication.hasStereotypeApplication(o,
		// (org.modeldriven.alf.uml.Stereotype) c);
		// Global.log("-----" + b);
	}

	Map<Element, org.modeldriven.alf.uml.Stereotype> annotationMap = new HashMap<>();

	public void printAnnotation(Member m) {
		for (StereotypeAnnotation an : m.getAnnotation()) {
			org.modeldriven.alf.uml.Stereotype s = an.getStereotype();
			if (s == null) {
				Global.log("-- Annotation: null");
			} else {
				Global.log("-- Annotation: " + an.getStereotype().getName());
			}
		}
	}
	
	public void mapAnnotation(ClassDefinition cd) {
		Global.log("Class: " + cd);
		printAnnotation(cd);
	}

	public void mapAnnotationFor(NamespaceDefinition unit) {
		printAnnotation(unit);
		//NamespaceDefinition def = ud.getDefinition();
		//Global.log("Unit: " + unit._toString(false));

		for (Member m : unit.getMember()) {
			printAnnotation(m);
			if (m instanceof ImportedMember) {
				//Global.log("Imported");
				ImportedMember im = (ImportedMember) m;
				ElementReference ref = im.getReferent();
				
				//ExternalElementReference extRef = (ExternalElementReference) ref; 
				//InternalElementReference intRef = (InternalElementReference) ref;
				
				SyntaxElement se = ref.getImpl().getAlf();
				
				if (se instanceof Member)
					printAnnotation((Member)se);
				
				if (se instanceof NamespaceDefinition) {
					//Global.log("Namespace");
					NamespaceDefinition nd = (NamespaceDefinition) se;
					mapAnnotationFor(nd);
				}
				if (se instanceof ClassDefinition) {
					ClassDefinition cl = (ClassDefinition) se;
					mapAnnotation(cl);
					mapAnnotationFor(cl);
				}
			}
			
			if (m instanceof NamespaceDefinition) {
				//Global.log("Namespace");
				NamespaceDefinition nd = (NamespaceDefinition) m;
				mapAnnotationFor(nd);
			}

			if (m instanceof ClassDefinition) {
				//Global.log("Class");
				ClassDefinition cl = (ClassDefinition) m;
				mapAnnotation(cl);
				mapAnnotationFor(cl);
			}
		}
	}

	public void alfParseTestMapAnnotation() {
		MyAlfMapping alf = new MyAlfMapping();
		alf.setIsVerbose(true);
		alf.setIsParseOnly(false);
		alf.setModelDirectory("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf");
		alf.setLibraryDirectory("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries");
		alf.setUmlDirectory("UML");

		UnitDefinition unit = alf.getUnitDefinition("App");
		//logMember(unit.getDefinition());

		Package p = alf.getModel("App");
		Global.log("PPPPPPPPPPPPPPPPPPPPPPP");
		Global.logRecursive(p, false);

		UnitDefinition unit2 = alf.parse("App", false);
		FumlMapping fm = FumlMapping.getMapping(unit2);

		//Class_ A = UmlWrapper.findClassByName(p, "A");
		
		//FumlMapping.getElementFactory().
		//Global.log(A);
		
		Global.log("UUUUUUUUUUUUUUUUUUUUUU");
		mapAnnotationFor(unit2.getDefinition());
	}
}
