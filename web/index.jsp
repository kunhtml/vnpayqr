<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>VNPay QR Demo</title>
</head>
<body>
    <h3>Create VNPay QR Payment</h3>
    <form method="post" action="create_payment">
        <div>
            <label>Amount (VND):</label>
            <input type="number" name="amount" value="10000" />
        </div>
        <div>
            <label>Order Info:</label>
            <input type="text" name="orderinfo" value="Test order" />
        </div>
        <button type="submit">Pay with VNPay QR</button>
    </form>
</body>
</html>
