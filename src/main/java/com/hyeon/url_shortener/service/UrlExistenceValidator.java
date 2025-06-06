package com.hyeon.url_shortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UrlExistenceValidator {
    private static final Logger log = LoggerFactory.getLogger(UrlExistenceValidator.class);

    public static boolean isUrlExists(String urlString) {
        try {
            log.debug("URL이 존재하는지 확인했습니다: {}", urlString);
            // 먼저 URI로 감싸는 이유는 URL보다 더 엄격하게 문법을 검사하기 때문
            URL url = new URI(urlString).toURL();
            // 해당 URL에 대한 HTTP 연결 객체 생성
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // HEAD 요청은 GET 요청처럼 서버에 요청을 보내되, 본문(body)을 가져오지 않음
            // 즉, URL이 존재하는지만 확인할 수 있고, 속도도 더 빠릅니다.
            connection.setRequestMethod("HEAD");
            // 연결 타임아웃과 읽기 타임아웃을 설정
            connection.setConnectTimeout(5000); // 5초
            connection.setReadTimeout(5000);

            // http 응답 코드 가져옴
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 400; // 200 또는 300의 응답 코드일 경오 true
        } catch (Exception e) {
            log.error("URL을 확인하는 중에 오류가 발생했습니다: {}", urlString, e);
            return false;
        }
    }
}
