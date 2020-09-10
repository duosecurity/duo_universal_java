<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
<body>
    <%
        String authURL = (String) request.getAttribute("authURL");
        response.sendRedirect(authURL);
    %>
</body>
</html>