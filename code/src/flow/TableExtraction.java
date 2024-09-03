package flow;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;


public class TableExtraction {
	
	void testExtraction ( PDDocument document ) throws IOException {
		List<?> allPages = document.getDocumentCatalog().getAllPages();
		for ( int pageNum=0; pageNum<allPages.size(); pageNum++) {
			PDPage page = (PDPage) allPages.get(pageNum);	
			System.out.println( "page = "+ (pageNum+1) );
			//Preprocess pre = new Preprocess();
			//String txt = pre.preprocessByPage(document, pageNum);
			// 获得字符坐标
			GetTextPosition pos = new GetTextPosition();
		    pos.getWordPositionMap(page);
		    // 获得行
		    Line line = new Line();       
		    line.makeLine(  );	
		    
		    // 获得表格数据
		    TableDatas td = new TableDatas();
		    td.getTableDatas ();
		    /*/*/
		}
	}	
}
