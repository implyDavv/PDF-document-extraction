package flow;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

public class TableTitle extends TableDatas {
	
	
	TableTitle() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * getTableNumber
	 * @return tableNumber
     * @serialData  tableTitleMapKey   
	 */
 	int getTableNumber ( ) { 		
 		Vector<String> titleValue = new Vector<String>();			
    	Iterator<Entry<Integer, String>>  iterator = tableTitleMap.entrySet().iterator();    	
    	while (iterator.hasNext()) {
    		Entry<Integer, String> entry = iterator.next();
    		int idx = entry.getKey(); 
    		tableTitleMapKey.addElement(idx);
    		titleValue.add(entry.getValue()); 
    	}
    	
    	int chinese = 0;
    	int english = 0;
    	for (int t=0; t<titleValue.size(); t++)  {
    		String s = titleValue.elementAt(t);
    		if ( s.startsWith("表") )  {
    			chinese++;
    		} else {
    			english++;
    		}
    	}
    	
    	int tableNumber = 0;
		if ( english == 0 && chinese != 0 )  { 
    		// 只有中文标题
    		tableNumber = chinese;
    	} else if ( chinese == 0 && english != 0) {
			// 只有英文标题
    		tableNumber = english;
		} else { // 中英文都有
    		if ( chinese == english ) {
    			// 中、英文标题数量相等
    			tableNumber = chinese;    			
    			int index ;
    			String title = "" ;
    			Map< Integer, String > indexMap = new LinkedHashMap< Integer, String >();				
				// 有英文的标题，则每个表格都会有英文译文，英文标题后是表格内容
				for ( int t=0; t<tableTitleMap.size(); t++)	{
					index = tableTitleMapKey.get(t);
					title = tableTitleMap.get( index );							
					if ( title.startsWith("表") ) {
						continue;
					}
					else { //if ( title.startsWith("T") )
						indexMap.put(index, title);								
						continue;
					}
				}
				// 更新 tableTitleMap
				// 只保留英文标题
    			tableTitleMap.clear();
    			tableTitleMap = indexMap;
    			// 更新 tableTitleMapKey
    			tableTitleMapKey.clear();
    			iterator = tableTitleMap.entrySet().iterator();
    			while (iterator.hasNext()) {
    	    		Entry<Integer, String> entry = iterator.next();
    	    		int idx = entry.getKey(); 
    	    		tableTitleMapKey.addElement(idx);
    	    	}    			
    		} else if ( chinese > english ) { // 英文标题没找全
    			// System.out.println(tableTitleMap);    			
    			tableNumber = chinese;
    			int index ;
    			String title = "" ;
    			Map< Integer, String > indexMap = new LinkedHashMap< Integer, String >();
    			// 保留英文标题，和没有对应英文的中文标题（往后找Line获得其英文标题）			
				for ( int t=0; t<tableTitleMap.size(); t++)	{
					index = tableTitleMapKey.get( t );
					title = tableTitleMap.get( index );
					if ( title.startsWith("表") ) {
						try {
							index = tableTitleMapKey.get( t+1 );
							title = tableTitleMap.get( index );
							if ( title.startsWith("T") ) {
								indexMap.put( index, title );	
								continue;
							} else {
								index = lineMapKey.indexOf( tableTitleMapKey.get( t ) );
								int count = 1;
								do {
									title = lineMap.get( lineMapKey.get(index)+count );
									count++;
								} while ( title.startsWith("T") );
								indexMap.put( lineMapKey.get(index)+count, title );	
								continue;
							}
						} catch ( ArrayIndexOutOfBoundsException ae ) {
							index = lineMapKey.indexOf( tableTitleMapKey.get( t ) );								
							title = lineMap.get( lineMapKey.get(index) );
							indexMap.put(lineMapKey.get(index), title);	
				    	} catch ( NullPointerException ne ) {
							index = lineMapKey.indexOf( tableTitleMapKey.get( t ) );								
							title = lineMap.get( lineMapKey.get(index) );
							indexMap.put(lineMapKey.get(index), title);
				    	}						
					} else {
						indexMap.put(index, title);							
						continue;
					}
				}
				// 更新 tableTitleMap				
    			tableTitleMap.clear();
    			tableTitleMap = indexMap;
    			// 更新 tableTitleMapKey
    			tableTitleMapKey.clear();
    			iterator = tableTitleMap.entrySet().iterator();
    			while (iterator.hasNext()) {
    	    		Entry<Integer, String> entry = iterator.next();
    	    		int idx = entry.getKey(); 
    	    		tableTitleMapKey.addElement(idx);
    	    	}
    		} else if ( chinese < english ) { // 中文标题被误删（由于标点符号）
    			tableNumber = english;
    			int index ;
    			String title = "" ;
    			Map< Integer, String > indexMap = new LinkedHashMap< Integer, String >();
    			// 保留英文标题
				for ( int t=0; t<tableTitleMap.size(); t++)	{
					index = tableTitleMapKey.get(t);
					title = tableTitleMap.get( index );							
					if ( !title.startsWith("表") ) {
						indexMap.put(index, title);	
						continue;
					} else { // if ( title.startsWith("T") )																	
						continue;
					}
				}
				// 更新 tableTitleMap				
    			tableTitleMap.clear();
    			tableTitleMap = indexMap;
    			// 更新 tableTitleMapKey
    			tableTitleMapKey.clear();
    			iterator = tableTitleMap.entrySet().iterator();
    			while (iterator.hasNext()) {
    	    		Entry<Integer, String> entry = iterator.next();
    	    		int idx = entry.getKey(); 
    	    		tableTitleMapKey.addElement(idx);
    	    	}   
    		}
    	}    	
		return tableNumber ;
    } 	

	
	/**
	 * getTableTitleMap
	 *  @return  tableTitleMap   
	 */
	Map< Integer, String >  getTableTitleMap() {
		tableTitleMap.clear();
		tableTitleMapKey.clear();
		for ( int l : lineMapKey ) {
    		String line = lineMap.get( l );
    		// 正则表达式 定位表格标题
    		if ( findTableTitle( line ) ) {    			
    			tableTitleMap.put( l, line );
    		}	
    	}
		//System.out.println("tableTitleMap::" + tableTitleMap);
		return tableTitleMap;
	}
	
