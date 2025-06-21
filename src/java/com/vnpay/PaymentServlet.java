package com.vnpay;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/create_payment")
public class PaymentServlet extends HttpServlet {

    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_TMN_CODE = "YOUR_TMN_CODE";
    private static final String VNP_HASH_SECRET = "YOUR_SECRET_KEY";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURN_URL = "http://localhost:8080/return.jsp";

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String buildUrl(SortedMap<String, String> params) throws Exception {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (query.length() > 0) {
                query.append('&');
            }
            query.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            query.append('=');
            query.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String hashData = query.toString();
        String secureHash = hmacSHA512(VNP_HASH_SECRET, hashData);
        return VNP_URL + "?" + hashData + "&vnp_SecureHash=" + secureHash;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String amount = req.getParameter("amount");
            String orderInfo = req.getParameter("orderinfo");
            SortedMap<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", VNP_VERSION);
            vnpParams.put("vnp_Command", VNP_COMMAND);
            vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
            vnpParams.put("vnp_Amount", String.valueOf(Integer.parseInt(amount) * 100));
            vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_IpAddr", req.getRemoteAddr());
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_OrderInfo", orderInfo != null ? orderInfo : "Payment");
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_ReturnUrl", VNP_RETURN_URL);
            vnpParams.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
            String paymentUrl = buildUrl(vnpParams);
            resp.sendRedirect(paymentUrl);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
