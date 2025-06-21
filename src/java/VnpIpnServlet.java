package com.vnpay;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/vnp_ipn")
public class VnpIpnServlet extends HttpServlet {

    private boolean verifySignature(Map<String, String> fields) throws Exception {
        String secureHash = fields.remove("vnp_SecureHash");
        String raw = new PaymentServlet().buildQuery(new TreeMap<>(fields));
        String sign = new PaymentServlet().hmacSHA512(
                PaymentServlet.VNP_HASH_SECRET, raw);
        return sign.equalsIgnoreCase(secureHash);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Map<String, String> fields = new TreeMap<>();
        req.getParameterMap().forEach((k, v) -> fields.put(k, v[0]));

        resp.setContentType("application/json");
        try {
            if (!verifySignature(fields)) {
                resp.getWriter().write("{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}");
                return;
            }

            // TODO: tra cứu đơn hàng theo vnp_TxnRef & kiểm tra số tiền
            if ("00".equals(fields.get("vnp_ResponseCode"))) {
                // Cập nhật đơn hàng thành công
                resp.getWriter().write("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            } else {
                // Giao dịch thất bại hoặc huỷ
                resp.getWriter().write("{\"RspCode\":\"00\",\"Message\":\"Confirm Failure\"}");
            }
        } catch (Exception e) {
            resp.getWriter().write("{\"RspCode\":\"99\",\"Message\":\"Unknown Error\"}");
        }
    }
}
