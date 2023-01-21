package io.extact.msa.rms.platform.fw.external.jwt;

import org.apache.commons.lang3.tuple.Pair;

/**
 * サーバから発行された認証トークンを受信したことを通知するイベント
 */
public class JwtRecieveEvent {

    /** サーバから受信したヘッダ(valueは受信時の生情報を入れているのでbearerも付いている) */
    private Pair<String, String> jwtHeader;

    public JwtRecieveEvent(Pair<String, String> jwtHeader) {
        this.jwtHeader = jwtHeader;
    }

    public Pair<String, String> getJwtHeader() {
        return jwtHeader;
    }
}
