package tutorial_000.languageNewFeatures;

import java.util.stream.Stream;

public class _001_StringImprovements {

	public static void main(String[] args) {
		/*
		 * Java 11 add a bunch of helper methods to the String class.
		 * 
		 * isBlank() : in the past, we used to use external libraries to check a String contained only whitespace. Java 11 introduced the method 
		 * isBlank()based on Character.isWhitespace(int). 
		 */
		final String exampleText1 = "";
		final String exampleText2 = " ";
		final String exampleText3 = " \n \t ";
		final String exampleText4 = " \n \t4 ";
		System.out.println(exampleText1.isBlank()); // true
		System.out.println(exampleText2.isBlank()); // true
		System.out.println(exampleText3.isBlank()); // true
		System.out.println(exampleText4.isBlank()); // false
		
		System.out.println("=====================================");
		
		/*
		 * lines() : return a Stream<String> containing all lines of a given String object.
		 */
		final String exampleText = "1 This is a\n2 multi line\n3 text with\n4 four lines!";
		final Stream<String> lines = exampleText.lines();
		lines.forEach(System.out::println);
		
		System.out.println("=====================================");
		
		/*
		 * repeat(int) : returns a string whose value is the concatenation of this string repeated count times. If this string is empty or count is zero 
		 * then the empty string is returned.
		 */
		final String star = "*";
		System.out.println(star.repeat(30));
		final String delimeter = " -*- ";
		System.out.println(delimeter.repeat(6));
		
		System.out.println("=====================================");
		
		/*
		 * strip(), stripLeading(), stripTrailing() : methods use for removing leading and trailing / leading / trailing blanks in a String. This methods
		 * use a slightly different definition of whitespace than trim() method. The method strip()is based on Character.isWhitespace(int).
		 */
		final String trimExampleText1 = " abc ";
		final String trimExampleText2 = " \t XYZ \t ";
		System.out.println("'" + trimExampleText1.strip() + "'");
		System.out.println("'" + trimExampleText2.strip() + "'");
		System.out.println("'" + trimExampleText2.stripLeading() + "'");
		System.out.println("'" + trimExampleText2.stripTrailing() + "'");
	}
}
