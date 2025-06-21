# VNPay QR Payment Demo

This project demonstrates a simple VNPay QR payment page using JSP and Servlets.

## Usage

1. Update `VNP_TMN_CODE` and `VNP_HASH_SECRET` in `src/java/com/vnpay/PaymentServlet.java` with credentials provided by VNPay.
2. Build and deploy the project to your servlet container (e.g. Tomcat). If you have Ant installed you can run:

```bash
ant -Dnb.internal.action.name=run
```

3. Open `http://localhost:8080/vnpayqr/index.jsp` in your browser and submit the form to generate a VNPay payment URL.

The return data from VNPay will be displayed on `return.jsp`.
