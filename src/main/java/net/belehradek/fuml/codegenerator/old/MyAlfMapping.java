package net.belehradek.fuml.codegenerator.old;

import java.io.IOException;

import org.modeldriven.alf.eclipse.fuml.execution.AlfCompiler;
import org.modeldriven.alf.syntax.units.UnitDefinition;
import org.modeldriven.alf.uml.Package;

public class MyAlfMapping extends AlfCompiler {
	
	@Override
	public void saveModel(String name, Package model) throws IOException {
		super.saveModel(name, model);
	}
	
	@Override
	public void setUmlDirectory(String umlDirectory) {
		super.setUmlDirectory(umlDirectory);
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
}
