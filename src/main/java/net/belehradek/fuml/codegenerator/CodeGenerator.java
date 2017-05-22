package net.belehradek.fuml.codegenerator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.*;
import org.modeldriven.alf.uml.Package;

import freemarker.template.TemplateException;

public class CodeGenerator {

	public void run(String[] args) {
		Global.log("CodeGenerator " + String.join(" ", args));
		
		Options options = new Options();

        Option library = new Option("l", null, true, "fUML(ALF) library directory");
        library.setRequired(true);
        options.addOption(library);

        Option model = new Option("m", null, true, "model directory");
        model.setRequired(true);
        options.addOption(model);
        
        Option uml = new Option("u", null, true, "fUML mapping output directory");
        uml.setRequired(true);
        options.addOption(uml);
        
        Option unit = new Option("n", null, true, "unit name");
        unit.setRequired(true);
        options.addOption(unit);
        
        Option template = new Option("t", null, true, "freemarker template");
        template.setRequired(true);
        options.addOption(template);
        
        Option output = new Option("o", null, true, "codegeneration output dir");
        output.setRequired(true);
        options.addOption(output);
        
        Option namespace = new Option("p", null, true, "namespace prefix");
        namespace.setRequired(false);
        options.addOption(namespace);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
        	Global.log(e.getMessage());
            formatter.printHelp("fUmlCodeGenerator", options);
            
            Global.log("Usage exampe:");
            Global.log(" java -jar CodeGenerator.jar -l ...");
            System.exit(1);
            return;
        }

        String libraryPath = cmd.getOptionValue("l");
        String modelPath = cmd.getOptionValue("m");
        String umlPath = cmd.getOptionValue("u");
        String unitName = cmd.getOptionValue("n");
        String templatePath = cmd.getOptionValue("t");
        String outputPath = cmd.getOptionValue("o");
        String namespacePrefix = cmd.getOptionValue("p");
        
        //---------------------------------------------------------------------

        MyAlfMapping alf = new MyAlfMapping(modelPath, libraryPath);
		Package pack = alf.getModel(unitName);
		
		if (pack != null) {
			Global.log(pack);
			
			TemplateEngineFile engine = new TemplateEngineFile(new File(outputPath));
			try {
				engine.setTemplate(new File(templatePath));
				engine.processPackage(pack, namespacePrefix);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new CodeGenerator().run(args);
	}
}
