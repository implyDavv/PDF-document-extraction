package flow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

import javax.security.sasl.AuthenticationException;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import com.hp.hpl.sparta.Document.Index;

import weka.filters.unsupervised.attribute.FirstOrder;

public class ArticleInformation extends Line {

	ArticleInformation( PDDocument document) throws IOException {
		super();
		preprocess ( document );		
	}
	
	void preprocess ( PDDocument document ) throws IOException 
	{	
		// 获得第一页的  WordPositionMap
		PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get( 0 );	
		GetTextPosition pos = new GetTextPosition( page );
		// 获得第一页的 text
		Preprocess line = new Preprocess(document);
	}
	
	private static int indexAuthor ;
	
	String getArticleTitle ( ) {
		String title = null;
		for ( int i=0; i<lineMapKey.size(); i++ )
		{	
			TextPosition character1;
			TextPosition character2;
			try {
				character1 = wordPositionMap.get( lineMapKey.get(i)+1 );
				character2 = wordPositionMap.get( lineMapKey.get(i+1)+1 );
				if ( character1.getFontSizeInPt() <= character2.getFontSizeInPt() ) {					
					continue;
				}
				else
				{
					title = lineMap.get( lineMapKey.get(i) );
					try {
						character1 = wordPositionMap.get( lineMapKey.get(i-1)+1 );
						character2 = wordPositionMap.get( lineMapKey.get(i)+1 );
						if ( character1.getFontSizeInPt() == character2.getFontSizeInPt() ) {
							title = lineMap.get( lineMapKey.get(i-1) ) + title;
						}						
					} catch (ArrayIndexOutOfBoundsException ae) {
						return title;
					}
					indexAuthor = i;
					break;
				}		
			} catch (NullPointerException e) {
				return title;			
			}		
		}
		title.replaceAll("\\s+", "");
		return title;
	}
	
		
	String[] getArticleAuthors ( ) {		
		String authorLine = lineMap.get( lineMapKey.get(indexAuthor+1) );
		String[] authors = authorLine.split("，");
		if( authors.length==1 ) {
			String s = authors[0];
			if( s.length() != 2 || s.length() !=3 ) {
				authors = authorLine.split("\\s+");
			}
		}
		for(String a:authors) {
			System.out.println( "a::"+a );
		}		
		return authors;		
	}
	
	Vector<String> getArticleAffiliation ( ) {
		Vector<String> affiliations = null;
		String affiliationLine = lineMap.get( lineMapKey.get(indexAuthor+2) );
		
		return affiliations;		
	}
	
}
