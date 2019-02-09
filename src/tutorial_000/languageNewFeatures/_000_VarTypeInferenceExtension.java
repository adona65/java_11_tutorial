package tutorial_000.languageNewFeatures;

import java.util.function.IntFunction;

public class _000_VarTypeInferenceExtension {

	public static void main(String[] args) {
		/*
		 * With java 11, var reserved word can now also be used to specify type in lambda expressions.  
		 */
		IntFunction<Integer> doubleItWithVar = (var x) -> x * 2;
		
		System.out.println(doubleItWithVar.apply(2));
		
		/*
		 * This is usefull if you get the desire to not have to explicitly specify a type, but nevertheless to be able to define the parameter 
		 * definitively or to add annotations. This is not possible if the lambda version is used completely without a type. For example :
		 "
		 	Function<String, String> trimmer = (@Notnull var str) -> str.trim();
		 " 
		 */
		
	}

}
