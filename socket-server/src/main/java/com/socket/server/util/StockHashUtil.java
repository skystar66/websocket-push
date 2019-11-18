package com.socket.server.util;

public class StockHashUtil {


    public static int getHashIndex(String str, int size) {

        if (str == null) {
            return 0;
        }

        // 转大写
        str = str.toUpperCase();

        //hash-取模
        int hash = str.hashCode();
        hash = Math.abs(hash);

        return hash % size;
    }


}
