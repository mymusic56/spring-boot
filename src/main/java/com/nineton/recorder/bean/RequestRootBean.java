package com.nineton.recorder.bean;

public class RequestRootBean {

    private String status;
    private String msg;
    private ResultBean result;
    
    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setMsg(String msg) {
         this.msg = msg;
     }
     public String getMsg() {
         return msg;
     }

    public void setResult(ResultBean result) {
         this.result = result;
     }
     public ResultBean getResult() {
         return result;
     }

}