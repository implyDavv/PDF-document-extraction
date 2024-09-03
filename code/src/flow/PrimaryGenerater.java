package flow;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrimaryGenerater {

	
	/**
	 * PrimaryGenerater 序列号  No.yyyyMMddXXXX
	 * @param 从数据库查询出的最大编号  maxOrder
	 * @return
	 */
	String generatPK ( String maxOrder ) {
		String order = null;
	    // 时间字符串产生方式
	    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");	    
	    String uid_pfix = format.format( new Date() ); 	 
	    if ( maxOrder != null && maxOrder.contains( uid_pfix )) {
	    	// 截取字符串最后四位
	    	String uid_end = maxOrder.substring(12,15);
	    	// 把 String 类型 转化为 int 类型
	        int endNum = Integer.parseInt( uid_end ) + 1; 
	        // 0：前面补充0；4：长度为4；d：参数为正数型     
	        String count = String.format("%04d", endNum );	       
	       	// 字符串拼接
	        order = uid_pfix + count;
	    } else {
	    	order = uid_pfix + "0001";
	    }
		
	    return order;
	}
	
	String primaryKey ( String maxOrder ) {
		String order = null;
	    if ( maxOrder != null ) {
	    	// String 类型 转化为 int 类型
	        int num = Integer.parseInt( maxOrder ) + 1; 
	        // int 类型 to String
	        order = String.valueOf( num );
	    } else {
	    	order = "1";
	    }
		
	    return order;
	}
	
	
}
