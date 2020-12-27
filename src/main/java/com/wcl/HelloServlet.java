package com.wcl;

import com.wcl.entity.bo.HeadLine;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = "我的简易框架";
        request.setAttribute("name",name);
        request.getRequestDispatcher("/WEB-INF/jsp/hello.jsp").forward(request,response);
        HeadLine headLine = new HeadLine();

    }

}
