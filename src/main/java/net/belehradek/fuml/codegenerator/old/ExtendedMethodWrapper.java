package net.belehradek.fuml.codegenerator.old;

import java.util.ArrayList;
import java.util.List;

import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Stereotype;

public class ExtendedMethodWrapper extends MethodWrapper {

	protected Object rootModel;
	
	public ExtendedMethodWrapper(Object model, Object rootModel) {
		super(model);
		this.rootModel = rootModel;
	}

	@Override
	protected Object callMethod(String methodName) {
		if (model instanceof Package) {
			Package p = (Package) model;
			if (methodName.equals("classes")) {
//				List<NamedElement> classes = (List<NamedElement>) super.callMethod("getOwnedMember");
//				List<NamedElement> out = new ArrayList<>(classes);
//				out.removeIf(l -> !(l instanceof Class_));
//				return out;
				return UmlFrameworkWrapper.getAllClasses(p, false);
			}
		}
		
		if (model instanceof Class_) {
			Class_ c = (Class_) model;
			if (methodName.equals("isStereoActivity")) {
				Package root = (Package) rootModel;
				List<NamedElement> members = new ArrayList<>(root.getOwnedMember());
				members.removeIf(l -> !(l instanceof Stereotype));
				Stereotype stereo = (Stereotype) members.get(0);
				return c.isStereotypeApplied(stereo);
			}
		}
		
		return super.callMethod(methodName);
	}
	
	@Override
	public MethodWrapper wrap(Object o) {
		return new ExtendedMethodWrapper(o, rootModel);
	}
}
