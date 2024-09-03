package flow;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;  
import java.sql.SQLException; 

import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apdplat.word.segmentation.Word;  


public class QueryMySQL {	
	
	
	/**
	  * 
	 * @return 
	 * @throws SQLException
	 * @throws IOException
	 */
	void m ( PDDocument document ) throws Exception
	{	
		/*/ 获得 ArticleInformation
		ArticleInformation info = new ArticleInformation( document );
		String title = info.getArticleTitle();
		System.out.println( title );
		//info.getArticleAuthors();		*/
		
		// 摘要
		
		// 正文
		Preprocess pre = new Preprocess(  );
		String body = pre.getText(document);
		KeySentences k = new KeySentences();
		Vector<List<Word>> resultVector = k.sentences( body );	
		System.out.print( "resultVector===" + resultVector );
		
		// 数据库连接
		ConnToSQL connToSQL = new ConnToSQL();  
		Connection conn = connToSQL.getConnection();
		// Statement中执行executeUpdate
		Statement stmt = conn.createStatement();
		
		// 唯一标识符 idArticle ：yyyyMMddXXXX
		String sql = "select max(idArticle) from articles";
		// executeQuery 
        ResultSet rs = stmt.executeQuery( sql );
        String max = "";
        if ( rs.next() ) {  
        	max = rs.getString("max(idArticle)");
		}
        else {
        	max = null;
        }        
        PrimaryGenerater primaryGenerater = new PrimaryGenerater();
        String idArticle = primaryGenerater.generatPK(max);
        /*/ 存 articles (idArticle,Title)
        sql = "insert into articles(idArticle,Title) values('" + idArticle + "', '"+ title + "')" ;
        stmt.execute( sql );*/
        
        // 初始化 composition & parameters  primaryKey
        // idComposition & idParameters 
        // idComposition
        sql = "select max(material_id) from material";        
		rs = stmt.executeQuery( sql );
		if ( rs.next() ) {  
        	max = rs.getString("max(material_id)");
		}
        else {
        	max = null;
        }
		// 初始化 primaryKey：idComposition
		String material_id = primaryGenerater.primaryKey( max );

        // idParameters
        sql = "select max(parameters_id) from parameters";
		rs = stmt.executeQuery( sql );
		if ( rs.next() ) {  
        	max = rs.getString("max(parameters_id)");
		}
        else {
        	max = null;
        }
		// 初始化  primaryKey ： idParameters
		String idParameters = primaryGenerater.primaryKey( max );

		// Vector<String> 存一篇文章对应的 idParameters & idComposition
		Vector<String> idParametersVector = new Vector<String>( );
		Vector<String> idCompositionVector = new Vector<String>( );		
		
        // 存 body 中的 data
        for( int i=0; i<resultVector.size(); i++ ) {
			List<Word> list = resultVector.get(i);
			Result result = getResult( list );
			String category = result.getCategory();

			// idParameters || material_id
			if ( !category.equalsIgnoreCase("composition") ) {
				category = "parameters" ;				
			}
			String noun = result.getNoun();
			String data = result.getData();
			System.out.println( i +"]" + " category~~ " + category + ";  noun~~ " + noun + ";  data~~" + data);

			// 插入数据
			if ( i==0 ) {
				switch ( category ) {
				case "parameters":
					sql = "insert into " + category + "(idParameters,idArticle,"+ noun + ") values('"+ idParameters + "', '" + idArticle + "', '" + data + "')";
					stmt.execute( sql );
					idParametersVector.add( idParameters );
					idParameters = primaryGenerater.primaryKey( idParameters );
					break;
				
				case "material_id":
					sql = "insert into " + category + "(material_id,idArticle,"+ noun + ") values('"+ material_id + "', '" + idArticle + "', '" + data + "')";
					stmt.execute( sql );
					idCompositionVector.add( material_id );
					material_id = primaryGenerater.primaryKey( material_id );
					break;
				}
				continue;
			}
			else {
				switch ( category ) {
				case "parameters":
					if ( idParametersVector.size() == 0 ) {						
						sql = "insert into " + category + "(idParameters,idArticle,"+ noun + ") values('"+ idParameters + "', '" + idArticle + "', '" + data + "')";
						stmt.execute( sql );
						idParametersVector.add( idParameters );
						idParameters = primaryGenerater.primaryKey( idParameters );
						break;
					}
					else {
						Boolean bool = false;
						for (int j = 0; j < idParametersVector.size(); j++) {
							sql = "select " + noun + " from parameters where " + noun + " is null and idParameters=" + idParametersVector.get(j);
							rs = stmt.executeQuery( sql );
							if ( rs.next() ) { // 结果集不为空
								// 插入数据
								sql = "update parameters set " + noun + "='" + data + "' where idParameters='" + idParametersVector.get(j) + "'";
								stmt.executeUpdate ( sql );
								bool = true;
								break ;		
							}
							else { // 结果集为空
								continue;
							}
						}
						if ( !bool ) {
							sql = "select " + noun + " from parameters where idParameters=" + idParametersVector.lastElement();
							rs = stmt.executeQuery( sql );
							if ( rs.next() ) {  
					        	if ( ! rs.getString( noun ).equals( data ) ) {
									sql = "insert into " + category + "(idParameters,idArticle,"+ noun + ") values('"+ idParameters + "', '" + idArticle + "', '" + data + "')";
									stmt.execute( sql );
									idParametersVector.add( idParameters );
									idParameters = primaryGenerater.primaryKey( idParameters );
								} else {
									continue;
								}
							}
					        else {
					        	sql = "insert into " + category + "(idParameters,idArticle,"+ noun + ") values('"+ idParameters + "', '" + idArticle + "', '" + data + "')";
								stmt.execute( sql );
								idParametersVector.add( idParameters );
								idParameters = primaryGenerater.primaryKey( idParameters );					        	
					        }							
							break;
						}
						else {
							break;
						}			
					}
					
				case "material":
					if ( idCompositionVector.size() == 0 ) {						
						sql = "insert into " + category + "(material_id,idArticle,"+ noun + ") values('"+ material_id + "', '" + idArticle + "', '" + data + "')";
						stmt.execute( sql );
						idCompositionVector.add(material_id);
						material_id = primaryGenerater.primaryKey( material_id );
						break;
					}
					else {
						Boolean bool = false;
						for (int j = 0; j < idCompositionVector.size(); j++) {
							sql = "select " + noun + " from material where " + noun + " is null and material_id=" + idCompositionVector.get(j);
							rs = stmt.executeQuery( sql );
							if ( rs.next() ) { // 结果集不为空								
								// 插入数据
								sql = "update material set " + noun + "='" + data + "' where material_id='" + idCompositionVector.get(j) + "'";
								stmt.executeUpdate ( sql );
								idCompositionVector.add(material_id);
								material_id = primaryGenerater.primaryKey( material_id );
								bool = true;
								break ;								
							}
							else { // 结果集为空
								continue;
							}
						}
						if ( !bool ) {
							sql = "insert into " + category + "(material_id,idArticle,"+ noun + ") values('"+ material_id + "', '" + idArticle + "', '" + data + "')";
							stmt.execute( sql );
							idCompositionVector.add(material_id);
							material_id = primaryGenerater.primaryKey( material_id );
							break;
						}
						else {
							break;
						}
					}
				}
			}
		}
        rs.close();
		conn.close();
	}

	

