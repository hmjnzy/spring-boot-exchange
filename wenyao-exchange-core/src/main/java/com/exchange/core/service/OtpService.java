package com.exchange.core.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class OtpService {

    private GoogleAuthenticator googleAuthenticator;

    @PostConstruct
    public void init() {
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    public String createSecretKey() {
        return this.googleAuthenticator.createCredentials().getKey();
    }

    public Boolean isOtpValid(final String secretKey, final int otpCode) {
        return this.googleAuthenticator.authorize(secretKey, otpCode);
    }

    public String getQRBarcodeURL(final String email, final String secretKey) {
        /*
        * issuer=WY.TRADE
        * */
        final String format = "otpauth://totp/%s?secret=%s&issuer=WY.TRADE";
        return String.format(format, email, secretKey);
    }

}
