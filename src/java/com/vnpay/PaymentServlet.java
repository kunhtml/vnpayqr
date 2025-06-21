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

    /* ========= CẤU HÌNH VNPAY ========= */
    private static final String VNP_VERSION     = "2.1.0";
    private static final String VNP_COMMAND     = "pay";
    private static final String VNP_TMN_CODE    = "YOUR_TMN_CODE";
    public static final String VNP_HASH_SECRET = "YOUR_SECRET_KEY";
    private static final String VNP_URL         = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_RETURN_URL  = "https://your-domain.com/vnp_return";
    private static final String DATE_FORMAT     = "yyyyMMddHHmmss";
    /* =================================== */

    /* ---------- util cơ bản ---------- */
    public String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public String buildQuery(SortedMap<String, String> params) throws Exception {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (query.length() > 0) query.append('&');
            query.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                 .append('=')
                 .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return query.toString();
    }

    private String buildPaymentUrl(SortedMap<String, String> params) throws Exception {
        String hashData   = buildQuery(params);
        String secureHash = hmacSHA512(VNP_HASH_SECRET, hashData);
        return VNP_URL + '?' + hashData + "&vnp_SecureHash=" + secureHash;
    }
    /* ---------------------------------- */

    private SortedMap<String, String> generateVnpParams(HttpServletRequest req) {
        String amount    = req.getParameter("amount");
        String orderInfo = req.getParameter("orderinfo");

        SortedMap<String, String> p = new TreeMap<>();
        p.put("vnp_Version",    VNP_VERSION);
        p.put("vnp_Command",    VNP_COMMAND);
        p.put("vnp_TmnCode",    VNP_TMN_CODE);
        p.put("vnp_Amount",     String.valueOf(Integer.parseInt(amount) * 100));
        p.put("vnp_CurrCode",   "VND");
        p.put("vnp_TxnRef",     String.valueOf(System.currentTimeMillis()));
        p.put("vnp_OrderInfo",  orderInfo != null ? orderInfo : "QR Pay");
        p.put("vnp_OrderType",  "other");
        p.put("vnp_Locale",     "vn");
        p.put("vnp_IpAddr",     req.getRemoteAddr());
        p.put("vnp_CreateDate", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        p.put("vnp_ReturnUrl",  VNP_RETURN_URL);
        p.put("vnp_BankCode",   "VNPAYQR");          // ép luồng QR
        return p;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            SortedMap<String, String> vnpParams = generateVnpParams(req);
            String paymentUrl = buildPaymentUrl(vnpParams);

            // URL tới servlet /qr để hiển thị ảnh QR
            String qrLink = req.getContextPath() + "/qr?url="
                    + URLEncoder.encode(paymentUrl, StandardCharsets.UTF_8);

            req.setAttribute("qrLink", qrLink);
            req.setAttribute("paymentUrl", paymentUrl);
            req.getRequestDispatcher("/qr.jsp").forward(req, resp);
        } catch (Exception ex) {
            throw new ServletException("Lỗi tạo QR thanh toán", ex);
        }
    }
}
