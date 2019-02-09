package tutorial_000.languageNewFeatures;

import java.util.Optional;

public class _003_OptionalClassExtension {
	
	public static void main(String[] args) {
		/*
		 * Java 11 ad an isEmpty() method to the Optional class, mading it more consistent compared to to other APIs like 
		 * Collection and String. 
		 * 
		 * isEmpty() : Return true if the optional include no value, otherwise return false.
		 */
		Optional<String> optEmpty = Optional.empty();
		
		if (optEmpty.isEmpty()) {
		    System.out.println("The optional is empty.");
		}
		
		optEmpty = Optional.of("Toto");
	
		if (!optEmpty.isEmpty()) {
		    System.out.println("Not empty yet.");
		}
	}
}
