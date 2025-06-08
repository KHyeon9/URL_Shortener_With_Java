package com.hyeon.url_shortener.web;

import com.hyeon.url_shortener.domain.exception.ShortUrlNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // url을 찾지 못한 경우 404 페이지로 이동
    @ExceptionHandler(ShortUrlNotFoundException.class)
    String handleShortUrlNotFoundException(ShortUrlNotFoundException ex) {
        log.error("Short URL을 찾지 못했습니다: {}", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    String handleException(Exception ex) {
        log.error("핸들링 되자않은 오류: {}", ex.getMessage());
        return "error/500";
    }
}
