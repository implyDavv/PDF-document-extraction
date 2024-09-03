import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;



public class Txt {
	public static void main(String[] args) throws IOException {
		  File file = new File("f:/00 小论文/00 Paper/2 表格抽取/2 实例/3 文献集/" 
		  		   		+ "3-49 挤压铸造压力对6061铝合金铸件组织及力学性能的影响/" + 
		  		   		"挤压铸造压力对6061铝合金铸件组织及力学性能的影响_李鲁.pdf");
	        
		 	int file_len = file.getAbsolutePath().length();	
	    	PrintStream old = System.out;
	    	try	
	    	{ // 文件输出路径
		        PrintStream out = new PrintStream(file.getAbsolutePath().substring(0, file_len - 4) + "_textout.txt");
		        System.setOut(out);
		    }	
	    	catch( FileNotFoundException e)   
	    	{
		        e.printStackTrace();
		    }
		 
		// 为给定名称的文件创建输入流实例,文件输入流读取PDF
		InputStream is = new FileInputStream(file); 
		// 创建PDF解析器，输入流传入转换器PDFParser
		PDFParser parser = new PDFParser(is); 
		// 解析PDF文档
		parser.parse(); 
		// 获得解析后的PDF文档，从转换器获得PDDocument
		PDDocument document = parser.getPDDocument(); 
		// 创建PDF文本剥离器
		PDFTextStripper stripper = new PDFTextStripper(); 
		// 设置文本不按照本来的位置排序（会有分栏）
        stripper.setSortByPosition(false);
        System.out.println( stripper.getText(document) );
        stripper.setStartPage(3);
        stripper.setEndPage(3);
		// 使用剥离器从PDF文档中剥离文本信息
        Map< Integer ,  String >  lineMap = new LinkedHashMap< Integer ,  String >();
       
        stripper.setLineSeparator("\r\n");
		String text = stripper.getText(document);
		//
		String t = text.replaceAll("\r\n|\r|\n", "").replaceAll(" ", "").replaceAll("　", "");
		for( String lineRaw : text.split(  stripper.getLineSeparator())  ){	
			System.out.println( lineRaw );		
			System.out.println( "*****************************************" );
			String title = lineRaw.replaceAll("\r\n|\r|\n", "").replaceAll(" ", "").replaceAll("　", "");
			int key = t.indexOf(title);
			lineMap.put(key, lineRaw);
		}
		//System.out.println( lineMap );

		System.setOut(old);
		
		if ( is != null ) {
			is.close();   //关闭输入流对象
		}
		if ( document != null ) {
			document.close();
		}
	}

}
