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
	  * �������ٴ����ִ��㷨��������ͣ�ô�
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Vector<List<Word>> getSegSentences(Vector<String> allSentences)
	{
		// �������󣬱�������йؼ���ִʺ�Ľ��	
		Vector<List<Word>> segSentences = new Vector<List<Word>>();
		// ȫ�ļ����ľ������� i���ӵ�һ�䣨�±���0����ʼ�ִ�
		for(int i=0; i<allSentences.size(); i++) {
			// ��ȡ��i�仰
			String txt = allSentences.get(i);
			// �������ٷִ��㷨��������ͣ�ô�
			List<Word> segment = getSegWords(txt);
			System.out.println(segment);
			segSentences.add(segment);	
		}
		return segSentences;
	}
	
	
	public List<Word> getSegWords(String args)
	{
		//�����û��Զ���ʵ�
		//userDic();
		//���ٷִ��㷨
		List<Word> result = WordSegmenter.seg(args, SegmentationAlgorithm.MinimalWordCount);
		result = WordRefiner.refine(result);
		return result;
	}

	public List<Word> getSegWords(String args, SegmentationAlgorithm segAlgm)
	{
		//�����û��Զ���ʵ�
		userDic();
		//����ִ��㷨segAlgm
		List<Word> result = WordSegmenter.segWithStopWords(args, segAlgm);
		result = WordRefiner.refine(result);
		return result;
	}
	
	private void userDic() 
	{
		//�Զ���ʵ�·��
		WordConfTools.set("dic.path", "classpath:userdic.txt,e:\\lib");
		DictionaryFactory.reload(); // �Զ���ʵ���װ
	}
	
	
}
