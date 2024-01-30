package com.chinamobile.test;

import com.chinamobile.tools.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/23 18:19
 */
public class JdbcTest {

    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCUtils.getConnection();
        String sql = "SELECT SUB_PATTERN_ACTION FROM PE_MATRIX_DATA  WHERE MATRIX_ID ='1697113418267311989'";
        List<HashMap<String, Object>> list = JDBCUtils.queryObjAsList(connection, sql);
        List<String> stringList = list.stream().map(
                stringObjectHashMap -> {
                    StringBuilder sb = new StringBuilder();
                    stringObjectHashMap.forEach((key, value) -> sb.append(key).append("=").append(value).append(","));
                    sb.deleteCharAt(sb.length() - 1);
                    return sb.toString();
                }
        ).collect(Collectors.toList());

        System.out.println(stringList);
        List<String> stringList1 = JDBCUtils.queryClobAsList(connection, sql);
        System.out.println(stringList1);
        JDBCUtils.closeResource(connection);


    }
}
