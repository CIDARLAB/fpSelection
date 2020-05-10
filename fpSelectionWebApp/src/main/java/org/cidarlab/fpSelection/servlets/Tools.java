/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alex
 */
public class Tools extends HttpServlet {
private ServletContext context;

    public void init(ServletConfig config) throws ServletException
    {
        this.context = config.getServletContext();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/")+"/_LayoutHeader.html")));
        String str;
        PrintWriter out = response.getWriter();
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/")+"/CytometerForm.html")));
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
        in = new BufferedReader(new InputStreamReader(new FileInputStream(context.getRealPath("/")+"/_LayoutFooter.html")));
        while ((str = in.readLine()) != null) {
            out.println(str);
        }
    }
}