	private Result getResult ( List<Word> list ) {
		Result result = new Result();
		OntologyQuery onto = new OntologyQuery() ;
		Vector<String> nounAndSuper = new Vector<String>(2);
		int index = 0 ;
		String word;
		// get Noun
		for( int i=0; i<list.size(); i++ ) {
			word = list.get(i).toString();
			boolean bool = onto.queryClass( word );
			if ( bool ) {				
				// nounStandardization
				nounAndSuper = onto.nounStandardization( word );				
				word = nounAndSuper.get(0);
				result.setNoun( word );
				result.setCategory( nounAndSuper.get(1) );
				index = i;
				break;
			}
		}
		
		// get Data
		String data = "";
		Check check = new Check();		
		// noun 鐨� index != 鏈�鍚庝竴浣嶏細 浠� noun 寰�鍚庢壘
		if ( index < list.size()-1 ) {
			for( int j = index ; j < list.size()-1; j++ ) {
				word = list.get(j+1).toString();
				if ( check.checkData( word ) ) {
					if ( data.length() != 0 ) {
						data += "/" ;
					}				
					data += word;
				}			
			}
			if ( data.length() == 0 ) {
				for( int j = index ; j >= 1; j-- ) {
					word = list.get( j-1 ).toString();
					if ( check.checkData( word ) )
					{
						if ( data.length() != 0 ) {
							data = "/" + data;
						}
						data = word + data;				
					}	
				}
				result.setData(data);
			}
			else {
				result.setData(data);
			}
			
		}
		else {
			for( int j = index ; j >= 1; j-- ) {
				word = list.get( j-1 ).toString();
				if ( check.checkData( word ) )
				{
					if ( data.length() != 0 ) {
						data = "/" + data;
					}
					data = word + data;				
				}
				result.setData(data);
			}
		}
		
		return result;
	}

}

