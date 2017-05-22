package net.belehradek.fuml.codegenerator.old;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelListSequence;
import freemarker.template.TemplateScalarModel;
import freemarker.template.Version;
import net.belehradek.fuml.codegenerator.Global;

import java.util.ArrayList;
import java.util.List;

import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Package;

public class ModelObjectWrapper extends DefaultObjectWrapper {
	
	public ModelObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @Override
    protected TemplateModel handleUnknownType(final Object obj) throws TemplateModelException {
        
    	if (obj instanceof Package) {
    		Global.log("ModelObjectWrapper instanceof package");
    		Package p = (Package) obj;
			return new TemplateHashModel() {
				@Override
				public boolean isEmpty() throws TemplateModelException {
					return false;
				}
				@Override
				public TemplateModel get(String key) throws TemplateModelException {
					if (key.equals("package")) {
						List<NamedElement> l = new ArrayList<>(p.getOwnedMember());
						l.removeIf(e -> !(e instanceof Class_));
						return new TemplateModelListSequence(l);
					}
					return null;
				}
			};
		}
    	
    	else if (obj instanceof Class_) {
    		Global.log("ModelObjectWrapper: class");
    		Class_ p = (Class_) obj;
			return new TemplateScalarModel() {
				@Override
				public String getAsString() throws TemplateModelException {
					return p.getName();
				}
			};
		}

    	return super.handleUnknownType(obj);
    }
}
