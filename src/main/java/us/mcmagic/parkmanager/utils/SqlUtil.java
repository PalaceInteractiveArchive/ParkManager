package us.mcmagic.parkmanager.utils;

import us.mcmagic.mcmagiccore.MCMagicCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Marc on 2/6/15
 */
public class SqlUtil {
    private static String connString;
    private static String username;
    private static String password;

    public static void initialize() {
        connString = MCMagicCore.getMCMagicConfig().sqlConnectionUrl;
        username = MCMagicCore.getMCMagicConfig().sqlUser;
        password = MCMagicCore.getMCMagicConfig().sqlPassword;
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(connString, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
