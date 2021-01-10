package pl.AJMNSM.OFD.core.connectionSQL;

import java.sql.*;

public class ConnectorSQLRegister {

    private final String URL = "jdbc:mysql://localhost:3306/projectdb";
    private final String userDB = "root";
    private final String passwordDB = "";


    private boolean register(String URL, String userDB, String passwordDB) {
        String email = "aaa@aa.a";
        String password = "123";
        String username = "Alan";
        ConnectorSQLLogin csl = new ConnectorSQLLogin();


        try {

            Connection connection = DriverManager.getConnection(URL, userDB, passwordDB);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");
            while (resultSet.next()) {
                if ((resultSet.getString(1).equals(email)) && resultSet.getString(2).equals(password)) {
                    System.out.println(resultSet.getString(1) + "  " + resultSet.getString(2) + "  " + resultSet.getString(3));
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
