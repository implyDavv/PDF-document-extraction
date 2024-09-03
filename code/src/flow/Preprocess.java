package flow;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class Preprocess {
	
	String text = "";
	
	Preprocess() {
		// TODO Auto-generated constructor stub
	}
	
	
	String preprocessByPage( PDDocument document, int page ) throws IOException	{	
		// 创建PDF文本剥离器
		PDFTextStripper stripper = new PDFTextStripper(); 
		// 设置文本不按照本来的位置排序
        stripper.setSortByPosition( false );
        stripper.setStartPage(page+1);
        stripper.setEndPage(page+1);
		// 使用剥离器从PDF文档中剥离文本信息
		String txt = stripper.getText( document ) ;
		// 字符格式标准化
		Check check = new Check();
		txt = check.full2Half( txt );
		txt.replaceAll(" ", "");
		//System.out.println( txt );
		return txt;
	}
	
	
	Preprocess( PDDocument document ) throws IOException	{	
		text = "";
		// 创建PDF文本剥离器
		PDFTextStripper stripper = new PDFTextStripper(); 
		// 设置文本不按照本来的位置排序
        stripper.setSortByPosition( false );
		// 使用剥离器从PDF文档中剥离文本信息
		text = stripper.getText( document ) ;
		// 字符格式标准化
		Check check = new Check();
		text = check.full2Half( text );
	}
	



	String getAbstract ( ) throws IOException {
		String abs = "";
		String x = "摘";
		int start = text.indexOf(x);
		
		int end = -1;
		String regex = "关键[字词]";
		Pattern p = Pattern.compile( regex );
		Matcher m = p.matcher( text );
		if( m.find() ) {
			String y = m.group().toString();
			end = text.indexOf( y );
		} 
		else {
			end = -1;
		}
		
		int mid = -1;
		String regex1 = "Abstract";
		Pattern p1 = Pattern.compile( regex1, Pattern.CASE_INSENSITIVE );
		Matcher m1 = p1.matcher( text );
		if( m1.find() ){
			String z = m1.group().toString();
			mid = text.indexOf(z);
		} 
		else {
			mid = -1;
		}
		
		if( start!= -1 && end!= -1 && start<end ) {
			abs = text.substring(start,end);
		} 
		else if( start == -1 && end!= -1 ){
			abs = text.substring( 20, end );		
		} 
		else if( start!= -1 && end == -1 && mid!= -1 && start<mid ){
			abs = text.substring( start,mid );
		} 
		else {
			abs = text;
		}		
		return abs;
	}

	String getBody ( ) throws IOException {
		String body = "";
		
		int start = -1;
		String regex1 = "(Key ?words {0,}:(.*)\\s)";
		Pattern p1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
		Matcher m1 = p1.matcher( text );
		if( m1.find() ) {
			String x = m1.group().toString();
			//System.out.print( x );
			start = text.indexOf(x) + x.length();		
		} 
		else {
			start = -1;
		}
		
		int end = -1;	
		String regex2 = "(参 {0,}考 {0,}文 {0,}献|Reference)";
		Pattern p2 = Pattern.compile( regex2, Pattern.CASE_INSENSITIVE);
		Matcher m2 = p2.matcher( text );
		if( m2.find() ) {
			String y = m2.group().toString();
			end = text.indexOf(y);
		} 
		else{
			end = -1;
		}		
		
		if( start!= -1 && end!= -1 && start<end ) {
			body = text.substring( start,end );
		} 
		else if( start!= -1 &&  end == -1 ){
			body = text.substring(start);
		} 
		else if( start == -1 && end!= -1 ) {
			body = text.substring( text.indexOf("Key")+100, end );
		} 
		else {
			body = text;
		}
		return body;
	}
	
	
	String getText( PDDocument document ) throws IOException {
		// 创建对象，保存文献中所有的字符串
		String text = null;
		/*// 为给定名称的文件创建输入流实例,文件输入流读取PDF
		InputStream is = new FileInputStream( file ); 
		// 创建PDF解析器，输入流传入转换器PDFParser
		PDFParser parser = new PDFParser(is); 
		// 解析PDF文档
		parser.parse();  
		// 获得解析后的PDF文档，从转换器获得PDDocument
		PDDocument document = parser.getPDDocument(); */

		// 创建PDF文本剥离器
		PDFTextStripper stripper = new PDFTextStripper(); 
		// 设置文本不按照本来的位置排序
        stripper.setSortByPosition( false );
		// 使用剥离器从PDF文档中剥离文本信息
		text = stripper.getText( document );  
		if ( document != null ) {
			document.close();
		}		
		// 字符格式标准化
		Check check = new Check();
		text = check.full2Half( text );
		//System.out.print( text );
		return text;			
	} 

	
	
}
