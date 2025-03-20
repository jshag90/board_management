package com.rsupport.board.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static void responseFileDownload(HttpServletResponse response, String fileName, byte[] fileData) {
        // 파일명 인코딩 (한글 파일명 깨짐 방지)
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        // 응답 헤더 설정
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        response.setContentLength(fileData.length);

        // 파일 데이터 전송
        try (OutputStream os = response.getOutputStream()) {
            os.write(fileData);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
