package dao;

import exception.DBException;
import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM bank_client")) {
            List<BankClient> allBankClient = new ArrayList<>();
            while (resultSet.next()) {
                BankClient bankClient = new BankClient(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getLong("money")
                );
                allBankClient.add(bankClient);
            }
            return (allBankClient.isEmpty()) ? Collections.emptyList() : allBankClient;
        }
    }

    public boolean validateClient(String name, String password) throws SQLException {
        String nameFromBase = getClientByName(name).getName();
        String passwordFromBase = getClientByName(name).getPassword();
        return passwordFromBase.equals(password) && nameFromBase.equals(name);
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        if (validateClient(name, password)) {
            if (isClientHasSum(name, transactValue)) {
                long after = getClientByName(name).getMoney() - transactValue;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE bank_client SET money = (?) WHERE name = (?)");
                preparedStatement.setLong(1, after);
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } else throw new SQLException();
        } else throw new SQLException();
    }

    public BankClient getClientById(long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_client WHERE name = (?)");
        preparedStatement.setLong(1, id);
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        resultSet.next();
        long idResult = resultSet.getLong(1);
        String nameClient = resultSet.getString(2);
        String password = resultSet.getString(3);
        Long money = resultSet.getLong(4);
        preparedStatement.close();
        return new BankClient(idResult, nameClient, password, money);

    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        return getClientByName(name).getMoney() >= expectedSum;
    }

    public long getClientIdByName(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from bank_clien WHERE name = (?)");
        preparedStatement.setString(1, name);
        preparedStatement.execute();
        ResultSet result = preparedStatement.getResultSet();
        result.next();
        long id = result.getLong(1);
        result.close();
        preparedStatement.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_client WHERE name = (?)");
        preparedStatement.setString(1, name);
        preparedStatement.executeQuery();
        ResultSet result = preparedStatement.getResultSet();
        result.next();
        String nameClient = result.getString(2);
        long id = result.getLong(1);
        String password = result.getString(3);
        Long money = result.getLong(4);
        preparedStatement.close();
        return new BankClient(id, nameClient, password, money);
    }

    public void addClient(BankClient client) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bank_client(name, password, money) VALUES (?, ?, ?)");
        preparedStatement.setString(1, client.getName());
        preparedStatement.setString(2, client.getPassword());
        preparedStatement.setLong(3, client.getMoney());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void createTable() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void dropTable() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS bank_client");
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
