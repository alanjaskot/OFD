package pl.AJMNSM.OFD.core.connectionSQL;

import java.sql.*;

public class ConnectorSQLLogin {

    private final String URL = "jdbc:mysql://localhost:3306/projectdb";
    private final String userDB = "root";
    private final String passwordDB = "";


    private boolean login(String URL, String userDB, String passwordDB) {

        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, userDB, passwordDB);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");
            while (resultSet.next()) {
                if ((resultSet.getString(1).equals("")) && resultSet.getString(2).equals("")) {
                   System.out.println("Witaj " + resultSet.getString(1) + "  " + resultSet.getString(2) + "  " + resultSet.getString(3));
                   return true;
                }
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }




}
