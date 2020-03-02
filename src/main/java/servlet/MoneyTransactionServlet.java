package servlet;

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

public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senderName = req.getParameter("senderName");
        String senderPass = req.getParameter("senderPass");
        long count = Long.parseLong(req.getParameter("count"));
        String nameTo = req.getParameter("nameTo");
        try {
           if(bankClientService.sendMoneyToClient(new BankClient(senderName, senderPass,count), nameTo, count))
           {
               resp.getWriter().println("The transaction was successful");
               resp.setStatus(HttpServletResponse.SC_OK);
           } else {
               resp.getWriter().println("transaction rejected");
               resp.setStatus(HttpServletResponse.SC_OK);
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
