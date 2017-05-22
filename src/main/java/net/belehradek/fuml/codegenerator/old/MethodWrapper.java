package net.belehradek.fuml.codegenerator.old;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import freemarker.template.SimpleCollection;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelListSequence;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

public class MethodWrapper implements TemplateHashModel {
	
	protected Object model;
	
	public MethodWrapper(Object model) {
		this.model = model;
	}
	
	protected Object callMethod(String methodName) {
		try {
			
			Method method = model.getClass().getMethod(methodName);
			return method.invoke(model);
			
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected TemplateModel WrapObject(Object obj) {
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
			List<MethodWrapper> lw = new ArrayList<>();
			for (Object o : (List<?>) obj) {
					lw.add(wrap(o));
			}
			return new TemplateModelListSequence(lw);
		}
		
		return wrap(obj);
	}
	
	public MethodWrapper wrap(Object o) {
		return new MethodWrapper(o);
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		Object ret = callMethod(key);
		return WrapObject(ret);
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return (model instanceof List && ((List<?>)model).isEmpty());
	}
}