	/**
	 * findTableTitle
	 *  @return  boolean
	 *  @param  line  
	 */
	private boolean findTableTitle ( String lineString ) {
		Check check = new Check();
		lineString = check.full2Half( lineString );
		// System.out.println( "lineString：" + lineString );	
		// Table Title	
	    String regex1 = "(^表[0-9]+ {0,}(.*))" ;
	    Pattern p1 = Pattern.compile( regex1 );
	    Matcher m1 = p1.matcher( lineString );
	    String regex2 = "(^Tab(le|.)? {0,}[0-9]+ {0,}(.*))"; 
	    Pattern p2 = Pattern.compile( regex2,Pattern.CASE_INSENSITIVE );
	    Matcher m2 = p2.matcher( lineString );
	    try {
	    	while ( m1.find() ) {	// filter 存在标点，文章中的句子，不是标题
	    		if ( m1.toString().contains("、") || m1.toString().contains("，") 
	    				|| m1.toString().contains("。") || m1.toString().contains("；") 
	    				 || m1.toString().contains("热处理") ) {   // m2 是英文，理论上该行不存在中文表达
		   			return false;
		   		} else {
	    			return queryOnto( m1.toString() );
	    		}
		   	}	
	    	while ( m2.find() ) {
	    		if ( m1.toString().contains("heattreatment") ) {
	    			return false;
	    		} else {
	    			return queryOnto( m2.toString().toLowerCase() );
	    		}	    		
		   	}
	    } catch ( NullPointerException npe ) {
	    	return false;
	    }  
	   	return false;
	}
	
	/**
	 * @return boolean
	 * @param title
	  * 表题语义查询
	 */
	private boolean queryOnto ( String title ) {
		boolean bool = false;		
		// Create ontology model
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM); 
		// Ontology file path
		String filePath = "E:\\protege\\TableTitleOntology.owl";
		// Read the file under the path and load the model
		ontModel.read( filePath ); 
		// Iteratively display the classes in the model, 
		// completing various operations during the iteration
		for(Iterator<?> i = ontModel.listClasses(); i.hasNext(); ) {
			// Return type casts
			OntClass c = (OntClass) i.next();  
			// Test if c is anonymous, not anonymous, and print the class name
			if ( !c.isAnon() ) {
				String noun = c.getLocalName().replaceAll("_", " ");
				while( title.contains( noun ) ) {
					bool = true;
					break;
					}
				}
		}
		return bool;
	}	
	
	
}
