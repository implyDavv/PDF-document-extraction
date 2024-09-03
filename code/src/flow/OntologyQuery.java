package flow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

public class OntologyQuery
{		
	OntModel ontologyFile ( ) {
		// create the base model
		OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM ); 
		// ontologyModel file Path
		String filePath = "E:\\protege\\onto.owl";	
		ontModel.read(filePath);
		
		int depth=0;		
		for ( ExtendedIterator<OntClass> i = ontModel.listClasses(); i.hasNext(); )	{
			OntClass c = i.next(); 					
			if ( !c.isAnon() && c.getLocalName().equals("SqueezeCasting")) {
				getOntoClassNode( c, depth );
			}
		}
		// System.out.println( ontoClassNodeMap );
		return ontModel; 
	}

	
	private void getOntoClassNode ( OntClass c, int depth ) 
	{		
		// System.out.println( "OntClass:"+ c.getLocalName() + "Depth:"+depth );
		ontoClassNodeMap.put(c.getLocalName(),depth);
		//print all subclass
		if ( c.hasSubClass() )
		{
			for( Iterator<OntClass> it = c.listSubClasses(true); it.hasNext(); )
			{
				OntClass c1 = it.next();
				getOntoClassNode( c1, depth+1 );
			}
		}
		return ;
	}
	
	
	boolean queryClass ( String segWord ) {	
		boolean a = false;
		OntModel ontModel = ontologyFile();
		// ������ʾģ���е��࣬�ڵ�����������ɸ��ֲ���
		for(Iterator<?> i=ontModel.listClasses(); i.hasNext(); ) 	{
			//��������ǿ��ת��
			OntClass c = (OntClass) i.next();  
			//����c�Ƿ����������������࣬��ӡ������
			if ( !c.isAnon() ) 
			{ 
				while( c.getLocalName().equals( segWord ) ) 
				{	
					if ( ontoClassNodeMap.get(segWord) >= 2 ) {
						a = true;
						break;
					}
				}
			}
		}
		return a;
	}	
	
	private static HashMap<String, Integer> ontoClassNodeMap = new HashMap<String, Integer>();
	
	Vector<String> nounStandardization ( String noun ) {
		OntModel ontModel = ontologyFile();		
		Vector<String> nounAndSuper = new Vector<>(2);
		String superclass = null ;
		int depth = ontoClassNodeMap.get( noun );
		if ( depth >= 3 ) { // ����������滻Ϊ������Ŀǰû���ļ����
			for ( ExtendedIterator<OntClass> i = ontModel.listClasses(); i.hasNext(); )	{
				OntClass c = i.next(); 
				if ( !c.isAnon() && c.getLocalName().equals( noun ) ) {
					// nounStandardization
					noun = c.getSuperClass().getLocalName();	
					nounAndSuper.add(noun);
					// һ������������composition  parameters  performance��
					superclass = c.getSuperClass().getSuperClass().getLocalName();					
					nounAndSuper.add(superclass);
					break;
				}
			}
		} else {
			nounAndSuper.add(noun);
			for ( ExtendedIterator<OntClass> i = ontModel.listClasses(); i.hasNext(); )	{
				OntClass c = i.next(); 
				if ( !c.isAnon() && c.getLocalName().equals( noun )) {
					superclass = c.getSuperClass().getLocalName();		
					nounAndSuper.add(superclass);
					break;
				}				
			}
		}
		// System.out.println( nounAndSuper );
		return nounAndSuper;
	}
	
	
}
