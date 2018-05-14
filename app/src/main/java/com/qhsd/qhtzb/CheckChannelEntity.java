package com.qhsd.qhtzb;

/**
 * Created by 49829 on 2017/9/27.
 */

public class CheckChannelEntity {

    /**
     * Result : true
     * Status : 1
     * Message : Success！
     * InnerData : {"Url":"http://futures3.houputech.com/app/index.html?v=20170606#/index"}
     * ResultCode : null
     */

    private boolean Result;
    private int Status;
    private String Message;
    private InnerDataBean InnerData;
    private Object ResultCode;

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean Result) {
        this.Result = Result;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public InnerDataBean getInnerData() {
        return InnerData;
    }

    public void setInnerData(InnerDataBean InnerData) {
        this.InnerData = InnerData;
    }

    public Object getResultCode() {
        return ResultCode;
    }

    public void setResultCode(Object ResultCode) {
        this.ResultCode = ResultCode;
    }

    public static class InnerDataBean {
        /**
         * Url : http://futures3.houputech.com/app/index.html?v=20170606#/index
         */

        private String Url;

        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url = Url;
        }
    }
}
