package com.rsupport.board.utils;

import com.rsupport.board.exception.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileUtil {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

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

    public static void checkValidFileSize(List<MultipartFile> multipartFileList){
        if(multipartFileList == null)
            return;

        long totalSize = multipartFileList.stream().mapToLong(MultipartFile::getSize).sum();

        if(totalSize > MAX_FILE_SIZE){
            throw new CustomException(ReturnCode.TOO_BIG_SIZE_FILE);
        }

        for(MultipartFile file : multipartFileList){

            if(file.getSize() < 1){
                throw new CustomException(ReturnCode.INVALID_REQUEST_PARAMETER);
            }
        }
    }
}
