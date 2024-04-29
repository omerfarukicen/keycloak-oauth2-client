package com.demo.oauth2client.session;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Session Information
 */
@Getter
@Setter
@NoArgsConstructor
public class SessionInfo {
    private String adi;
    private String soyadi;
    private String email;
    private String kullaniciAdi;
    private Map<String, String> kullaniciOzellikleri;
    private Map<String, Map<String, String>> gruplar;
    private Map<String, Map<String, String>> islemGruplari;

    @Override
    public String toString() {
        return "SessionInfo{" +
                "adi='" + adi + '\'' +
                ", soyadi='" + soyadi + '\'' +
                ", email='" + email + '\'' +
                ", kullaniciAdi='" + kullaniciAdi + '\'' +
                ", kullaniciOzellikleri=" + kullaniciOzellikleri +
                ", gruplar=" + gruplar +
                ", islemGruplari=" + islemGruplari +
                '}';
    }

    public String sessionShortInfo() {
        return "{" +
                "adi='" + adi + '\'' +
                ", soyadi='" + soyadi + '\'' +
                ", kullaniciAdi='" + kullaniciAdi + '\'' +
                '}';
    }
}
