package model;

public class Session {

    String id;

    String ipAddress;

    long since;

    public Session(String id, String ipAddress, long since) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.since = since;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public long getSince() {
        return since;
    }
}
