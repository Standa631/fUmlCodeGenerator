package net.belehradek.fuml.codegenerator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class test1 {

	@Test
	public void test() throws IOException {
		CodeGenerator.main(new String[] {
				"-l", "..\\fUmlLibraries",
				"-m", "..\\fUmlTest\\src\\main\\alf",
				"-n", "App",
				"-o", "out",
				"-p", "net.belehradek.out",
				"-t", "..\\fUmlTest\\src\\main\\ftl\\androidRoot.ftl",
				"-u", "mapping"
		});
	}

}
