package com.dili.http.okhttp.java;

import com.dili.ss.mbg.beetl.ZipUtils;
import com.dili.ss.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ResourceUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
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
	//判断是否运行过，已经运行过则不进行解压
	private static boolean isRun = false;

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
//			String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//			logger.info("====classpath初始地址:"+path);
//			path = java.net.URLDecoder.decode(path, "UTF-8");
//			int firstIndex = 0;
//			if(path.lastIndexOf(System.getProperty("path.separator")) != -1) {
//				firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
//			}
////			int lastIndex = path.lastIndexOf(File.separator) + 1;
//			int lastIndex = path.indexOf(".jar!") + 1;
//			path = path.substring(firstIndex, path.lastIndexOf("/")+1);
//			logger.info("====classpath转换地址:"+path);
//			List<String> opts = Arrays.asList("-Xlint:unchecked", "-d", path);
//			opts.add("-target");
//			opts.add("1.8");
            boolean isJar = isJarRun();
            if( isJar && !isRun){
                String jarDirPath = new ApplicationHome(getClass()).getDir().getAbsolutePath();
                String jarPath = new ApplicationHome(getClass()).getSource().getAbsolutePath();
                ZipUtils.unzip(new File(jarDirPath), new File(jarPath));
                isRun = true;
            }
            Iterable options = isJar ? Arrays.asList("-classpath", buildClassPath("./BOOT-INF/lib/")) : null;
			CompilationTask task = compiler.getTask(null, manager, null, options, null, Arrays.asList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new RuntimeException("Compilation failed.");
			}
			return manager.getClassBytes();
		}
    }

    private boolean isJarRun() {
//        ProtectionDomain protectionDomain = getClass().getProtectionDomain();
//        CodeSource codeSource = protectionDomain.getCodeSource();
//        URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
//        String path = (location == null ? null : location.getSchemeSpecificPart());
//        File root = new File(path);
//        return root.isDirectory() ? false : true;
		try {
			File file = new File(ResourceUtils.getURL("classpath:").getPath());
			return file.getAbsolutePath().endsWith("\\BOOT-INF\\classes!") ? true : false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//取不到路径默认为包模式
			return true;
		}
	}

    private static String buildClassPath(String libRelativePath){
        String jarDirPath = new ApplicationHome(JavaStringCompiler.class).getDir().getAbsolutePath();
        File libDir = new File(jarDirPath, libRelativePath);
        File[] jars = libDir.listFiles();
        StringBuilder classpath = new StringBuilder();
        for(File jar : jars){
            classpath.append(libRelativePath).append(jar.getName()).append(";");
        }
        return classpath.toString();
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
	    Object obj = SpringUtil.getBean("valueProviderUtils");
	    if(obj == null){
            try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
                return classLoader.loadClass(name);
            }
        }else {
            try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes, obj.getClass().getClassLoader())) {
                return classLoader.loadClass(name);
            }
        }
	}
}
