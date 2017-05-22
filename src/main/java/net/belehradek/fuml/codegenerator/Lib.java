package net.belehradek.fuml.codegenerator;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class Lib {
	
	public static void setZeroAnchor(Node node) {
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
	}
	
	public static String toFilePath(String path) {
		return "file:////" + path.replace('\\', '/');
	}
	
	/*public static void callNonpublicMethod(Class<?> class_, Object object, String methodName, Object[] params) {
		
	}*/
}
