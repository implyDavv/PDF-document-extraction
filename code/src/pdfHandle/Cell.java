package pdfHandle;


public class Cell {	
	/*
	// 该行的字符
	String line = lineMap.get(lineIndex);
	// 该行的长度
	int lth = line.length();
	// 该行的起始字符索引值
	int start = lineIndex-lth+1;
	// 该行的第一个字符信息
	Map<String, Object> wordInfos1 = PDWordInfosMap.get(start);
	// 该行的第一个字符 
	String cell = (String) wordInfos1.get(CHARACTER);
	
	Map<String, Object> wordInfos2 = new LinkedHashMap<String, Object>();
	
	for ( start = lineIndex-lth+1; start<=lineIndex; start++) 
	{
		try 
		{ // index 是否溢出
			wordInfos2 = PDWordInfosMap.get(start+1);
		} 
		catch ( IndexOutOfBoundsException e ) 
		{
			return ;  // 第start+1位溢出，结束for循环
		}
		
		wordInfos1 = PDWordInfosMap.get(start);				
		// 计算第 start 与  start+1  字符 的 X 坐标之差   
		float x = (float) wordInfos2.get(X) - (float) (wordInfos1.get(X));		    		
		// 同一个单元格 cell 的  （中字+中字  /  中符+中符）  X < space+0.3
		//   X  <  space+3.3      （中字+中符）
		//   X  <  space       （中符+英文/单位）
		//   X  <  space+3.5      （符号+英文）
		if ( x>=-0.9 && x< (float) wordInfos1.get(XSCALE)+3.5 ) 
		{
			cell += (String) wordInfos2.get(CHARACTER);
			if (start == lineIndex) 
			{
				cellMap.put( start,cell );
			}
			continue ;
		} 
		else 
		{
			cellMap.put( start,cell );	    			
			cell = (String) wordInfos2.get(CHARACTER);	
			continue ;
		}
		
	}*/
}
