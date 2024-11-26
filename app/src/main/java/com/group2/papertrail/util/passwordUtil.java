package com.group2.papertrail.util;

import org.mindrot.jbcrypt.BCrypt;

public class passwordUtil {
    public static String hashingPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
