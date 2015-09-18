package com.github.picto.protocol.thp.model;

import com.github.picto.bencode.BEncodedDictionary;
import com.github.picto.bencode.annotation.BEncodeByteArray;
import com.github.picto.bencode.annotation.BEncodeDictionary;
import com.github.picto.bencode.annotation.BEncodeInteger;
import com.github.picto.bencode.type.BEncodeableDictionary;
import com.github.picto.protocol.pwp.model.Peer;
import com.google.inject.Provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a tracker response.
 * Created by Pierre on 31/08/15.
 */
@BEncodeDictionary(type = TrackerAnnounceResponseModel.class)
public class TrackerAnnounceResponseModel implements BEncodedDictionary {

    private String failureReason;
    private String warningMessage;

    private Integer interval;
    private Integer minInterval;

    private String trackerId;

    private Integer complete;
    private Integer incomplete;

    private byte[] binaryPeers;

    private BEncodeableDictionary dictionary;

    public TrackerAnnounceResponseModel() {

    }

    /**
     * Optional String, must be the only value if present. Gives a human readeable reason as to why the request failed.
     * TODO: this is a special case and won't be bencoded.
     * @return A failure String.
     */
    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * New. Similar to failure but will be processed normally.
     * @return
     */
    @BEncodeByteArray(name = "warning message")
    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    /**
     * Recommended request interval.
     * @return A number of seconds.
     */
    @BEncodeInteger(name = "interval")
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    /**
     * Mandatory minimal request interval. Must never be broken.
     * @return
     */
    @BEncodeInteger(name = "min interval")
    public Integer getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(Integer minInterval) {
        this.minInterval = minInterval;
    }

    /**
     * A String the client must send back for any subsequent requests.
     * @return A client identifier for the tracker.
     */
    @BEncodeByteArray(name = "tracker id")
    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    /**
     * The number of active seeders.
     * @return The number of seeds sharing the file.
     */
    @BEncodeInteger(name = "complete")
    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    /**
     * The number of active leechers.
     * @return The number of leechers sharing the file.
     */
    @BEncodeInteger(name = "incomplete")
    public Integer getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(Integer incomplete) {
        this.incomplete = incomplete;
    }

    /**
     * TODO: if compact if specified, we receive the binary model. If not, we receive the dictionary. How
     * TODO: to configure the unserializer to understand this nuancy, for the SAME attribute ?
     * Byte array containing a multiple of 6-byte client addresses. For each group of 6 bytes, the first 4 are the
     * ip address in network byte order, the 2 last are the port in network byte order.
     * @return An array of peer addresses.
     */
    @BEncodeByteArray(name = "peers")
    public byte[] getBinaryPeers() {
        return binaryPeers;
    }

    public void setBinaryPeers(byte[] binaryPeers) {
        this.binaryPeers = binaryPeers;
    }

    /**
     * Get the list of peers on the network sent by the server.
     * @return A list of simple peer objects.
     */
    public List<Peer> getPeers(final Provider<Peer> peerProvider) {
        List<Peer> peers = new ArrayList<>();
        if (binaryPeers != null && binaryPeers.length > 0) {
            if (binaryPeers.length % 6 != 0) {
                throw new IllegalStateException("Peers should be represented by groups of 6 bytes");
            }
            for (int i = 0; i < binaryPeers.length; i+= 6) {
                byte[] ipBytes = Arrays.copyOfRange(binaryPeers, i, i + 4);
                byte[] portBytes = Arrays.copyOfRange(binaryPeers, i + 4, i + 6);

                int port = 0;
                port |= portBytes[0] & 0xFF;
                port <<= 8;
                port |= portBytes[1] & 0xFF;

                Peer peer = peerProvider.get();
                try {
                    peer.setHost(InetAddress.getByAddress(ipBytes));
                    peer.setPort(port);
                    peers.add(peer);
                } catch (UnknownHostException e) {
                    //TODO: shouldnt fail too hard on this.
                    throw new IllegalStateException("The peer ip address is invalid");
                }
            }
        }
        return peers;
    }

    @Override
    public BEncodeableDictionary getBEncodeableDictionary() {
        return dictionary;
    }

    @Override
    public void setBEncodeableDictionary(final BEncodeableDictionary dictionary) {
        this.dictionary = dictionary;
    }
}
