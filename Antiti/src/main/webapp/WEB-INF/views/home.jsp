<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>

<form action="/ws/login" method="post">
	<input type="text" name="id" placeholder="ID" />
	<input type="submit" value="go" />
</form>
</body>
</html>
