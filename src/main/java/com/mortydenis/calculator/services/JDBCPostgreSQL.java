package com.mortydenis.calculator.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCPostgreSQL {
    private static JDBCPostgreSQL instance;
    private Connection connection;
    private Statement statement;
    private final String DB_URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=calculator" ;
    private final String USER = "postgres" ;
    private final String PASS = "MortyDenis" ;

    private JDBCPostgreSQL(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JDBCPostgreSQL getInstance() {
        if (instance == null) {
            instance = new JDBCPostgreSQL();
        }
        return instance;
    }

    public void saveOperation(String operation) {
        String sql = String.format("INSERT INTO operations(operation) VALUES('%s')", operation);
        try {
            statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<String> getHistoryOperations() {
        List<String> operations = new ArrayList<>();
        String sql = String.format("SELECT operation FROM operations ORDER BY id desc LIMIT 10");
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()) {
                operations.add(resultSet.getString("operation"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return operations;
    }
}
