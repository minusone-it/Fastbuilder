package it.minusone.fastbuilder.utils.api.mcserverping;

import it.minusone.fastbuilder.utils.utils.internet.dnsrecords.DNSRecord;

import java.util.List;

public class ServerPingResponse {
    private final String SRVHostname;
    private final int SRVPort;
    private final long ping;
    private final String hostname;
    private final int port;
    private final boolean srv;
    private final ServerPingJSONResponse response;
    private final List<DNSRecord> records;

    ServerPingResponse(String hostname, int port, String SRVHostname, int SRVPort, long ping, boolean srv, ServerPingJSONResponse response, List<DNSRecord> records) {
        this.SRVHostname = SRVHostname;
        this.SRVPort = SRVPort;
        this.hostname = hostname;
        this.port = port;
        this.ping = ping;
        this.srv = srv;
        this.response = response;
        this.records = records;
    }

}
