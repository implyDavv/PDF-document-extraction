package flow;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.language.DaitchMokotoffSoundex;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

public class TableDatas extends Line {

	TableDatas() throws IOException {
		super();		
	}
	
	
	protected static Map< Integer, String > tableTitleMap = new LinkedHashMap< Integer, String >(); 
	protected static Vector<Integer> tableTitleMapKey = new Vector<Integer>();
	protected static TreeMap<Integer, TreeMap<Integer, String>> checkedMap = new TreeMap<Integer, TreeMap<Integer, String>>();
	/**
	 * getTableDatas
	 * @return checkedMap
	 * @throws IOException
	 */
	void getTableDatas () throws IOException {		
		checkedMap.clear();
		TableTitle tableTitle = new TableTitle();
		tableTitle.getTableTitleMap();		
		// 获得表格数量，更新 tableTitleMap
		int tableNumber = tableTitle.getTableNumber();
		// 
		System.out.println( "tableNumber = " + tableNumber);
    	// System.out.println( "update:"+ tableTitleMap );
		// System.out.println( tableTitleMapKey );		
		
		int startIndex = 0;
		int no = 1; 		
		if ( tableNumber > 0 ) {
			for ( int t : tableTitleMapKey ) {
				//
				System.out.println( "tableNo."+ no );
				no++;
				//try	{ // 标题行
					startIndex = lineMapKey.get( lineMapKey.indexOf(t) );	
				//} catch ( ArrayIndexOutOfBoundsException ae ) {
					// 1 ???
					//startIndex = lineMapKey.get( 1 );
				//}					
				//String title = lineMap.get( startIndex );	

				// 定位、获得表格数据行
				Map<Integer,String> tableLineMap = new LinkedHashMap<Integer , String >();						
				for ( int l = lineMapKey.indexOf( startIndex ) +1;  l < lineMapKey.size();  l++ ) {
					String line = lineMap.get( lineMapKey.get(l) );			
					// 判断行 l 是否属于表格内容
					//System.out.println( "~~~~~~~"+ line );
					if ( lineContents( l , startIndex ) ) {			
						tableLineMap.put( lineMapKey.get(l), line );
			    		continue ;
			    	} else if ( l == lineMapKey.indexOf( startIndex ) +1 ) {
			    		// 处理标题换行的情况，第1次找到的数据行  参数与标题相等，返回 false
			    		if ( lineContents( l+1 , startIndex ) ) {			
							tableLineMap.put( lineMapKey.get(l+1), line );							
				    		continue ;
				    	}			    		
					} else {
			    		break;
					}
				}
				// 
				System.out.println( "tableLineMap:"+tableLineMap );
				// 合并换行文本
				TableRefactor tab = new TableRefactor();
				Map<Integer, String> reTableLineMap = new LinkedHashMap<Integer, String>();
				reTableLineMap = tab.refreshTableLine( tableLineMap );
				System.out.println( "reTableLineMap:" + reTableLineMap );
				// 以 空格 分割表格文本行，获得单元格数据 cellMap 并按照 y 坐标聚类 cellY
				TreeMap<Integer, TreeMap<Integer, String>> cellY = getCellMap( reTableLineMap );
				// 
				System.out.println( "cellY:" + cellY );
				
				// 计算 x y 坐标，过滤无关 数据行
				checkedMap = checkCell( cellY, startIndex );
				//  System.out.println( "checkedMap1:"+ checkedMap );
				if ( checkedMap == null ) {
					refreshTableDatas ( startIndex );
				    cellY = getCellMap( reTableLineMap );
				    checkedMap = checkCell( cellY, startIndex );
				}
				//
				System.out.println( "checkedMap2:" + checkedMap );
				
			    /*// 表格重构
				tab.getTable( checkedMap );*/
				
				Storage result = new Storage();
				// 存入数组，以二维数组的形式存表格数据
				Vector<Vector<String>> table = new Vector<Vector<String>>();
				table = result.storage( );
				System.out.println( "table:" + table );
				System.out.println( "**************************************************************" );
			}
		} else {
			//System.out.println( "no table" );
			return ;
		}
		return ;
	}


	
	
