package net.belehradek.fuml.codegenerator;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.TemplateException;

import org.modeldriven.alf.uml.Package;

public class TemplateEngineFile extends TemplateEngine {
	
	protected File outputRoot;
	
	public TemplateEngineFile(File outputRoot) {
		super();
		this.outputRoot = outputRoot;
	}

	public void processPackage(Package pack, String rootNamespace) throws TemplateException, IOException {
		Map<String, Object> model = new HashMap<>();
		model.put("model", new UmlTemplateWrapper(pack, pack, rootNamespace));
		model.put("createFile", new CreateFileDirective(outputRoot));
		super.processNullWriter(model);
	}
}
