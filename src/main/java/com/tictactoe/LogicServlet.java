package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name="LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Получение текущую сессию
        HttpSession currentSession = req.getSession();

        //Получение объекта игрового поля из сессии
        Field field = extractField(currentSession);

        //Получение индекса ячейки, по которой произошел клик
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);

        //Проверка, что ячейка, в которой был клик - пустая.
        //Иначе ничего не делаем и отправляем пользователя на ту же страницу без изменений параметров сессии
        if(Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        //Постановка крестика в ячейке, где кликнул пользователь
        field.getField().put(index, Sign.CROSS);

        //Проверка, есть ли победитель после постановки Х
        if(checkWin(resp, currentSession, field)){
            return;
        }

        //Получение пустой ячейки поля
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            //Проверка, не победил ли нолик после добавления нолика
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        }
        //Если пустой ячейки нет, то это ничья
            else {
                // Добавляем в сессию флаг, который сигнализирует что произошла ничья
                currentSession.setAttribute("draw", true);

                // Считаем список значков
                List<Sign> data = field.getFieldData();

                // Обновляем этот список в сессии
                currentSession.setAttribute("data", data);

                // Шлем редирект
                resp.sendRedirect("/index.jsp");
                return;
            }
            
        //Счет списка значков
        List<Sign> data = field.getFieldData();

        //Обновление объекта поля и списка значков в сессии
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if(Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    //Метод, проверяющий наличие трех Х или 0 подряд
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            //Добавление флага, что имеется победитель. Параметр используется далее
            currentSession.setAttribute("winner", winner);

            //Подсчет списка значков
            List<Sign> data = field.getFieldData();

            //Обновление списка в сессии
            currentSession.setAttribute("data", data);

            //Перенаправление
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
