package net.belehradek.fuml.codegenerator;

import java.io.PrintStream;

import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Class_;

public class Global {
	
	protected static PrintStream loggerStream = null;
	
	public static void log(String text) {
		System.out.println("Log: " + text);
		
		if (loggerStream != null) {
			loggerStream.println("Log: " + text);
		}
	}
	
	public static void log(Package pack) {
		if (pack == null) return;
		
		log(pack.getClass().getName() + ": " + pack.getName());
		for (NamedElement e : pack.getMember()) {
			log(e);
		}
	}
	
	public static void log(NamedElement e) {
		log(e.getClass().getName() + ": " + e.getName());
	}
	
	public static void logRecursive(Package pack, boolean library) {
//		if (!library && UmlWrapper.isLibrary(pack)) return;
//		log(pack.getClass().getName() + ": " + pack.getQualifiedName());
//		for (NamedElement e : pack.getOwnedMember()) {
//			if (!UmlWrapper.isLibrary(e))
//				log(e.getClass().getName() + ": " + e.getQualifiedName());
//			if (e instanceof Package) {
//				logRecursive((Package)e, library);
//			}
//		}
		
		for (Class_ c : UmlFrameworkWrapper.getAllClasses(pack, library)) {
			log(c);
		}
	}
	
	public static PrintStream getLoggerStream() {
		return loggerStream;
	}
	
	public static void setLoggerStream(PrintStream loggerStream) {
		Global.loggerStream = loggerStream;
	}
}
