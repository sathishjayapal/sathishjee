<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Sync file upload !" %>
</h1>
<br/>
<form method="POST" action="upload" enctype="multipart/form-data">
    File:
    <input type="file" name="file" id="file"/> <br/>
    Destination:
    <input type="text" value="/tmp" name="destination"/>
    </br>
    <input type="submit" value="Upload" name="upload" id="upload"/>
</form>

</body>
</html>
