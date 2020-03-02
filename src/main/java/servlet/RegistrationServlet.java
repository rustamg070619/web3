package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String name = req.getParameter("name");
            String password = req.getParameter("password");
            long money = Long.parseLong(req.getParameter("money"));
            if (new BankClientService().addClient(new BankClient(name, password, money))) {
                resp.getWriter().println("Add client successful");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.getWriter().println("Client not add");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (DBException | NumberFormatException | SQLException e) {
            resp.getWriter().println("Client not add");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