	/**
	 * checkCell
	 * @param cellMapY
	 * @param startIndex 表题字符索引
	 * @return  checkedMap 
	 */
	private TreeMap<Integer, TreeMap<Integer, String>>  checkCell ( TreeMap<Integer, TreeMap<Integer, String>> cellY, int startIndex ) {
		// 获得标题的 y ，第一行数据和 标题的 y 差别太大，和其他的行 y 也不是正常的行距，不应属于表格内容
		// 算行首字 x y 坐标与表题 x y 坐标的距离
		TextPosition title = wordPositionMap.get( startIndex ) ;
		//System.out.println( "title:"+ title );
		float titleX = title.getX();
		float titleY = title.getY();
		Vector<Integer> cellYKey = new Vector<Integer>();
		Iterator<Entry<Integer, TreeMap<Integer, String>>> iterator = cellY.entrySet().iterator();
		while ( iterator.hasNext() ) {
			Entry<Integer, TreeMap<Integer, String>> entry = iterator.next();
			int key = entry.getKey();
			cellYKey.addElement(key);
		}
		
		TreeMap<Integer, TreeMap<Integer, String>> checkedMap = new TreeMap<Integer, TreeMap<Integer, String>>();
		int no = 1 ;
		for ( int k = 0; k < cellYKey.size(); k++ ) {
			int y = cellYKey.get(k);
			TreeMap<Integer, String> cellX = cellY.get(y);
			int index = cellX.firstKey();
			float x = wordPositionMap.get( index ).getX();
			double dis = Math.sqrt( Math.pow( Math.abs(x-titleX), 2 ) + Math.pow( Math.abs( y-titleY), 2 )  );
			float disX = Math.abs( x-titleX );
			float disY = y-titleY ;
			// 数据行 - 标题行
			if ( ( ( disY <= 30*no && disY >= 0 ) || dis > 100 ) && disX < 100 ) {
				checkedMap.put( y, cellY.get( y ) );
				no++;
			} 
		}
		return checkedMap;
	}
	

	/**
	  * 根据 x 坐标排序单元格数据
	 * @param checkedMap
	 * @return  
	 */
	private TreeMap<Float, TreeMap<Float, String>> sortCellByPosX ( TreeMap<Float, TreeMap<Integer, String>> checkedMap ) {
		Vector<Float> checkedMapKey = new Vector<Float>( checkedMap.size() );
		Iterator<Entry<Float, TreeMap<Integer, String>>> iterator = checkedMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Float, TreeMap<Integer, String>> entry = iterator.next();
			Float idY = entry.getKey();
			checkedMapKey.addElement( idY );
		}
		
