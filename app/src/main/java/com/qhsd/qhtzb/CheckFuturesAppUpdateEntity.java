package com.qhsd.qhtzb;

/**
 * Created by 49829 on 2017/10/17.
 */

public class CheckFuturesAppUpdateEntity {
    /**
     * Result : true
     * Status : 1
     * Message : 保存成功！
     * InnerData : {"DownloadUrl":"https://mbbd-api.houputech.com/AppUpdate/LmAppDownload?package=qhtzb_v2.1_2017.10.16_huawei.apk","NeedUpdate":true}
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
         * DownloadUrl : https://mbbd-api.houputech.com/AppUpdate/LmAppDownload?package=qhtzb_v2.1_2017.10.16_huawei.apk
         * NeedUpdate : true
         */

        private String DownloadUrl;
        private boolean NeedUpdate;
        private String Content;

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public String getDownloadUrl() {
            return DownloadUrl;
        }

        public void setDownloadUrl(String DownloadUrl) {
            this.DownloadUrl = DownloadUrl;
        }

        public boolean isNeedUpdate() {
            return NeedUpdate;
        }

        public void setNeedUpdate(boolean NeedUpdate) {
            this.NeedUpdate = NeedUpdate;
        }
    }
}
