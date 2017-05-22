package net.belehradek.fuml.codegenerator.old;

import java.io.File;
import java.io.IOException;

import org.modeldriven.alf.eclipse.fuml.execution.AlfCompiler;
import org.modeldriven.alf.syntax.units.UnitDefinition;
import org.modeldriven.alf.uml.Package;

import net.belehradek.fuml.codegenerator.Lib;

public class XmiModelSaver extends AlfCompiler {

	public XmiModelSaver() {
		super();
		
		//setUmlDirectory(uml.getAbsolutePath());
		//setModelDirectory(uml.getAbsolutePath());
		//setLibraryDirectory(library.getAbsolutePath());
	}
	
//	public void saveModel(String path, String name, UnitDefinition unit) throws IOException {	
//		setUmlDirectory(path);
//		saveModel(name, unit);
//	}
	
	public void saveModel(Package model, String path, String name) throws IOException {	
		setUmlDirectory(path);
		saveModel(name, model);
	}
	
	public void saveModel(Package model, File target) throws IOException {	
//		setUmlDirectory(Lib.toFilePath(target.getParent()));
//		String name = target.getName();
//		saveModel(name.substring(0, name.lastIndexOf(".")), model);
		
		String name = target.getName();
		name = name.substring(0, name.lastIndexOf("."));
		saveModel(model, Lib.toFilePath(target.getParent()), name);
	}
}
