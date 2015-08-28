package com.github.picto.thp.model;

import com.github.picto.httputil.annotation.GET;
import com.github.picto.httputil.annotation.URLEncode;
import com.github.picto.httputil.stringify.BooleanStringifier;
import com.github.picto.httputil.stringify.HashStringifier;

/**
 * Model of a request sent to a tracker announce url for information about a torrent.
 * Created by Pierre on 27/08/15.
 */
@GET
public class AnnounceRequestModel {

    private byte[] infoHash;

    private byte[] peerId;

    private Integer port;

    private Integer uploaded;

    private Integer downloaded;

    private Integer left;

    private Boolean compact;

    private Boolean noPeerId;

    private ThpAnnounceEvent event;

    private String ip;

    private Integer numWant;

    private String key;

    private String trackerId;

    public AnnounceRequestModel() {
    }

    /**
     * 20-bytes SHA1 hash of the value of the info key from the Metainfo file. The value will be a bencoded dictionary
     * and this method returns it's SHA1 hash.
     * Must be formed as such \x10\xAB...
     * @return A SHA1 hash of the bencoded info dictionary.
     */
    @URLEncode(name = "info_hash", stringify = HashStringifier.class, raw = true)
    public byte[] getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(byte[] infoHash) {
        this.infoHash = infoHash;
    }

    /**
     * Unique client signature. No particular protocol are defined to generate it, and has to be unique during runtime,
     * and considered random.
     * @return A random, runtime-unique, 20-byte long, string identifier.
     */
    @URLEncode(name = "peer_id", stringify = HashStringifier.class, raw = true)
    public byte[] getPeerId() {
        return peerId;
    }

    public void setPeerId(final byte[] peerId) {
        if (peerId.length != 20) {
            throw new IllegalStateException("Immpossible to use a peer id that is not 20 bytes long");
        }
        this.peerId = peerId;
    }

    /**
     * The port the client is listening to. Reserved bittorrent ports are usually in the 6881-6889 range.
     * @return A port the client is listening to.
     */
    @URLEncode(name = "port")
    public Integer getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * The total amount uploaded by the client, as a number ASCII String.
     * @return An ASCII String representing the number of bytes already uploaded.
     */
    @URLEncode(name = "uploaded")
    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * The total amount downloaded by the client, as a number ASCII String.
     * @return An ASCII String representing the number of bytes already downloaded.
     */
    @URLEncode(name = "downloaded")
    public Integer getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Integer downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * The amount left to download by the client to have a 100% completion and all files, as a number ASCII String.
     * @return An ASCII String representing the number of bytes already downloaded.
     */
    @URLEncode(name = "left")
    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    /**
     * Accepts compact response. Instead of returning a peer list, the tracker can send a peers byte array with 6 bytes
     * per peer, the first four being the host in network byte order, the last two are the port in network byte order.
     * Some trackers only support this way.
     * @return True if we wish to have a compact response, false otherwise.
     */
    @URLEncode(name = "compact", stringify = BooleanStringifier.class)
    public Boolean isCompact() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    /**
     * Indicates that the tracker can omit peer id field in returned peer dictionary. This option is ignored if
     * compact is enabled.
     * @return True if we wish to omit peer id field in returned peer dictionary.
     */
    @URLEncode(name = "no_peer_id", stringify = BooleanStringifier.class)
    public Boolean isNoPeerId() {
        return noPeerId;
    }

    public void setNoPeerId(final Boolean noPeerId) {
        this.noPeerId = noPeerId;
    }

    /**
     * The event code defining this request goal.
     * @return A THP Event code.
     */
    @URLEncode(name = "event")
    public ThpAnnounceEvent getEvent() {
        return event;
    }

    public void setEvent(final ThpAnnounceEvent event) {
        this.event = event;
    }

    /**
     * Optional field indicating the ip address of the client. Solely needed if:
     *   - The client is connecting through a proxy
     *   - The client and tracker are on the same NAT subnet (in which case the ip address to give to external peers
     *   cannot be a subnet one, as it would be unreacheable)
     * @return
     */
    @URLEncode(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Optional field indicating the number of peer desired in the response. Can be 0. If ommited, typically default
     * to 50 peers.
     * @return Null if default value desired, the number of desired peer otherwise.
     */
    @URLEncode(name = "numwant")
    public Integer getNumWant() {
        return numWant;
    }

    public void setNumWant(Integer numWant) {
        this.numWant = numWant;
    }

    /**
     * Additional internal client identification mechanism, for the tracker to track specific client statistics
     * (usually private trackers). Will never be sent to other peers.
     * @return A unique tracker-specific identifier of the client.
     */
    @URLEncode(name = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * If previous announce responses contained a tracker id, it should be set for further requests.
     * @return A tracker id previously provided by the tracker.
     */
    @URLEncode(name = "trackerid")
    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }
}
