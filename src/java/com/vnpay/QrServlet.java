package com.vnpay;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/qr")
public class QrServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String encodedUrl = req.getParameter("url");
        if (encodedUrl == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing url");
            return;
        }
        // Giải mã, vì tham số url đã được encode 1 lần
        String url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);

        try {
            BitMatrix matrix = new QRCodeWriter()
                    .encode(url, BarcodeFormat.QR_CODE, 300, 300);
            resp.setContentType("image/png");
            MatrixToImageWriter.writeToStream(matrix, "PNG", resp.getOutputStream());
        } catch (Exception e) {
            throw new ServletException("Cannot generate QR", e);
        }
    }
}
