package flow;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apdplat.word.segmentation.Word;

public class KeySentences {
	
	Vector<List<Word>>  sentences ( String text )	{	
		Vector<List<Word>> keySentencesVector = new Vector<List<Word>>();
		// allkeySentences
		Vector<String> sentencesVector = getSentences( text );	
		System.out.print( "sentencesVector==="+ sentencesVector );
		// allkeySentences segment
		Vector<List<Word>> sentencesWordVector = segSentence( sentencesVector );
		// filter allkeySentences
		keySentencesVector = getKeySentences( sentencesWordVector ) ;		
		// noun 标准化
		return keySentencesVector;
	}


	/**	 
	 * @param allText
	 * @return Vector<String> 
	 * 
	 */
	private Vector<String> getSentences ( String text ){	
		// 保存所有关键句
		Vector<String> sentencesVector = new Vector<String>(); 
		String punctuation = "[^，,。：:；;()（）<>《》]*" ;
		String unit = "([0-9]+\\.?[0-9]* *)"
				+ "(?:MPa|kN|℃|s|min|%"
				+ "|mm/s|mm·s-1|mm/min|mm·min-1|m/s|m·s-1|m/min|m·min-1"
				+ "|[A-Z]{1}[a-z]{1}|%"
				+ "|HB)";
		String regex = punctuation+unit+punctuation;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		while( m.find() )
		{
			String sentence = m.group().replaceAll("\\s+|\t|\r|\n", "");
			sentencesVector.add( sentence );
		}		
		return sentencesVector;
	}
	
	
	/**
	 * segSentence 所有关键句分词
	 * @param allSentencesVector
	 * @return Vector<List<Word>>
	 * 
	 */
	private Vector<List<Word>>  segSentence ( Vector<String> sentencesVector )
	{
		Vector< List<Word> > sentencesWordVector = new Vector<List<Word>>( sentencesVector.size() );
		// 全文所有带单位的句子进行分词，保存分词结果
		SegWords sw = new SegWords();
		for( int i=0; i< sentencesVector.size(); i++ ) { // allSentences 全文句子数量 i		
			String s = sentencesVector.get( i );
			List<Word> segWord = sw.getSegWords( s );
			//System.out.print( "segSen==="+ segWord );
			sentencesWordVector.add( segWord );
		}
		//System.out.print( "sentencesWordVector~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+ sentencesWordVector );
		return sentencesWordVector;
	}
	
	
	/**
	 * 本体查询，获得 KeySentences  noun+数 | 数+noun
	 * @param sentencesWordVector
	 * @return Vector<List<Word>>
	 * 
	 */
	private Vector<List<Word>> getKeySentences ( Vector< List<Word> > sentencesWordVector ) 
	{		
		// “noun+数” （不一定有单位）
		Vector< List<Word> > keySentencesVector = new Vector<List<Word>>();

		findKeySentences:
		for( int i=0; i<sentencesWordVector.size(); i++ )	{
			List<Word> segWord = sentencesWordVector.get(i);			
			// 定义 word 变量j：第i句话中的第j个分词			
			for( int j=0; j<segWord.size(); j++ ) {
				// 获得第j个词
				String word = segWord.get(j).toString();
				// 第j个word能否在本体中找到对应的类	
				OntologyQuery onto = new OntologyQuery() ;
				boolean bool = onto.queryClass( word );
				if( bool ) {	
					// 第j个 word 能在本体中找到对应的 Class
					// System.out.print( "word === "+j+" ::  "+word );					
					Check check = new Check();
					try {   
						// check ~ j-1
						word = segWord.get(j-1).toString();											
						if( check.checkData(word) ) {   
							keySentencesVector.add( segWord );
							continue findKeySentences; 
						}
						else {
							// check ~ j+1
							word = segWord.get(j+1).toString();
							if( check.checkData(word) ) {   
								// 第j+1位满足格式，存储该句话
								keySentencesVector.add( segWord );
								continue findKeySentences; 
							}
							else {
								// check ~ j+2
								word = segWord.get(j+2).toString();	
								// check Format：“数+单位 | 数”
								if( check.checkData(word) ) { 
									keySentencesVector.add( segWord );
									continue findKeySentences; 
								}
								else {
									continue findKeySentences;
								}
							}
						}
					} catch ( IndexOutOfBoundsException e ) { // j-1 溢出
						try {
							// check ~ j+1
							word = segWord.get(j+1).toString(); 
							// check Format：“数+单位 | 数”						
							if( check.checkData(word) ) {  // 第j+1位满足格式，存储该句话
								keySentencesVector.add( segWord );
								continue findKeySentences; 
							}		
							else {  // 第j+1位不满足格式，check第j+2位  （多个值） "选取、设定"		
								// check ~ j+2
								word = segWord.get(j+2).toString();	
								// 第j+2位没有溢出，check Format：“数+单位 | 数”
								if( check.checkData(word) ) {   // 第j+2位满足格式，存储该句
									keySentencesVector.add( segWord );
									continue findKeySentences; 
								}
								else  {	
									/*// j+3
									word = segWord.get(j+3).toString();	
									if( check.checkData(word) ) {
										keySentencesVector.add( segWord );
										continue findKeySentences; 
									}	
									else {
										// 找下一句话 i+1*/
										continue findKeySentences; 
									//}
								}
							}															
						}
						catch ( IndexOutOfBoundsException ie ) 
						{	
							continue findKeySentences;						
						}
					}
				}				
			}
		}		
		return keySentencesVector;
	}

	
}
