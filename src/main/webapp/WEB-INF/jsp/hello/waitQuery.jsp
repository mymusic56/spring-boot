<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
	<h3>${date}</h3>
	<table border="1px" cellspacing="0" style="text-align:center;">
		<tr>
			<th>Num</th>
			<th>ID</th>
			<th>Name</th>
			<th>Task</th>
		</tr>
		<c:forEach varStatus="status" var="stu" items="${voiceList}">
			<tr>
				<td>${status.count}</td><!-- ${status.index} -->
				<td>${stu.getId()}</td>
				<td>${stu.getC_id()}</td>
				<td>${stu.getTask_id()}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</body>
</html>