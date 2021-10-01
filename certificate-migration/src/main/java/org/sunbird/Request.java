package org.sunbird;

public class Request {

    private String cassandraHost;
    private String cassandraKeyspaceName;
    private String cassandraPort;
    private String esConnection;
    private String date1;
    private String date2;
    private String certificateBasePath;

    public Request() {
    }

    public Request(
            String cassandraHost,
            String cassandraKeyspaceName,
            String cassandraPort,String esConnection, String date1, String date2, String certificateBasePath) {
        this.cassandraHost = cassandraHost;
        this.cassandraKeyspaceName = cassandraKeyspaceName;
        this.cassandraPort = cassandraPort;
        this.esConnection = esConnection;
        this.date1 = date1;
        this.date2 = date2;
    }

    public String getCassandraHost() {
        return cassandraHost;
    }

    public void setCassandraHost(String cassandraHost) {
        this.cassandraHost = cassandraHost;
    }

    public String getCassandraKeyspaceName() {
        return cassandraKeyspaceName;
    }

    public void setCassandraKeyspaceName(String cassandraKeyspaceName) {
        this.cassandraKeyspaceName = cassandraKeyspaceName;
    }

    public String getCassandraPort() {
        return cassandraPort;
    }

    public void setCassandraPort(String cassandraPort) {
        this.cassandraPort = cassandraPort;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getEsConnection() {
        return esConnection;
    }

    public void setEsConnection(String esConnection) {
        this.esConnection = esConnection;
    }

    public String getCertificateBasePath() {
        return certificateBasePath;
    }

    public void setCertificateBasePath(String certificateBasePath) {
        this.certificateBasePath = certificateBasePath;
    }

    @Override
    public String toString() {
        return "Request{" +
                "cassandraHost='" + cassandraHost + '\'' +
                ", cassandraKeyspaceName='" + cassandraKeyspaceName + '\'' +
                ", cassandraPort='" + cassandraPort + '\'' +
                ", esConnection='" + esConnection + '\'' +
                ", date1='" + date1 + '\'' +
                ", date2='" + date2 + '\'' +
                ", certificateBasePath='" + certificateBasePath + '\'' +
                '}';
    }
}
