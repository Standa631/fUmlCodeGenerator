package net.belehradek.fuml.codegenerator.old;

import java.io.IOException;
import java.util.List;

import org.modeldriven.alf.fuml.execution.AlfCompiler;
import org.modeldriven.alf.syntax.units.Member;
import org.modeldriven.alf.syntax.units.UnitDefinition;
import org.modeldriven.alf.uml.ElementFactory;
import org.modeldriven.alf.uml.Package;

import net.belehradek.fuml.codegenerator.Global;

public class MyAlfNoMapping extends AlfCompiler {

	public MyAlfNoMapping() {
		setIsVerbose(true);
		setIsParseOnly(true);
		setModelDirectory("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf");
		setLibraryDirectory("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries");
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
		return null;
	}

	@Override
	protected void printUsage() {
		Global.log("MyAlf: printUsage()");
	}
	
	
}
