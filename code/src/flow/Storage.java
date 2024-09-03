package flow;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

public class Storage extends TableDatas{

	Storage() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	Vector<Vector<String>> storage( ) {
		Vector<Integer> checkedMapKey = new Vector<Integer>();
		Iterator<Entry<Integer, TreeMap<Integer, String>>> iterator = checkedMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, TreeMap<Integer, String>> entry = iterator.next();
			Integer idx = entry.getKey();
			checkedMapKey.addElement(idx);
		}
		Check check = new Check();
		Vector<Vector<String>> table = new Vector<Vector<String>>();		
		for ( int k : checkedMapKey ) {
			TreeMap<Integer, String> rowMap = checkedMap.get( k );
			
			Vector<Integer> rowMapKey = new Vector<Integer>();
			Iterator<Entry<Integer, String>> iterator1 = rowMap.entrySet().iterator();
			while (iterator1.hasNext()) {
				Entry<Integer, String> entry = iterator1.next();
				Integer idx = entry.getKey();
				rowMapKey.addElement( idx );
			}			
			// System.out.println( rowMap );
			
			Vector<String> row = new Vector<String>();
			for (Integer r : rowMapKey) {				
				String cellString = check.full2Half( rowMap.get(r) );
				row.add( cellString );
			}
			table.add(row);
		}		
		return table;
	}

}
