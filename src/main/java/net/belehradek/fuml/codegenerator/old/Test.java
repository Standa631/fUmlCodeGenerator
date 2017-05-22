package net.belehradek.fuml.codegenerator.old;

import java.io.File;
import java.io.IOException;

import org.modeldriven.alf.uml.Package;

import net.belehradek.fuml.codegenerator.Global;

public class Test {

	protected String libPath = "C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries";
	protected String modelAlfPath = "C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf";
	protected String modelFumlPath = "C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\fuml";
	protected String unitName = "App";
	
	protected String fumlBuildPath = "C:\\Users\\Bel2\\DIP\\fUmlTest\\build\\fuml";
	protected String targetPath = "C:\\Users\\Bel2\\DIP\\fUmlTest\\build\\fuml\\out.uml";
	
	public static void main(String[] args) throws IOException {
		System.out.println("Test");
		new Test().run();
		System.out.println("Done");
	}

	public void run() throws IOException {
	
//		XmiModelLoader xmiModelLoader = new XmiModelLoader(new File(libPath), new File(modelFumlPath));
//		Package model2 = xmiModelLoader.loadModel("Test");
//		Global.log("Xmi load");
//		Global.log(model2);
		
		Global.log("--------------------------------------------------");
		
		AlfModelLoader alfModelLoader = new AlfModelLoader(new File(libPath), new File(modelAlfPath), new File(modelFumlPath));
//		RootNamespaceImpl root = ((RootNamespaceImpl)alfModelLoader.getRootScopeImpl());
//		root.getResource(Lib.toFilePath("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\fuml\\Test.uml"));
		Package model = alfModelLoader.loadModel(unitName, false);
		Global.log("Alf load");
		Global.log(model);
		
		Global.log("--------------------------------------------------");
		
		//OK
//		Package model3 = model;
//		for (PackageableElement el : model2.getPackagedElement()) {
//			model3.addPackagedElement(el);
//		}

		//naopak nejede, neco se stereotypem
//		Package model3 = model2;
//		for (PackageableElement el : model.getPackagedElement()) {
//			model3.addPackagedElement(el);
//		}
		
		//Model v modelu - blbe
//		Package model3 = model2;
//		model3.addPackagedElement(model);
		
		Global.log("--------------------------------------------------");
		
		Global.log("Xmi save");
		Global.log(model);
		XmiModelSaver xmiModelSaver = new XmiModelSaver();
		//xmiModelSaver.setModelDirectory(modelFumlPath);
		//xmiModelSaver.setLibraryDirectory(libPath);
		xmiModelSaver.saveModel(model, new File(targetPath));
		
		Global.log("--------------------------------------------------");
		
//		XmiModelLoader xmiModelLoader2 = new XmiModelLoader(new File(libPath), new File(fumlBuildPath));
//		Package model4 = xmiModelLoader2.loadModel("out");
//		Global.log("Xmi load");
//		Global.log(model4);
	}
}
