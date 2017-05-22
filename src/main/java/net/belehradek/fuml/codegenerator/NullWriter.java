package net.belehradek.fuml.codegenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class NullWriter extends Writer {

	@Override
	public void close() throws IOException {

	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

	}
}
