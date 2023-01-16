<%--
  Created by IntelliJ IDEA.
  User: skminfotech
  Date: 1/7/23
  Time: 9:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>uploader file- async mode</title>
</head>
<body>
<form method="POST" action="asyncServlet" enctype="multipart/form-data">
    File:
    <input type="file" name="file" id="file"/> <br/>
    </br>
    <input type="submit" value="Upload" name="asyncServlet" id="asyncServlet"/>
</form>
</body>
</html>
