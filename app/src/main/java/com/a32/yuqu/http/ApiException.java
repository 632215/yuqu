package com.a32.yuqu.http;

/**
 * Created by liukun on 16/3/10.
 */
public class ApiException extends RuntimeException {

    public static final int PARAMETERERROR = 100;
    public static final int SIGNATUREVERIFICATIONFAILED = 1001;
    public static final int COMMONPARAMETERERROR = 1002;
    public static final int SDKERROR = 1003;
    public static final int SMSFAILED = 1101;
    public static final int VERIFICATIONCODEERROR = 1102;
    public static final int MSMTOAST = 1103;
    public static final int TENMINUTES = 1104;
    public static final int LOCATIONOBJECTERROR = 1201;
    public static final int OWNERINFORMATIONINCORRECT = 1301;
    public static final int LOGINPLEASE = 2100;
    public static final int USERREGISTRATIONFAILED = 2101;
    public static final int USERSOURCEISILLEGAL = 2102;
    public static final int LANDINGROUTEILLEGALLAW = 2103;
    public static final int WRONG_PASSWORD = 2104;
    public static final int INTERNALAPICALLERROR = 3001;


    public ApiException(String state, String msg) {
        getApiExceptionMessage(msg);
    }


    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     *
     * @param msg
     * @return
     */
    private static void getApiExceptionMessage(String msg) {

    }

}

