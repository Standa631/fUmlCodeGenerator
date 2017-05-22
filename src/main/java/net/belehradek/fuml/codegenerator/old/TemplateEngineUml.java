package net.belehradek.fuml.codegenerator.old;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.TemplateException;

import org.modeldriven.alf.uml.Package;

public class TemplateEngineUml extends TemplateEngine {
	
	protected File root;
	
	public TemplateEngineUml(File root) {
		super();
		this.root = root;
	}

	public void processPackage(Package pack, String rootNamespace) throws TemplateException, IOException {
		Map<String, Object> model = new HashMap<>();
		model.put("model", new UmlTemplateWrapper(pack, pack, rootNamespace));
		model.put("createFile", new CreateFileDirective(root));
		super.processNullWriter(model);
	}
}
