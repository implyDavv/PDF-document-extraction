package pdfHandle;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class ContStream {
	
	public static void main (String[] args) throws Exception { 
		       
		        File resource = new File("f:\\java\\table\\（2003）ZA27挤压铸造工艺参数的优化.pdf");		 
		        PDDocument document = PDDocument.load(resource);
		        // pageNum 第几页
		        int pageNum = 0;
		        try {
		            document = PDDocument.load(resource);
		            List<?> allPages = document.getDocumentCatalog().getAllPages();		           
		            PDPage page = (PDPage) allPages.get(pageNum);
		            PDStream contents = page.getContents();
		            if ( contents != null ) {
		            	String s=contents.getInputStreamAsString();
		                System.out.println(s);
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
	
	 }
}
