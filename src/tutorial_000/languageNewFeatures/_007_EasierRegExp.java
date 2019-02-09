package tutorial_000.languageNewFeatures;

import java.util.Arrays;
import java.util.regex.Pattern;

public class _007_EasierRegExp {
	public static void main(String[] args) {
		/*
		 * Java 11 introduce an asMatchPredicate() method for working with regular expressions. The asMatchPredicate() method creates 
		 * a new predicate from a compiled pattern. We then are able to call the test() method on it to perform tests with our pattern. 
		 */
		var words = Arrays.asList("dog", "Dog", "DOG", "Doggy");
		
        var dogPredicate = Pattern.compile("dog", Pattern.CASE_INSENSITIVE).asMatchPredicate();

        words.forEach((word) -> {
            if (dogPredicate.test(word)) {
                System.out.printf("%s matches%n", word);
            } else {
                System.out.printf("%s does not match%n", word);
            }
        });
	}
}
