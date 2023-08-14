package io.ikeyit.xanthium.server;

public class ServerConfig {
    private int port = 11011;
    private String address;
    private int syncPort = 11012;
    private String syncAddress;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSyncPort() {
        return syncPort;
    }

    public void setSyncPort(int syncPort) {
        this.syncPort = syncPort;
    }

    public String getSyncAddress() {
        return syncAddress;
    }

    public void setSyncAddress(String syncAddress) {
        this.syncAddress = syncAddress;
    }
}
