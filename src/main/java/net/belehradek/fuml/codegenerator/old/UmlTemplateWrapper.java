package net.belehradek.fuml.codegenerator.old;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.ActivityEdge;
import org.modeldriven.alf.uml.ActivityNode;
import org.modeldriven.alf.uml.Behavior;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.ObjectFlow;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Parameter;
import org.modeldriven.alf.uml.Property;
import org.modeldriven.alf.uml.Stereotype;
import org.modeldriven.alf.uml.StereotypeApplication;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelListSequence;
import freemarker.template.TemplateNumberModel;

public class UmlTemplateWrapper implements TemplateHashModel, TemplateMethodModelEx {
	
	protected Object rootModel;
	protected Object model;
	protected String rootNamespace = null;
	
	public UmlTemplateWrapper(Object rootModel, Object model, String rootNamespace) {
		this.rootModel = rootModel;
		this.model = model;
		this.rootNamespace = rootNamespace;
	}
	
	public UmlTemplateWrapper(Object rootModel, Object model) {
		this.rootModel = rootModel;
		this.model = model;
	}
	
	protected Object getValue(String key) {
		if (model instanceof Package) {
			Package p = (Package) model;
			if (key.equals("allClasses")) {
				return UmlFrameworkWrapper.getAllClasses(p, false);
			} else if (key.equals("allActivityClasses")) {
				return UmlFrameworkWrapper.getAllActivityClasses(p);
			}
		}
		
		else if (model instanceof ActivityNode) {
			ActivityNode node = (ActivityNode) model;
			if (key.equals("name")) {
				return node.getName();
			} else if (key.equals("qualifiedName")) {
				return node.getQualifiedName();
			}
		}
		
		else if (model instanceof Activity) {
			Activity o = (Activity) model;
			if (key.equals("name")) {
				return o.getName();
			} else if (key.equals("type")) {
				if (UmlWrapper.getReturnParameter(o) != null)
					return UmlWrapper.getReturnParameter(o).getType().getName();
				else
					return null;
			} else if (key.equals("visibility")) {
				//"public", "private", "protected", "package"
				return o.getVisibility();
			} else if (key.equals("parameters")) {
				return UmlWrapper.getNonReturnParameters(o);
			} else if (key.equals("body")) {
				return UmlFrameworkWrapper.getActivityBodyString(o);
			} else if (key.equals("code")) {
				return UmlFrameworkWrapper.getActivityBodyCode(o);
			} else if (key.equals("nodes")) {
				return UmlFrameworkWrapper.getActivityAllNodes(o);
			} else if (key.equals("leafNodes")) {
				return UmlFrameworkWrapper.getActivityAllLeafNodes(o);
			}
		}
		
		else if (model instanceof Class_) {
			Class_ c = (Class_) model;
			if (key.equals("name")) {
				return c.getName();
			} else if (key.equals("isTemplate")) {
				return c.isTemplate();
			} else if (key.equals("qualifiedName")) {
				return c.getQualifiedName();
			} else if (key.equals("packageName")) {
				String s = c.getQualifiedName().replaceAll("::", ".");
				if (rootNamespace != null)
					s = s.replace("Model.", rootNamespace + ".");
				int index = s.lastIndexOf(".");
				if (index >= 0)
					s = s.substring(0, s.lastIndexOf("."));
				else
					s = "NoNamespace";
				return s;
			} else if (key.equals("attributes")) {
				return UmlWrapper.getAttributes(c);
			} else if (key.equals("operations")) {
				return UmlWrapper.getOperations(c);
			} else if (key.equals("activities")) {
				return UmlWrapper.getActivities(c);
			} else if (key.equals("visibility")) {
				//"public", "private", "protected", "package"
				return c.getVisibility();
			} else if (key.equals("isActivity")) {
				return UmlFrameworkWrapper.isActivity(c);
			} else if (key.equals("isPersistent")) {
				return UmlFrameworkWrapper.isPersistent(c);
			}
		}
		
		else if (model instanceof Property) {
			Property p = (Property) model;
			if (key.equals("name")) {
				return p.getName();
			} else if (key.equals("type")) {
				if (p.getType() != null)
					return p.getType().getName();
				else
					return null;
			} else if (key.equals("visibility")) {
				//"public", "private", "protected", "package"
				return p.getVisibility();
			}
		}
		
		else if (model instanceof Operation) {
			Operation o = (Operation) model;
			if (key.equals("name")) {
				return o.getName();
			} else if (key.equals("type")) {
				if (o.getType() != null)
					return o.getType().getName();
				else
					return null;
			} else if (key.equals("visibility")) {
				//"public", "private", "protected", "package"
				return o.getVisibility();
			} else if (key.equals("parameters")) {
				//"public", "private", "protected", "package"
				return UmlWrapper.getNonReturnParameters(o);
			} else if (key.equals("activity")) {
				List<Behavior> bh = o.getMethod();
				if (bh.size() >= 1 && bh.get(0) instanceof Activity)
					return (Activity) bh.get(0);
				else
					return null;
			} else if (key.equals("isStartActivity")) {
				return UmlFrameworkWrapper.isStartActivity(o);
			} else if (key.equals("getStartActivity")) {
				return UmlFrameworkWrapper.getStartActivity(o);
			}
		}
		
		else if (model instanceof Parameter) {
			Parameter p = (Parameter) model;
			if (key.equals("name")) {
				return p.getName();
			} else if (key.equals("type")) {
				if (p.getType() != null)
					return p.getType().getName();
				else
					return null;
			}
		}
		
		return null;
	}
	
	protected TemplateModel WrapObject(Object obj) {
		//null
		if (obj == null) {
			return SimpleScalar.newInstanceOrNull(null);
		}
		
		//string
		if (obj instanceof String) {
			return SimpleScalar.newInstanceOrNull((String) obj);
		}
		
		//boolean
		else if (obj instanceof Boolean) {
			return new TemplateBooleanModel() {
				@Override
				public boolean getAsBoolean() throws TemplateModelException {
					return (Boolean)obj;
				}
			};
		}
		
		//number
		else if (obj instanceof Integer) {
			return new TemplateNumberModel() {
				@Override
				public Number getAsNumber() throws TemplateModelException {
					return (Number) obj;
				}
			};
		}
		
		//list
		else if (obj instanceof List) {
			List<UmlTemplateWrapper> lw = new ArrayList<>();
			for (Object o : (List<?>) obj) {
					lw.add(wrap(o));
			}
			return new TemplateModelListSequence(lw);
		}
		
		return wrap(obj);
	}
	
	public UmlTemplateWrapper wrap(Object o) {
		return new UmlTemplateWrapper(rootModel, o, rootNamespace);
	}
	
	//-------------------------------------------------------------------------

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		Object ret = getValue(key);
		return WrapObject(ret);
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return (model instanceof Collection && ((Collection)model).isEmpty());
	}

	//-------------------------------------------------------------------------
	
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		
		return null;
	}
}