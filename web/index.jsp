<%-- 
    Document   : index
    Created on : 22.feb.2013, 13:12:53
    Author     : abnormal
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="javax.jmdns.ServiceInfo"%>
<%@page import="javax.jmdns.JmDNS"%>
<%@page import="java.net.InetAddress"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web service app Obligatorisk 3</title>
    </head>
    <body>
        <h1>Hello Asbjørn!</h1>
        <p>Webservice er tilgjengelig her: <%="http://"
                + InetAddress.getLocalHost().getHostAddress()
                + ":" + request.getServerPort()
                + request.getContextPath() + "/Oblig3Service"%>
        </p>
        <p>WSDL fil er tilgjengelig her <%="http://"
                + InetAddress.getLocalHost().getHostAddress()
                + ":" + request.getServerPort()
                + request.getContextPath() + "/Oblig3Service?WSDL"%>
        </p>
        <p>Du kan teste web-service via webgrensesnitt her: <%="http://"
                + InetAddress.getLocalHost().getHostAddress()
                + ":" + request.getServerPort()
                + request.getContextPath() + "/Oblig3Service?tester"%>
        </p>
        <p>
            <%
                Object message = application.getAttribute("messages");
                if(message instanceof List) {
                    out.println("<ul>");
                    for(int i = 0; i < ((List)message).size(); i++) {
                        out.println("<li>"+((List)message).get(i)+"</li>");
                    }
                    out.println("</ul>");
                }
            %>
        </p>
    </body>
</html>
