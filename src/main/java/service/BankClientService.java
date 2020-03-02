package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public  BankClient getClientByName(String name) throws SQLException {
        BankClientDAO dao = getBankClientDAO();
        return dao.getClientByName(name);
    }

    public List<BankClient> getAllClient() {
            BankClientDAO dao = getBankClientDAO();
        List<BankClient> result = null;
        try {
            result = dao.getAllBankClient();
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return result;
    }

    public boolean deleteClient(String name) {
        return false;
    }

    public boolean addClient(BankClient client) throws DBException, SQLException {
        BankClientDAO dao = getBankClientDAO();
        try {
            getClientByName(client.getName());
        } catch (SQLException e) {
            dao.addClient(client);
            return true;
        }
        return false;
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) throws SQLException {
        BankClient recepient = getClientByName(name);   //это получатель
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.updateClientsMoney(sender.getName(), sender.getPassword(), sender.getMoney());
            dao.updateClientsMoney(recepient.getName(), recepient.getPassword(), -value);
        } catch (SQLException e){
            return false;
        }
        return true;
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {

            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
            Connection connection;
            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=admin12345").
                    append("&useSSL=false&serverTimezone=UTC");        //password

            System.out.println("URL: " + url + "\n");

            connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
