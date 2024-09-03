package pdfHandle;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GetRescourse {
	 public static void m(String[] args) throws IOException{
		 File file = new File("f:\\java\\表格","成分\\【36】成分、性能.pdf");		
		 InputStream is= new FileInputStream(file);				
		// Main enter point to work with PDF document.		
		 PDDocument document = PDDocument.load(is);
		 List<?> allPages = document.getDocumentCatalog().getAllPages();
		 int pageNum = document.getNumberOfPages();
		 System.out.println(pageNum);
		 // 按页读取文档				
		for( int i = 0; i<pageNum; i++) {
			PDPage page = (PDPage) allPages.get(i);
			if(page != null) {
				// 获取所有rescourse
				PDResources res = page.getResources();
				COSDictionary dic = res.getCOSDictionary();
				Set<COSName> set = dic.keySet();
				int size = dic.size();
				System.out.println(size);
				Iterator<COSName> iterator = set.iterator();
				while ( iterator.hasNext() ){
					COSName cosName = (COSName) iterator.next();
					String obj = cosName.getName();			
					System.out.println("page"+i+":"+obj);
					
			}			
		 }else {
			 System.out.println("No XObjects");
		 }
		}
		document.close();	
		is.close();
	 }
}
