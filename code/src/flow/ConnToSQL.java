package flow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apdplat.word.segmentation.Word;

public class ConnToSQL 
{
	/**
	  * 一个通用的方法，不修改源程序的情况下，可以获取任何数据库的连接
	  * 方法：把数据库驱动Driver实现类的全类名、URL、user、password、放入一个配置文件
	  * 通过修改配置文件的方式实现和具体的数据库连接
	 * @throws SQLException
	 */	
	Connection getConnection() throws SQLException, IOException 
	{		
		// 读取类路径下的jdbc.properties文件
		Properties properties = new Properties();
		try(InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties"))
		{
			properties.load(in);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();	
		}
		
		String driverClass = properties.getProperty("driver");
		String jdbcUrl = properties.getProperty("jdbcUrl");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		// 加载 MySQL JDBC 驱动程序 
		Driver driver = null ;  
		try 
		{
			// 注册 JDBC 驱动
			driver = (Driver) Class.forName(driverClass).newInstance();
		} 
		catch (InstantiationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
		
		Properties info = new Properties();
		info.put("user", user);
		info.put("password", password);
		Connection conn = driver.connect( jdbcUrl, info );
		System.out.println("Success loading Mysql Driver!"); 
		return conn;  
	}	 

	
	/**
	  * 创建表，插入列名
	  * 存储标准格式的数据
	 * @throws SQLException
	 * @throws IOException
	 */
	void creTable( File file, Vector<List<Word>> senVector) throws SQLException, IOException 
	{
		String name = file.getName();
		// 数据库连接
		Connection conn = getConnection(); 
		// 调用Statement里面的方法，executeUpdate实现插入，更新和删除等
		Statement stmt = conn.createStatement(); 
		if(senVector == null) 
		{
			System.out.println("error");
		} else {
			int i=0; 
			if (i<senVector.size()) 
			{
				List<Word> sentence = senVector.get(i);
				if(sentence == null || sentence.size() != 2) 
				{
					System.out.println("error");
				} 
				else 
				{
					String noun = sentence.get(0).toString();
					String word = sentence.get(1).toString();
					String sql = "create table"+ name + "("+ noun +" varchar(255))";
					// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功  
					int result = stmt.executeUpdate(sql);									
					if (result != -1) 
					{
						System.out.println("创建数据表成功");
						sql = "insert into" + name + "("+noun+")"+ "values("+word+")";  
						result = stmt.executeUpdate(sql);
						i++;
						
					}											
				}				
			}
		}
	}
}
		
			


