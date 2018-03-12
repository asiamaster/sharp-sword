package com.dili.http.okhttp.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * In-memory compile Java source code as String.
 * 
 * @author asiamaster
 */
public class JavaStringCompiler {
	protected static final Logger logger = LoggerFactory.getLogger(JavaStringCompiler.class);
	JavaCompiler compiler;
	StandardJavaFileManager stdManager;

	public JavaStringCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.stdManager = compiler.getStandardFileManager(null, null, null);
	}

	/**
	 * Compile a Java source file in memory.
	 * 
	 * @param fileName
	 *            Java file name, e.g. "Test.java"
	 * @param source
	 *            The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 *         class binary as value.
	 * @throws IOException
	 *             If compile error.
	 */
	public Map<String, byte[]> compile(String fileName, String source) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
			String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			logger.info("====classpath初始地址:"+path);
			path = java.net.URLDecoder.decode(path, "UTF-8");
			int firstIndex = 0;
			if(path.lastIndexOf(System.getProperty("path.separator")) != -1) {
				firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
			}
//			int lastIndex = path.lastIndexOf(File.separator) + 1;
			int lastIndex = path.indexOf(".jar!") + 1;
			path = path.substring(firstIndex, path.lastIndexOf("/")+1);
			logger.info("====classpath转换地址:"+path);
			List<String> opts = Arrays.asList("-Xlint:unchecked", "-d", path);
//			opts.add("-target");
//			opts.add("1.8");
			CompilationTask task = compiler.getTask(null, manager, null, opts, null, Arrays.asList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new RuntimeException("Compilation failed.");
			}
			return manager.getClassBytes();
		}
	}



	/**
	 * Load class from compiled classes.
	 * 
	 * @param name
	 *            Full class name.
	 * @param classBytes
	 *            Compiled results as a Map.
	 * @return The Class instance.
	 * @throws ClassNotFoundException
	 *             If class not found.
	 * @throws IOException
	 *             If load error.
	 */
	public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
		try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
			return classLoader.loadClass(name);
		}
	}
}
