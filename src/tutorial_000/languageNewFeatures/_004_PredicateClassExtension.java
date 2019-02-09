package tutorial_000.languageNewFeatures;

import java.util.function.Predicate;

public class _004_PredicateClassExtension {

	public static void main(String[] args) {
		/*
		 * Java 11 add a not() method to predicate, that do the same work as negate() method, but in a more readable way.
		 */
		// check String emptiness with negate().
		final Predicate<String> isBlanck = String::isBlank;
		
		System.out.println(isBlanck.test("   ")); // true.
		System.out.println(isBlanck.negate().test("   ")); // false.
		
		System.out.println("-------------------------------------");
		
		// Perform the same example with not().
		final Predicate<String> notBlank = Predicate.not(String::isBlank);
		System.out.println(notBlank.test("    ")); // false.
		System.out.println(notBlank.test("toto")); // true.
	}

}
