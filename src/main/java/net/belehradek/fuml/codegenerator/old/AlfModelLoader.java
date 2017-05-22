package net.belehradek.fuml.codegenerator.old;

import java.io.File;
import java.io.IOException;

import org.modeldriven.alf.eclipse.fuml.execution.AlfCompiler;
import org.modeldriven.alf.eclipse.units.RootNamespaceImpl;
import org.modeldriven.alf.syntax.units.UnitDefinition;
import org.modeldriven.alf.uml.Package;

import net.belehradek.fuml.codegenerator.Lib;

public class AlfModelLoader extends AlfCompiler {

	public AlfModelLoader(File libraryDirectory, File modelDirectory, File umlDirectory) {
		super();
		
		setIsVerbose(true);
		setLibraryDirectory(libraryDirectory.getAbsolutePath());
		setModelDirectory(modelDirectory.getAbsolutePath());
		setUmlDirectory(Lib.toFilePath(umlDirectory.getAbsolutePath()));
	}
	
	public Package loadModel(String name, boolean isFile) throws IOException {
		loadResources();
		UnitDefinition unit = parse(name, isFile);
		
		RootNamespaceImpl root = ((RootNamespaceImpl)getRootScopeImpl());
		root.getResource(Lib.toFilePath("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\fuml\\FumlTest.uml"));
		
		unit = parse(name, isFile);
		
		process(unit);
		Package model = getModel(unit);
		return model;
	}
}