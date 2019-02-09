package tutorial_000.languageNewFeatures;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class _005_TimeUnitClassExtension {

	public static void main(String[] args) {
		/*
		 * JAva 11 add a convertion(duration) method to TimeUnit class, that allow to convert a Duration Object to 
		 * a Timeunit one.
		 */
		final TimeUnit days = TimeUnit.DAYS;
		System.out.println(days.convert(Duration.ofDays(7))); // 7
		System.out.println(days.convert((Duration.ofHours(72)))); // 3
		System.out.println(days.convert((Duration.ofHours(42)))); // 1 (because of round made during convertion process)
	}

}
