package io.ikeyit.xanthium.server;

public class Response extends Message {
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