		TreeMap<Float, TreeMap<Float, String>> cellMap = new TreeMap<Float, TreeMap<Float, String>>();
		for (int y = 0; y < checkedMapKey.size(); y++) {
			Float posY = checkedMapKey.get(y);
			Map<Integer, String> lineCell = checkedMap.get( posY );
			Vector<Integer> lineCellKey = new Vector<Integer>( lineCell.size() );
			Iterator<Entry<Integer, String>> iterator1 = lineCell.entrySet().iterator();
			while ( iterator1.hasNext() ) {
				Entry<Integer, String> entry = iterator1.next();
				Integer idX = entry.getKey();
				lineCellKey.addElement( idX );
			}

			TreeMap<Float, String> mapX = new TreeMap<Float, String>();
			for (int x = 0; x < lineCellKey.size(); x++) {
				Integer wordIndex = lineCellKey.get( x );
				String cell = lineCell.get( wordIndex );
				float word = wordPositionMap.get( wordIndex ).getX();
				mapX.put( word, cell );
			}
			cellMap.put( posY, mapX );
		}
		return cellMap;
	}
	

	
	/**
	 * getCellMap cellMapKey
	 * @return sort mapY： TreeMap<Integer, TreeMap<Float, String>>
	 * @param tableDatasLineMap
	 */
	private TreeMap<Integer, TreeMap<Integer, String>> getCellMap ( Map<Integer, String> reTableLineMap ) {
		Vector<Integer> reTableLineMapKey = new Vector<Integer>();
		Iterator<Entry<Integer, String>> iterator = reTableLineMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, String> entry = iterator.next();
			int idx = entry.getKey();			
			reTableLineMapKey.addElement(idx);
		}
		
		// Integer 文本行首字符 y 坐标，TreeMap<Integer, String> 为文本行分割为单元格数据的结果 
		TreeMap<Integer, TreeMap<Integer, String>> cellY = new TreeMap<Integer, TreeMap<Integer, String>>();
		for (int k = 0; k < reTableLineMapKey.size(); k++) { // 表格数据行
			int index = reTableLineMapKey.get( k );
			String line = reTableLineMap.get( index );
			int y = Math.round( wordPositionMap.get( index ).getY() );
			
			String cell = line.substring( 0,1 );
			int key = index;
			TreeMap<Integer, String> cellMap = new TreeMap<Integer, String>();
			if ( line.length() > 1 ) {
				for (int i = 1; i < line.length(); i++) {
					float x1 = wordPositionMap.get( index+i-1 ).getX();
					float x2 = wordPositionMap.get( index+i ).getX();
					float y1 = wordPositionMap.get( index+i-1 ).getY();
					float y2 = wordPositionMap.get( index+i ).getY();
					if ( Math.abs(x2-x1)-wordPositionMap.get( index+i-1 ).getWidth() <= 2.5  
							|| Math.abs( y1-y2 ) > 3 ) {
						cell += line.substring(i, i+1);
						if ( i == line.length()-1 ) {
							if ( cellY.get(y) == null ) {
								cellMap .put( key, cell );
								cellY.put( y, cellMap );
							} else {
								TreeMap<Integer, String> value = cellY.get(y);
								value.put( key, cell );
								cellY.put( y, value );
							}
						} else {
							continue;
						}
					} else if ( cellY.get(y) == null ) {
						cellMap .put( key, cell );
						cellY.put( y, cellMap );
						cell = line.substring(i, i+1);
						key = index+i;
					} else {
						TreeMap<Integer, String> value = cellY.get(y);
						value.put( key, cell );
						cellY.put( y, value );
						cell = line.substring(i, i+1);
						key = index+i;
					}
				}			
			} else if ( cellY.get(y) == null ) {
				cellMap.put( key, line );
				cellY.put( y, cellMap );
			} else {
				TreeMap<Integer, String> value = cellY.get(y);
				value.put( key, cell );
				cellY.put( y, value );
			}
		}
		
		return cellY;
	}
	

	@SuppressWarnings("unused")
	private TreeMap<Float, TreeMap<Integer, String>> getCellMap1 ( Map<Integer, String> tableLineMap ) {
		Vector<Integer> tableLineMapKey = new Vector<Integer>();
		Iterator<Entry<Integer, String>> iterator = tableLineMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, String> entry = iterator.next();
			int idx = entry.getKey();
			tableLineMapKey.addElement(idx);
		}
		// Integer 为文本行首字符索引，TreeMap<Integer, String> 为文本行分割为单元格数据的结果 
		TreeMap<Float, TreeMap<Integer, String> > cellMapY = new TreeMap<Float, TreeMap<Integer, String> >();
		for (int k = 0; k < tableLineMapKey.size(); k++) { // 表格数据行	
			Integer index = tableLineMapKey.get( k );			
			String line = tableLineMap.get( index );
			TreeMap<Integer, String> cellLineMap = new TreeMap<Integer, String>(); 
			for ( String cell : line.split(" ") ) { // 以“空格”分割文本行
				cellLineMap.put(index, cell);
				index += cell.length();
			}
			cellMapY.put(  wordPositionMap.get( tableLineMapKey.get(k) ).getY() , cellLineMap );
		}
		return cellMapY;
	}

	/**
	 * @return 获得属于表格内容的文本行字符
	 * @param startIndex  标题行
	 */
	@SuppressWarnings("unused")
	private  Map<Integer,String>  getTableContentsMap ( int startIndex ) {
		Map<Integer,String> tableLineMap = new LinkedHashMap<Integer , String >();						
		for ( int l = lineMapKey.indexOf(startIndex)+1;  l < lineMapKey.size();  l++ ) {
			String line = lineMap.get( lineMapKey.get(l) );			
			// 判断行 l 是否属于表格内容
			//	System.out.println( "~~~~~~~"+lineMapKey.get(l) );
			if ( lineContents( l, startIndex ) ) {				
				tableLineMap.put( lineMapKey.get(l), line );
	    		continue ;
	    	} else {
	    		break;
			}
		}
		return tableLineMap;		
	}
	
	/**
	 * @return 重新查找属于表格内容的文本行字符
	 * @param startIndex  标题行
	 * @return 
	 */
	void refreshTableDatas ( int startIndex ) throws IOException {	
		try	{
			startIndex = lineMapKey.get( lineMapKey.indexOf( startIndex ) );	
		} catch ( ArrayIndexOutOfBoundsException ae ) {					
			startIndex = lineMapKey.get( 1 );
		}					
		Map<Integer, String> tableLineMap = new LinkedHashMap<Integer , String >();
		for ( int l1 = lineMapKey.indexOf(startIndex)-1;  l1 > 0;  l1-- ) {
			String line = lineMap.get( lineMapKey.get(l1) );
			if ( lineContents( l1,startIndex ) ) {				
				tableLineMap.put( lineMapKey.get(l1), line );
		   		continue ;
		   	} else {
				break;
			}
		}
	}
	
	
	/**
	  * 第l行是否属于表格内容
	 * @param int l
	 * @return boolean
	 */
	private boolean lineContents ( int l , int tableTitle ) {
		try {			
    		TextPosition lineFirstWord1 = wordPositionMap.get( tableTitle );  
    		/*System.out.println( "lineFirstWord1【"+ index + "：" + lineFirstWord1.getCharacter() + "】："
    					+ "FontSize:"+lineFirstWord1.getFontSizeInPt() +  "、"
    					+ "XScale:"+lineFirstWord1.getXScale() +  "、"
    					+ "Width:"+lineFirstWord1.getWidth() +  "、"
    					+ "Height:"+lineFirstWord1.getHeight() );*/
    		
    		// 第 l 行首字 wordPositionMap
    		int index = lineMapKey.get(l);
    		TextPosition lineFirstWord2 = wordPositionMap.get( index );
    		/*System.out.println( "lineFirstWord2【"+ index + "：" + lineFirstWord2.getCharacter() + "】:"
					+ "FontSize:"+lineFirstWord2.getFontSizeInPt() +  "、"
					+ "WidthOfSpace:"+lineFirstWord2.getWidthOfSpace() );*/
    		
    		// 表格内容结束，字号会明显改变（增大）
    		float fontSize = lineFirstWord2.getFontSizeInPt() - lineFirstWord1.getFontSizeInPt();
    		float ys = Math.abs( lineFirstWord1.getYScale() - lineFirstWord2.getYScale() );
    		//if( fontSize <= 1.0 && ys <= 1.0 ) {
    		if( fontSize < 1.0 ) {
    			if ( lineMap.get( index ).indexOf("。") != -1  ||  lineMap.get( index ).indexOf("，") != -1 
						|| lineMap.get( index ).toString().startsWith("图"))	 {
					return false;
				} else {
					Check check = new Check();
					String line = lineMap.get( index ).toString();
					check.full2Half( line );
					// Table Title	
				    String regex1 = "(^表[0-9]+(.*))" ;
				    Pattern p1 = Pattern.compile( regex1 );
				    Matcher m1 = p1.matcher( line );
				    String regex2 = "(^Tab(le)?[.]{0,}[0-9]+(.*))"; 
				    Pattern p2 = Pattern.compile( regex2,Pattern.CASE_INSENSITIVE );
				    Matcher m2 = p2.matcher( line );
					if( m1.find() || m2.find() ) {		
						return false;
					} else {
						return true;
					} 
				}
    		} else if ( fontSize == 0 ) { 
    			// 有的没有fontSize信息，全文是一样的值       			
    			//  X\YSCALE
    			if ( ys <= 1.0 ) { 
    				if ( lineMap.get( index ).indexOf("。") != -1  ||  lineMap.get( index ).indexOf("，") != -1 
    						|| lineMap.get( index ).toString().startsWith("图"))	 {
    					return false;
    				} else {
    					Check check = new Check();
    					String line = lineMap.get( index ).toString();
    					check.full2Half( line );
    					// Table Title	
    				    String regex1 = "(^表[0-9]+(.*))" ;
    				    Pattern p1 = Pattern.compile( regex1 );
    				    Matcher m1 = p1.matcher( line );
    				    String regex2 = "(^Tab(le)?[.]{0,}[0-9]+(.*))"; 
    				    Pattern p2 = Pattern.compile( regex2,Pattern.CASE_INSENSITIVE );
    				    Matcher m2 = p2.matcher( line );
    					if( m1.find() || m2.find() ) {		
    						return false;
    					} else {
    						return true;// 都没变，依然属于表格内容   
    					} 
					}			
    			} else {   
    				return false;
    			}
    		} else {
    			return false;
    		}
		} catch ( NullPointerException ne ) {
    		return false;
    	} catch ( ArrayIndexOutOfBoundsException e ) {
    		return false;
    	}
	}
	
	
	@SuppressWarnings("unused")
	private TreeMap<Float, TreeMap<Float, String>>  cellDatas ( TreeMap<Float, TreeMap<Float, String>> checkedMap ) {
		TreeMap<Float, TreeMap<Float, String>>  map = new TreeMap<Float, TreeMap<Float, String>>();

		Vector<Float> checkedMapKey = new Vector<Float>();
		Iterator<Entry<Float, TreeMap<Float, String>>> iterator = checkedMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Float, TreeMap<Float, String>> entry = iterator.next();
			Float idx = entry.getKey();
			checkedMapKey.addElement(idx);
		}

		// 整理每一行		
		for (Float t : checkedMapKey) {
			TreeMap<Float, String> lineY = checkedMap.get(t);
			// 存每行的X坐标	
			Vector<Float> posX = new Vector<Float>();
			Iterator<Entry<Float, String>> iterator1 = lineY.entrySet().iterator();
			while (iterator1.hasNext())	{
				Entry<Float, String> entry = iterator1.next();
				Float idx = entry.getKey();
				posX.addElement(idx);
			}
						
			float average = ( posX.get( posX.size()-1 ) - posX.get(0) ) / (posX.size()-1);
			String cell = lineY.get( posX.get(0) );
			int c = 0;
			TreeMap<Float, String> X = new TreeMap<Float, String>();
			for ( int r = 0; r < posX.size(); r++ ) {				
				try	{
					if ( ( posX.get(r+1) - posX.get(r) ) >= average-10 || ( posX.get(r+1) - posX.get(r) ) >= 24 ) {
						X.put( posX.get(r-c), cell );
						c = 0;
						cell = lineY.get( posX.get(r+1) );
					} else {
						cell += lineY.get( posX.get(r+1) );
						c++;
					}
				} catch ( IndexOutOfBoundsException ie ) {					
					X.put( posX.get(r-c), cell );
				}				
			}
			map.put(t, X);
		}
		return map;
	}
	@SuppressWarnings("unused")
	private Map<Integer, String> getContents(PDDocument document, Map<Integer, String> tableDatasMap)
			throws IOException {
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(false);
		String txt = stripper.getText(document);

		Vector<Integer> tableContentsMapKey = new Vector<Integer>(); // 表格内容 tableContentsMap 的 indexVector
		Iterator<Entry<Integer, String>> iterator = tableDatasMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, String> entry = iterator.next();
			int idx = entry.getKey();
			tableContentsMapKey.addElement(idx);
		}

		Map<Integer, String> tabLine = new LinkedHashMap<Integer, String>();
		for (int m = 0; m < tableDatasMap.size(); m++) {
			String str1 = tableDatasMap.get(tableContentsMapKey.get(m));
			Map<Float, String> tableLine = new LinkedHashMap<Float, String>();
			for (String line : txt.split(stripper.getLineSeparator())) {
				// 计算字符相似度（编辑距离）
				float similarity = levenshtein(str1, line);
				if (similarity >= 0.8 || (similarity >= 0.6 && difstep <= 11)) {
					tableLine.put(similarity, line);
					continue;
				} else {
					continue;
				}
			}
			// System.out.println( tableLine );
			if (tableLine != null) {
				Vector<Float> tableLineKey = new Vector<Float>(); // 表格内容 tableContentsMap 的 indexVector
				Iterator<Entry<Float, String>> iterator1 = tableLine.entrySet().iterator();
				while (iterator1.hasNext()) {
					Entry<Float, String> entry = iterator1.next();
					Float idx = entry.getKey();
					tableLineKey.addElement(idx);
				}

				// 获得相似度最大的 line
				if (tableLine.size() == 1) {
					tabLine.put(tableContentsMapKey.get(m), tableLine.get(tableLineKey.get(0)));
					// System.out.println( tableLine.get( tableLineKey.get(0) ) );
				} else {
					for (int m1 = 0; m1 < tableLine.size() - 1; m1++) {
						float a = tableLineKey.get(m1);
						float b = tableLineKey.get(m1 + 1);
						if (a > b) {
							tabLine.put(tableContentsMapKey.get(m), tableLine.get(a));

						} else {
							tabLine.put(tableContentsMapKey.get(m), tableLine.get(b));
							// System.out.println( tableLine.get( b ) );
						}
					}
				}

			}
		}
		return tabLine;
	}

	private static int difstep;

	static float levenshtein(String str1, String str2) {
		// 计算两个字符串的长度。
		int len1 = str1.length();
		int len2 = str2.length();
		// 建立数组，比字符长度大一个空间
		int[][] dif = new int[len1 + 1][len2 + 1];
		// 赋初值，步骤B。
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		// 计算两个字符是否一样，计算左上的值
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 取三个值中最小的
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
			}
		}
		// System.out.println("字符串\""+str1+"\"与\""+str2+"\"的比较");
		// 取数组右下角的值，同样不同位置代表不同字符串的比较
		difstep = dif[len1][len2];
		// System.out.println("差异步骤："+ dif[len1][len2] );
		// 计算相似度
		float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
		// System.out.println("相似度："+ similarity);
		return similarity;
	}

	// 得到最小值
	private static int min(int... is) {
		int min = Integer.MAX_VALUE;
		for (int i : is) {
			if (min > i) {
				min = i;
			}
		}
		return min;
	}


}
