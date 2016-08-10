/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cidarlab.fpSelection.servlets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 * @author prash
 */
public class StartFpSelectionWebApp {

    public static void main(String[] args) {
        Server server = new Server(8080);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        WebAppContext contextWeb = new WebAppContext();
        contextWeb.setDescriptor(context + "/WEB-INF/web.xml");
        contextWeb.setResourceBase("../fpSelectionWebApp/src/main/webapp/");
        contextWeb.setConfigurationClasses(new String[]{"org.eclipse.jetty.webapp.WebInfConfiguration",
                "org.eclipse.jetty.webapp.WebXmlConfiguration",
                "org.eclipse.jetty.webapp.MetaInfConfiguration",
                "org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration"});
        contextWeb.setContextPath("/");
        contextWeb.setParentLoaderPriority(true);
        
        HandlerList handlers = new HandlerList();
        handlers.addHandler(contextWeb);
        server.setHandler(handlers);
        
        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
