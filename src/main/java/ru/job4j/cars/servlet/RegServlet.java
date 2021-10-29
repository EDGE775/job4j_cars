package ru.job4j.cars.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.HbmService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("reg.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        HbmService service = HbmService.instOf();
        if (service.findUserByEmail(email) != null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь с такой почтой уже зарегистрирован!");
        } else {
            service.saveUser(new User(name, email, password));
            resp.sendRedirect(req.getContextPath() + "/auth.do");
        }
    }
}
