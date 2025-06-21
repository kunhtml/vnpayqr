package com.vnpay;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/vnp_return")
public class VnpReturnServlet extends HttpServlet {

    private boolean verifySignature(Map<String, String> fields) throws Exception {
        String secureHash = fields.remove("vnp_SecureHash");
        String raw = new PaymentServlet().buildQuery(new TreeMap<>(fields));
        String sign = new PaymentServlet().hmacSHA512(
                PaymentServlet.VNP_HASH_SECRET, raw);
        return sign.equalsIgnoreCase(secureHash);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Map<String, String> fields = new TreeMap<>();
        req.getParameterMap().forEach((k, v) -> fields.put(k, v[0]));

        boolean valid;
        try { valid = verifySignature(fields); } catch (Exception e) { valid = false; }

        if (valid && "00".equals(fields.get("vnp_ResponseCode"))) {
            // TODO: update DB đơn hàng thành công
            req.setAttribute("status", "success");
        } else {
            req.setAttribute("status", "failed");
        }
        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }
}
