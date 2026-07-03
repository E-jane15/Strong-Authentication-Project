package com.example.authenticationsystem.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {

    public String generateQrCode(String username, String secret) {

        try {

            String otpAuthUrl = String.format(
                    "otpauth://totp/AuthenticationSystem:%s?secret=%s&issuer=AuthenticationSystem",
                    username,
                    secret
            );

            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(
                    otpAuthUrl,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    matrix,
                    "PNG",
                    stream
            );

            return Base64.getEncoder().encodeToString(stream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}