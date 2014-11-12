package com.legobuilder0813.MagicAssistant.Utils;

public class NumberUtil {

	public static boolean isInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
}