package com.hollycrm.smcs.http;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import com.hollycrm.smcs.http42.HttpClient;

public class DownloadBai {
	
	@Test
	public void downPic() throws Exception {
		String user = "root";
		String password = "123456";
		String url = "jdbc:mysql://localhost:3306/weibo";
		String driver = "com.mysql.jdbc.Driver";
		Connection con;
		Statement stmt;
	    int count = 1000 ;
	    int start = 0;
	    ResultSet rs;
	   String userId;
	   String loginName;
		try {
			 Class.forName(driver);
			 con = DriverManager.getConnection(url, user, password);
			 stmt = con.createStatement();
			 String sql = "select user_id,login_name from peixun_user_141021 limit %s, 1000 ";
			 while(count == 1000) {
				 count = 0;
				 rs = stmt.executeQuery(String.format(sql, start));
				 while (rs.next()) {
					 userId = rs.getString(0);
					 loginName = rs.getString(1);
					 
					 	HttpClient client = new HttpClient();
						byte [] fileByte = client.httpGetWithByte(String.format("http://family.baidu.com:8083/images/userimages/%s.jpg", loginName));
						FileOutputStream fos = new FileOutputStream(new File(String.format("C:\\Users\\dingqinjian\\Desktop\\pic\\%s.jpg", userId)));
						fos.write(fileByte);
						fos.close();
				 }
				 
			 }
			 
			 
		} catch(Exception e) {
			
		}		
	}

}
