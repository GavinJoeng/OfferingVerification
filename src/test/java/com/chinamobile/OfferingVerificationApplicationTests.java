package com.chinamobile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OfferingVerificationApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println("---------------");
    }

    @Test
    public void Test(){



    }


    public static String getQuerySQL(String SQLFileName){

        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(SQLFileName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return new String(bytes);
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
