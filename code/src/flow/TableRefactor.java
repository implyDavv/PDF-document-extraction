package flow;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.compress.archivers.zip.X0014_X509Certificates;

import com.hp.hpl.sparta.Document.Index;

import java.util.TreeMap;

public class TableRefactor extends TableDatas {
	
	TableRefactor() throws IOException {
		super();
	}
	
	void getTable(TreeMap<Float, TreeMap<Integer, String>> checkedMap ) {
		Vector<Float> checkedMapKey = new Vector<Float>();
		Iterator<Entry<Float, TreeMap<Integer, String>>> iterator = checkedMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Float, TreeMap<Integer, String>> entry = iterator.next();
			float idx = entry.getKey();
			checkedMapKey.addElement(idx);
		}
		float y = checkedMapKey.get(0);
		int colMax = checkedMap.get(y).size(); // 首行数据的列数 col
		// 获得 colMax 列最多的 y 值
		float colMaxY = y;
		for ( int k = 1; k < checkedMapKey.size(); k++ ) { // 第 y 行
			y = checkedMapKey.get(k);
			int col = checkedMap.get(y).size();
			if ( col > colMax ) {
				colMax = col;
				colMaxY = y;
				continue;
			} else {
				continue;
			}
		}
	}

	Map<Integer, String> refreshTableLine( Map<Integer, String> tableLineMap ) {
		Vector<Integer> tableLineMapKey = new Vector<Integer>();
		Iterator<Entry<Integer, String>> iterator = tableLineMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, String> entry = iterator.next();
			int idx = entry.getKey();
			tableLineMapKey.addElement(idx);
		}
		String line = "";
		Map<Integer, String> reTableLineMap = new LinkedHashMap<Integer, String>();
		for ( int l = 0; l < tableLineMapKey.size(); l++ ) {
			int index1 = tableLineMapKey.get(l);
			String line1 = lineMap.get(index1); 
			float x11 = wordPositionMap.get(index1).getX();
			float x12 = wordPositionMap.get( index1+line1.length()-1 ).getX();
			try {
				int index2 = tableLineMapKey.get(l+1);
				String line2 = lineMap.get(index2); 
				float x21 = wordPositionMap.get(index2).getX();
				float x22 = wordPositionMap.get( index2+line2.length()-1 ).getX();
			
				if ( (Math.abs(x21-x11) - Math.abs(x22-x12) < 3 )  &&  Math.abs(x21-x12) < 100
						&& ( (x11 > x21 &&  x12 < x22)  ||  ( x11 < x21 &&  x12 > x22 ) ) ) {
					line = line1 + line2;
					reTableLineMap.put( index1, line );
					l++;
				} else {
					reTableLineMap.put( index1, line1 );
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				reTableLineMap.put( index1, line1 );
			}			
		}		
		return reTableLineMap;		
	}
  	

}
