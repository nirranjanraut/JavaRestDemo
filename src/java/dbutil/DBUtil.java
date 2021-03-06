/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbutil;

import constants.Constants;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author              NiRRaNjAN
 * @stackoverflow       http://stackoverflow.com/users/1911941/elite
 * @playstore           https://play.google.com/store/apps/developer?id=NiRRaNjAN+RauT
 * @facebook            https://www.facebook.com/NiRRaNjAN.RauT
 * @gmail               nirranjan.raut@gmail.com
 */
public class DBUtil implements Constants {

    private static Connection connection;
    private static String errorMessage;

    public static Connection getConnection(String db) {
        try {
            Class.forName(CLASS_NAME);
            connection = DriverManager.getConnection(URL + db, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            errorMessage = e.getMessage();
        }
        return connection;
    }

    public static String getErrorMessage() {
        return errorMessage;
    }

}