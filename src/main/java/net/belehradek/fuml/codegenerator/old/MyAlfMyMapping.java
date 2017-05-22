package net.belehradek.fuml.codegenerator.old;

import java.io.IOException;

import org.modeldriven.alf.eclipse.uml.Stereotype;
import org.modeldriven.alf.fuml.execution.AlfCompiler;
import org.modeldriven.alf.syntax.units.UnitDefinition;
import org.modeldriven.alf.uml.ElementFactory;
import org.modeldriven.alf.uml.Model;
import org.modeldriven.alf.uml.Package;

import net.belehradek.fuml.codegenerator.Global;

public class MyAlfMyMapping extends AlfCompiler {
	
	public MyAlfMyMapping() {
		setIsVerbose(true);
		setIsParseOnly(false);
		setModelDirectory("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf");
		setLibraryDirectory("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries");
	}
	
	public Package getModel(String unitName) {
		UnitDefinition unit = parse(unitName, false);
		process(unit);
		Package p = getModel(unit);
		return p;
	}
	
	public UnitDefinition getUnitDefinition(String unitName) {
		UnitDefinition unit = parse(unitName, false);
		process(unit);
		return unit;
	}

	@Override
	public void saveModel(String arg0, Package arg1) throws IOException {
		Global.log("MyAlf: saveModel()");
	}

	@Override
	protected ElementFactory createElementFactory() {
		Global.log("MyAlf: createElementFactory()");
		//return null;
		return org.modeldriven.alf.eclipse.uml.Element.FACTORY;
	}

	@Override
	protected void printUsage() {
		Global.log("MyAlf: printUsage()");
	}
	
}