package net.belehradek.fuml.codegenerator.old;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.belehradek.fuml.codegenerator.Global;

public class CreateFileDirective implements TemplateDirectiveModel {
	
	protected static final String PARAM_NAME_FILENAME = "fileName";
	protected static final String PARAM_NAME_FILEPATH = "filePath";
	
	protected File rootDir;
	
	public CreateFileDirective(File rootDir) {
		this.rootDir = rootDir;
	}

    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException {

    	String fileName = null;
    	if (params.get(PARAM_NAME_FILENAME) != null)
    		fileName = params.get(PARAM_NAME_FILENAME).toString();
    	
    	String filePath = null;
    	if (params.get(PARAM_NAME_FILEPATH) != null)
    		filePath = params.get(PARAM_NAME_FILEPATH).toString();

        if (body != null && fileName != null) {
        	File file = null;
        	if (filePath != null)
        		file = new File(rootDir, preparePathString(filePath) + fileName);
        	else
        		file = new File(rootDir, fileName);
        	
        	Global.log("CreateFileDirective: " + file.getAbsolutePath());
        	file.getParentFile().mkdirs();
        	file.createNewFile();
        	FileWriter fw = new FileWriter(file);
        	BufferedWriter bw = new BufferedWriter(fw);
        	body.render(bw);
        	bw.close();
        }
    }
    
    protected String preparePathString(String path) {
    	String out = path.replace(".", "\\");
    	out = out.replace("::", "\\");
    	out += "\\";
    	return out;
    }
}