package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //Создание новой сессии
        HttpSession currentSession = req.getSession(true);

        //Создание игрового поля
        Field field = new Field();
        Map<Integer, Sign> fildData = field.getField();

        //Получение списка значений поля
        List<Sign> data = field.getFieldData();

        //Добавление в сессию параметров поля для хранения состояния между запросами
        currentSession.setAttribute("field", field);

        //и значений поля, отсортированных по индексу для отрисовки крестиков и ноликов
        currentSession.setAttribute("data", data);

        //Перенаправление запроса на страницу index.jsp через сервер
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
