<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quét mã QR để thanh toán</title>
</head>
<body>
    <h2>Quét mã QR bằng ứng dụng ngân hàng</h2>
    <img src="${qrLink}" alt="QR VNPAY" width="300" height="300"/>
    <p>Nếu app không tự mở, hãy bấm
        <a href="${paymentUrl}" target="_blank">vào đây</a>.
    </p>
</body>
</html>
