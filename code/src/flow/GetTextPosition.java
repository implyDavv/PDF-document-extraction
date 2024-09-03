package flow;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;


public class GetTextPosition extends PDFTextStripper {
	
	static 	Map< Integer ,  TextPosition  >  wordPositionMap = new LinkedHashMap< Integer ,  TextPosition >();
	
	GetTextPosition() throws IOException {
        super.setSortByPosition( false );
    } 
    
	GetTextPosition( PDPage page ) throws IOException {
        super.setSortByPosition( false );
        getWordPositionMap ( page );
    } 
	
	private static final int BOLD_F_NUM = 2;
	private static final String[] BOLD_FLAGS = {"Bold", "CAJ FNT04"};
	private static final int ITALIC_F_NUM = 2;
	private static final String[] ITALIC_FLAGS = {"Italic", "CAJ FNT03"};	   
    
	// 是否加粗
	@SuppressWarnings("unused")
	private boolean IsBold(String font)	{		
		for ( int i = 0; i < BOLD_F_NUM; i++ ) {
			if ( font.contains(BOLD_FLAGS[i]) ) {
				return true;
			}
		}
		return false;
	}
   
	// 是否倾斜
	@SuppressWarnings("unused")
	private boolean IsItalic(String font) {
		for ( int i = 0; i < ITALIC_F_NUM; i++ ) {
			if ( font.contains(ITALIC_FLAGS[i]) ) {
				return true;
			}				
		}			
		return false;
	}   
   
   
    /**
     * A method provided as an event interface to allow a subclass to perform
     * some specific functionality when text needs to be processed.
     * 
     * @return PDWordInfosMap
     * @param PDtext The text to be processed.
     */
    @Override
    protected void processTextPosition( TextPosition text ) {
    	if( text != null ) {     		
    		//if ( !text.getCharacter().equals(" ") && !text.getCharacter().equals("　") &&  !text.getCharacter().equals("") ) {
    			//System.out.println( wordIndex + "==【" + text.getCharacter() + "】   X：" + text.getXDirAdj() + "  Y：" + text.getYDirAdj() +"  FontSizeInPt：" + text.getFontSizeInPt() 
    							//+ "  XS：" + text.getXScale() + "  YS：" + text.getYScale() );
    			wordPositionMap.put( wordIndex, text );
	    		wordIndex++;
    		//}
    	} 
    }    
    
	private int wordIndex = 0 ;
	
	
	/**
	 * @return  wordPositionMap
	 * @param  PDPage page 
	 * @throws IOException
	 */	
	void  getWordPositionMap ( PDPage page ) throws IOException {
		wordPositionMap.clear();
		wordIndex = 0 ;
		PDStream contents = page.getContents();
		GetTextPosition printer = new GetTextPosition();
	    // 处理页面上的字符信息，获得每个字符的坐标、字体
	    if( contents != null ) {   
	    	printer.processStream( page, page.findResources(), page.getContents().getStream() );
	    }
		 // System.out.println( "wordPositionMap~~~" + wordPositionMap );		
	}
	
}
