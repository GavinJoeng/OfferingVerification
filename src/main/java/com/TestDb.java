package com;

import java.sql.*;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/23 10:59
 */
public class TestDb {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.huawei.ngcbs.dbmanproxydriver.jdbc.ZCloudProxyDriver");
        try (Connection connection = DriverManager.getConnection("jdbc:zcloudproxy@10.0.54.18:21800?cluster=saassysdb_cluster1&vnode=106&mode=native",
                "admin", "A#dmin_1")) {

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT CHARGE_CODE_ID, CHARGE_CODE FROM CM_CHARGE_CODE")) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(2));
                    }
                }

            }

        }

    }
}
