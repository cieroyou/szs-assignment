package com.sera.refund.common;


import java.util.Map;
import java.util.Set;

public class AllowedUsers {

    // 🔹 사전 등록된 사용자 (이름 -> 주민등록번호)
    private static final Map<String, String> ALLOWED_USERS = Map.of(
            "동탁", "921108-1582816",
            "관우", "681108-1582816",
            "손권", "890601-2455116",
            "유비", "790411-1656116",
            "조조", "810326-2715702"
    );


    // 🔹 이름과 주민등록번호가 일치하는지 확인
    public static boolean isAllowedUser(String name, String regNo) {
        return ALLOWED_USERS.containsKey(name) && ALLOWED_USERS.get(name).equals(regNo);
    }
}
