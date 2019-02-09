package tutorial_000.languageNewFeatures;

public class _006_SingleFileLaunch {
	/*
	 * One of the criticisms of Java is that it can be verbose in its syntax and the "ceremony" associated with running even 
	 * a trivial application can make it hard to approach as a beginner. To write an application that just prints "Hello World!", 
	 * it requires you to write a class with a public static void main  method and use the  System.out.println method. Having done 
	 * this, you must then compile the code using javac. Finally, you can run the application to be welcomed into the world. Doing 
	 * the same thing in most scripting languages is significantly simpler and quicker.
	 * 
	 * Java 11's release eliminates the need to compile a single-file application, so now you can simply type "java HelloWorld.java"
	 * in command line. The Java launcher will identify that the file contains Java source code and will compile the code to a class 
	 * file before executing it.
	 * 
	 * Parameters placed after the name of the source file are passed as parameters when executing the application. Parameters placed 
	 * before the name of the source file are passed as parameters to the Java launcher after the code has been compiled. This allows 
	 * for things like the classpath to be set on the command line. Parameters that are relevant to the compiler (such as the classpath) 
	 * will also be passed to javac for compilation. For example :
	 "
	 	java -classpath /home/foo/java Hello.java Bonjour
	 "
	 * would be equivalent to :
	 "
	 	javac -classpath /home/foo/java Hello.java
		java -classpath /home/foo/java Hello Bonjour
	 "
	 *
	 * This release also provides "shebang" support. To reduce the need to even mention the Java launcher on the command line, this can be 
	 * included on the first line of the source file. For example :
	 "
	 	#!/usr/bin/java --source 11
   		public class HelloWorld {...}
	 "
	 * It is necessary to specify the –source flag with the version of Java to use.
	 */
}
