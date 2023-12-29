package com.chinamobile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OfferingVerificationApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println("---------------");
    }

	/**
	 * 提取測試
	 * @throws SQLException
	 */
//    @Test
//    void testDoSQL() throws SQLException {
//
//        System.out.println(dataSource.getClass());
//
//		//获取连接
//
//        Connection con = dataSource.getConnection();
//
//		//调用Connection的createStatement方法创建语句对象
//
//        Statement stmt = con.createStatement();
//
//
//		//查询数据
//
//        ResultSet rs = stmt.executeQuery("select * from customer_t1");
//
//		//遍历数据
//
//        while (rs.next()) {
//
//            String sk = rs.getString("c_customer_sk");
//
//            String name = rs.getString("c_customer_name");
//
//            System.out.println("sk:" + sk + " 姓名：" + name);
//
//        }
//
//        con.close();
//
//    }
//

}
