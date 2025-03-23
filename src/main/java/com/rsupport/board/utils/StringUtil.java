package com.rsupport.board.utils;

import com.rsupport.board.exception.CustomException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StringUtil {

    public static String textToSha256(String input) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));  // 각 바이트를 두 자리 16진수로 변환
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void validOrderStartDateTimeEndDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime){
        if(!startDateTime.isBefore(endDateTime)){
            throw new CustomException(ReturnCode.NO_INVALID_ORDER_LOCAL_DATE_TIME);
        }
    }

    public static void validOrderStartDateEndDate(LocalDate startDateTime, LocalDate endDateTime){
        if(!startDateTime.isBefore(endDateTime)){
            throw new CustomException(ReturnCode.NO_INVALID_ORDER_LOCAL_DATE_TIME);
        }
    }

}
