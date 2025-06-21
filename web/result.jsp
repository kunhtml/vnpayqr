<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://jakarta.apache.org/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kết quả thanh toán</title>
</head>
<body>
<c:choose>
    <c:when test="${status eq 'success'}">
        <h2 style="color:green">Thanh toán thành công!</h2>
    </c:when>
    <c:otherwise>
        <h2 style="color:red">Thanh toán thất bại!</h2>
    </c:otherwise>
</c:choose>
<a href="/">Quay về trang chủ</a>
</body>
</html>
