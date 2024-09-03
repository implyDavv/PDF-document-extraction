package flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.apache.pdfbox.pdmodel.PDDocument;

public class test {
	
	public static void main ( String[] args ) throws Exception 	{   
		// 获取开始时间
		long startTime = System.currentTimeMillis(); 
		
		File file = new File("f:/00 小论文/00 Paper/2 表格抽取/2 实例/3 文献集/"
        		+ "3-50 挤压铸造对重熔原位α﹣Al2O3pZL109复合材料组织与性能的影响/"
        		+ "挤压铸造对重熔原位α﹣Al2O3pZL109复合材料组织与性能的影响.pdf");
		PDDocument document = PDDocument.load(file); 
		int file_len = file.getAbsolutePath().length();	
    	PrintStream old = System.out;	
		/*if( document.isEncrypted() ) {
            try {
				document.decrypt( "" );
			} 
            catch (CryptographyException e) {
				e.printStackTrace();
			}
        }*/
    	try	{   // 文件输出路径
	        PrintStream out = new PrintStream(file.getAbsolutePath().substring(0, file_len-4) + "_out.txt");
	        System.setOut(out);
    	} catch( FileNotFoundException e) {
	        e.printStackTrace();
	    }    	
    	
    	// 表格抽取
    	TableExtraction te = new TableExtraction();
		te.testExtraction( document );
		
		long endTime = System.currentTimeMillis(); // 获取结束时间
		 
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");// 输出程序运行时间
		
		System.setOut(old);	
		if ( document != null ) {
			document.close();
		}   
		 /*
		QueryMySQL sql = new QueryMySQL();		  
		sql.m ( document );	
		
		ConnToSQL connToSQL = new ConnToSQL();
		Connection conn = connToSQL.getConnection();
        Statement stmt = conn.createStatement();
       
        // System.out.println( order );
        /* ResultSetMetaData rsmd = rs.getMetaData();
        // 取得全部列数
        int colcount = rsmd.getColumnCount();
        System.out.println( colcount );
        for(int i=1;i<=colcount;i++){
        	String colname = rsmd.getColumnName(i);//取得全部列名
        	
        }		        
        sql = "insert into articles(idArticle) values("+ id +")";  
               
        conn.close();*/
	}	
}
