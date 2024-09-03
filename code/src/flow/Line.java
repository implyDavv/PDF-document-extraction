package flow;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.reasoner.rulesys.builtins.LE;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import java.util.Map.Entry;

public class Line extends GetTextPosition {	
	Line() throws IOException {
		super();
	}
	
	static  Map< Integer ,  String >  lineMap = new LinkedHashMap< Integer ,  String >();
	static  Vector<Integer> lineMapKey = new Vector<Integer>();
	
	/**  
     *  make 文本行 
     * 	@param  document , pageNum 
     **/
	void  makeLine () throws IOException { 
		lineMap.clear();
		lineMapKey.clear();
		int key = 0;
		String line = wordPositionMap.get( 0 ).getCharacter();		
		for (int i = 0; i < wordPositionMap.size()-1; i++) {
			float x1 = wordPositionMap.get( i ).getX();
			float x2 = wordPositionMap.get( i+1 ).getX();
			float y1 = wordPositionMap.get( i ).getY();
			int y2 = (int) wordPositionMap.get( i+1 ).getY();
			if ( x2-x1 > 0 && Math.abs(y1-y2) < 3.5 ) {
				line += wordPositionMap.get( i+1 ).getCharacter();
				continue;
			} else {
				lineMap.put( key, line );
				key = i+1;
				line = wordPositionMap.get( i+1 ).getCharacter();
				continue;
			}
		}
		getLineMapKey( lineMap );
		//System.out.println( "lineMap：" + lineMap );			
	}
	
	
	/**
	 *  @return  lineMapKey 
	 *  
	 */
	private Vector<Integer>  getLineMapKey( Map< Integer , String > lineMap ) { 		
	 	Iterator<Entry<Integer, String>> iterator = lineMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, String> entry = iterator.next();
			int idx = entry.getKey();    				
			lineMapKey.addElement(idx);
		}		
		return lineMapKey;	
	} 
	
	
	/**  
     *  make 文本行 
     * 	@param  document , pageNum 
     **/
	void  makeLine2 ( PDDocument document , int pageNum ) throws IOException { 
		// 创建PDF文本剥离器
		PDFTextStripper stripper = new PDFTextStripper(); 
		// 设置文本不按照本来的位置排序（会有分栏）
		stripper.setSortByPosition(false);
		// 使用剥离器从PDF文档中剥离文本信息	
		stripper.setStartPage( pageNum+1 );
		stripper.setEndPage( pageNum+1 );
		stripper.setLineSeparator("\n");
		String textRaw = stripper.getText(document) ;		
		int key = 0;
		int count = 0;
		lineMap.clear();
		lineMapKey.clear();
		String[] lineRaw = textRaw.split(stripper.getLineSeparator());
		for( int i=0; i < lineRaw.length; i++ ) {
			String line = lineRaw[i].replaceAll("/S", "").replaceAll("/s", "").replaceAll(" ", "").replaceAll("　", "");
			String a = line.substring(0,1); 
			System.out.println( a );
			System.out.println( line.length() );
			String b = "";
			try {
				if ( key==0 ) {
					b = wordPositionMap.get( key ).getCharacter();
				} else {		
					b = wordPositionMap.get( key-1 ).getCharacter();
				}					
			} catch (NullPointerException ne) {
				System.out.println( a + "and" + b );
				continue;
			}
			System.out.println( b );
			if ( !a.equals(b) ) {
				for (int j = 0; j < 20; j++) {
					key += 1;
					try {
						b = wordPositionMap.get( key ).getCharacter();
					} catch (NullPointerException ne) {
						continue;
					}
					if ( a.equals(b) ) {
						break;
					} else {
						continue;
					}
				}
				if ( !a.equals(b) ) {
					key -= 20;
					for (int j = 0; j < 20; j++) {
						key -= 1;
						try {
							b = wordPositionMap.get( key ).getCharacter();
						} catch (NullPointerException ne) {
							continue;
						}						
						if ( a.equals(b) ) {
							break;
						} else {
							continue;
						}
					}
				}
			}
			System.out.println( key + "：" + line );			
			lineMap.put( key, line );
			
			for (int j = 0; j <= i; j++) {		
				key = count + lineRaw[j].replaceAll("/s/S", "").replaceAll(" 　", "").length();				
			}
			count = key;
		}
		getLineMapKey( lineMap );
		//  System.out.println( "lineMap：" + lineMap );
    }

	
	
	
	void  makeLine1 ( PDDocument document , int pageNum ) throws IOException { 
		// 创建PDF文本剥离器
		PDFTextStripper stripper = new PDFTextStripper(); 
		// 设置文本不按照本来的位置排序（会有分栏）
		stripper.setSortByPosition(false);
		// 使用剥离器从PDF文档中剥离文本信息	
		stripper.setStartPage( pageNum+1 );
		stripper.setEndPage( pageNum+1 );
		stripper.setLineSeparator("\n");
		Check check = new Check();
		String textRaw = check.full2Half( stripper.getText(document) );		
		String textDel = textRaw.replaceAll("\n\r\f\t", "").replaceAll(" ", "");	
		
		int from = 0;
		lineMap.clear();
		lineMapKey.clear();
		
		for( String lineRaw : textRaw.split(  stripper.getLineSeparator() ) ) {
			lineRaw = check.full2Half( lineRaw );
			String lineDel = lineRaw.replaceAll("/s/S", "").replaceAll(" ", "");
			System.out.println( lineDel );
			int key = textDel.indexOf( lineDel,from );
			String a = lineDel.substring(0,1); 
			String b = "";
			try {
				b = wordPositionMap.get( key ).getCharacter();
			} catch (NullPointerException ne) {
				continue;
			}			
			boolean bool = a.equals(b);
			if ( !bool ) {
				for (int i = 0; i < 20; i++) {
					key += 1;
					try {
						b = wordPositionMap.get( key ).getCharacter();
					} catch (NullPointerException ne) {
						continue;
					}
					if ( bool ) {
						break;
					} else {
						bool = false;
						continue;
					}
				}
				if ( !bool ) {
					key = textDel.indexOf( lineDel,from );
					for (int i = 0; i < 20; i++) {
						key -= 1;
						try {
							b = wordPositionMap.get( key ).getCharacter();
						} catch (NullPointerException ne) {
							continue;
						}						
						if ( bool ) {
							break;
						} else {
							bool = false;
							continue;
						}
					}
				}
			}			
			System.out.println( key + "：" + lineRaw );
			
			lineMap.put( key, lineRaw );
			from = key;	
		}
		getLineMapKey( lineMap );
		//  System.out.println( "lineMap：" + lineMap );
    }


    
	/**
	 *  @return  lineMap 
	 *  
	 */
    private Map< Integer , String > getLineMap( ) {    	
		// get first word 
	    TextPosition word1 = wordPositionMap.get(0);
	    String line = (String) word1.getCharacter();	
	   	TextPosition word2 ;
		for ( int i = 0; i < wordPositionMap.size(); i++ ) {
		    word1 = wordPositionMap.get(i);
		    word2 = wordPositionMap.get(i+1);
		    float x ;
		   	float y ;
		   	try {  	// Calculate the difference of X 、Y between the i and i+1 word
		    	x = (float) word2.getX() - (float) (word1.getX());
		   		y = Math.abs( (float) word2.getY() - (float) (word1.getY()) );
		   	} catch ( NullPointerException ne ) { 
		   		lineMap.put( i,line );
		   		return lineMap ; 
		   	}		    		
		    // 同一行 character 的 Y 坐标一致，（下标与正文相差 1.4969， 上标与正文相差 3-5（引文））
		    // 正常的换行之间的 Y 值相差 10 以上？ X 减小
		   	// x 增大（中英文和符号之间的连接有些许误差），y 基本一致
			if ( x>=-0.9 && y<=5.5 ) {
			  	line += (String) word2.getCharacter();	
			   	continue ;
			} else 	{
			  	lineMap.put( i,line );		    			
			   	line = (String) word2.getCharacter();			
			  	continue ;
			}
		}
		return lineMap;
    }
}
