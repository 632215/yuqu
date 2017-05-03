package com.a32.yuqu.http;

/**
 * Created by 32 on 17/3/5.
 */
public class HttpResult<T> {
    private String status;
    private String message ;
    private T data;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

//    @Override
//    public String toString() {
//        return "HttpResult{" +
//                "status=" + status +
//                ", message='" + message + '\'' +
//                ", data=" + data +
//                '}';
//    }
@Override
public String toString() {
    return "HttpResult{" +
            "status=" + status +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
}

}
