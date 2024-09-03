package flow;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.segmentation.WordRefiner;
import org.apdplat.word.util.WordConfTools;

public class SegWords {

	/**
	  * 采用最少词数分词算法，不保留停用词
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Vector<List<Word>> getSegSentences(Vector<String> allSentences)
	{
		// 创建对象，保存对所有关键句分词后的结果	
		Vector<List<Word>> segSentences = new Vector<List<Word>>();
		// 全文检索的句子数量 i，从第一句（下标是0）开始分词
		for(int i=0; i<allSentences.size(); i++) {
			// 获取第i句话
			String txt = allSentences.get(i);
			// 采用最少分词算法，不保留停用词
			List<Word> segment = getSegWords(txt);
			System.out.println(segment);
			segSentences.add(segment);	
		}
		return segSentences;
	}
	
	
	public List<Word> getSegWords(String args)
	{
		//加载用户自定义词典
		//userDic();
		//最少分词算法
		List<Word> result = WordSegmenter.seg(args, SegmentationAlgorithm.MinimalWordCount);
		result = WordRefiner.refine(result);
		return result;
	}

	public List<Word> getSegWords(String args, SegmentationAlgorithm segAlgm)
	{
		//加载用户自定义词典
		userDic();
		//传入分词算法segAlgm
		List<Word> result = WordSegmenter.segWithStopWords(args, segAlgm);
		result = WordRefiner.refine(result);
		return result;
	}
	
	private void userDic() 
	{
		//自定义词典路径
		WordConfTools.set("dic.path", "classpath:userdic.txt,e:\\lib");
		DictionaryFactory.reload(); // 自定义词典重装
	}
	
	
}
