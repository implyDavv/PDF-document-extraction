package protege;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.function.library.leviathan.cartesian;
import org.apache.jena.util.iterator.ExtendedIterator;

public class test {
	
	static HashMap<String, Integer> ontoMap = new HashMap<String, Integer>();
	
	public static void main ( String[] args )  
	{		
		// create the base model
		OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		OntDocumentManager dm = ontModel.getDocumentManager();
		//本体路径
		String filePath = "E:\\protege\\onto.owl";		
		//读取路径下的文件，加载模型
		ontModel.read( filePath );
		int depth=0;		
		for ( ExtendedIterator<OntClass> i = ontModel.listClasses(); i.hasNext(); )	{
			OntClass c = i.next(); 					
			if ( !c.isAnon() && c.getLocalName().equals("挤压铸造")) {
				getOntClassNode( c, depth );
			}
		}
		// System.out.println( ontoMap );
	}
		

	private static void getOntClassNode( OntClass c, int depth ) 
	{		
		// System.out.println( "OntClass:"+ c.getLocalName() + "Depth:"+depth );
		ontoMap.put(c.getLocalName(),depth);
		//print all subclass
		if ( c.hasSubClass() )
		{
			for( Iterator<OntClass> it = c.listSubClasses(true); it.hasNext(); )
			{
				OntClass c1 = it.next();
				getOntClassNode( c1, depth+1 );
			}
		}
		return ;
	}
}
