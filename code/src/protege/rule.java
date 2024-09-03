package protege;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.*;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;

public class rule {
	public static void rule(String[] args) 
	{		
		//创建本体模型
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);  
		//读取路径下的文件，加载模型
		ontModel.read("file:E:\\protégé\\Ontology190305.owl"); 			
		//定义自定义规则
		String rule1="[rule1: (?a yl:hasHusband ?b)(?a yl:isMotherOf ?c)->(?b yl:isFatherOf ?c)]";
		//定义查询
		String queryString="...";
		//创建推理机
		Reasoner reasoner= new GenericRuleReasoner(Rule.parseRules(rule1));
		//创建含有推理规则的infModel
		InfModel inf= ModelFactory.createInfModel(reasoner,ontModel);
		//生成查询
		Query query = QueryFactory.create(queryString); 
		//执行查询
		try (QueryExecution qexec=QueryExecutionFactory.create(query,ontModel)){
			ResultSet results= qexec.execSelect();
			for ( ; results.hasNext() ; ) {
				QuerySolution soln = results.nextSolution();
				RDFNode x=soln.get("varName");  //Get a result variable by name.
				Resource r= soln.getResource("VarR");  //Get a result variable -must be a resource
				Literal l= soln.getLiteral("VarL");  //Get a result variable -must be a literal
			}			
		}
	}
	
}
