package com.nokiatest.model;


import java.util.ArrayList;
import java.util.List;

public class UeAppData {

    private String cloudApplicationName;

    private String ipECA;
    private String ipCDN;
    private String applicationType;
    private List<ListContentFile> listContentFiles = new ArrayList<>();

    public String getIpECA() {
        return ipECA;
    }

    public void setIpECA(String ipECA) {
        this.ipECA = ipECA;
    }

    public String getIpCDN() {
        return ipCDN;
    }

    public void setIpCDN(String ipCDN) {
        this.ipCDN = ipCDN;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }


    public String getCloudApplicationName() {
        return cloudApplicationName;
    }

    public void setCloudApplicationName(String cloudApplicationName) {
        this.cloudApplicationName = cloudApplicationName;
    }

    public class ListContentFile {
        private String edgeUrl;
        private String internetUrl;

        public String getEdgeUrl() {
            return edgeUrl;
        }

        public void setEdgeUrl(String edgeUrl) {
            this.edgeUrl = edgeUrl;
        }

        public String getInternetUrl() {
            return internetUrl;
        }

        public void setInternetUrl(String internetUrl) {
            this.internetUrl = internetUrl;
        }

    }

}





