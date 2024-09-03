package flow;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check {	
	
	/**
	 * Full 2 half-corner characters
	 * @param string
	 * @return half-corner String
	 */
	String full2Half ( String string )	{
		char[] chars = string.toCharArray();
		for(int i = 0 ; i< chars.length; i++) {
			int code = (int) chars[i];
			// 	是否全角
			if ( code >= 65281 && code <= 65374 ) {
				chars[i] = (char)(chars[i] - 65248);
			}
			else if( code == 12288 ) { // 空格  
				chars[i] = (char)( chars[i] -12288+32 );
			}
			else if( code == 65377 ) {  
				chars[i] = (char)(12290);
            }
			else if(code == 12539 ) {  
				chars[i] = (char)(183);
            }
			else if( code == 8226 )	{   //特殊字符 ·
				chars[i] = (char)(183);  
            }
		}
		return String.valueOf( chars );
	}
	
	
	/**
	  * 格式化 AllSentence
	  * 删除空格、回车、制表符等
	 * @param vectorSentence
	 * @return Vector<String> delBlank后的关键句组
	 * @throws IOException
	 */
	
	String delBlank ( String sentence )	{		
		// 删除句中的空格符、制表符、回车符	
		if(sentence != null) {
			Pattern p = Pattern.compile("\\s+|\t|\r|\n|\\S+");
			Matcher m = p.matcher( sentence );
			sentence = m.replaceAll("");				
		} 
		return sentence;
	}		
				
	
	/**
	 * check data | data + unit
	 * @param word
	 * @return boolean
	 */
	boolean checkData ( String word ) {		
		boolean a = false;
		String regex = "(^[0-9]+\\.?[0-9]*"
				+ "(?:MPa|kN|℃|s|min|%"
				+ "|mm/s|mm·s-1|mm/min|mm·min-1|m/s|m·s-1|m/min|m·min-1|[A-Z]{1}[a-z]{1}|HB)*"
				+ "|余量)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher( word );
		while( m.find() ) {
			a = true;
		}
		return a;
	}
	
}
