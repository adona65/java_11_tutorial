package tutorial_000.languageNewFeatures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class _002_FilesImprovements {

	public static void main(String[] args) throws IOException {
		/*
		 * Java 11 made it easier to handle strings with files. It is now possible to write or read strings in a file. The utility class 
		 * java.nio.file.Files get few extensions in terms of methods writeString() and readString(). The following example shows how to 
		 * write or read a string in a file, that is UTF-8 encoded by default :
		 */
		var destinationPath = Path.of("ExampleFile.txt");
		Files.writeString(destinationPath, "1: This is a string to file test\n");
		Files.writeString(destinationPath, "2: Second line");
		final String line1 = Files.readString(destinationPath);
		final String line2 = Files.readString(destinationPath);
		System.out.println(line1);
		System.out.println(line2);
		
		/*
		 * This will output "2: Second line" two times because when writing, the mode of overwriting and not appending is not used.
		 */
		
		System.out.println("-------------------------------------");
		
		Files.writeString(destinationPath, "1: This is a string to file test\n");
		Files.writeString(destinationPath, "2: Second line", StandardOpenOption.APPEND);
		var line1Bis = Files.readString(destinationPath);
		var line2Bis = Files.readString(destinationPath);
		System.out.println(line1Bis);
		System.out.println(line2Bis);
		
		System.out.println("-------------------------------------");
		
		/*
		 * Now, it will display the full content of the file two times. We may use the String.lines() method to get lines one by one.
		 */
		var content = Files.readString(destinationPath);
		content.lines().forEach(System.out::println);
		
	}

}
