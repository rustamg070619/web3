package dao;

import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("UPDATE bank_client SET money =" + after + " WHERE name = '" + name + "'");
                // stmt.close();
            } else throw new SQLException();
        } else throw new SQLException();
    }

    public BankClient getClientById(long id) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeQuery("SELECT * FROM bank_client WHERE name ='" + id + "'");
        ResultSet resultSet = stmt.getResultSet();
        resultSet.next();
        long idResult = resultSet.getLong(1);
        String nameClient = resultSet.getString(2);
        String password = resultSet.getString(3);
        Long money = resultSet.getLong(4);
        stmt.close();
        return new BankClient(idResult, nameClient, password, money);

    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        return getClientByName(name).getMoney() >= expectedSum;
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_clien where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeQuery("SELECT * FROM bank_client WHERE name ='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        String nameClient = result.getString(2);
        long id = result.getLong(1);
        String password = result.getString(3);
        Long money = result.getLong(4);
        stmt.close();
        return new BankClient(id, nameClient, password, money);
    }

    public void addClient(BankClient client) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT INTO bank_client(name, password, money) VALUES ( '" + client.getName() + "','" + client.getPassword() + "','" + client.getMoney() + "')");
        stmt.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
