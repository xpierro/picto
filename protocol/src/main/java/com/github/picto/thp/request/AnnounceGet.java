package com.github.picto.thp.request;

import com.github.picto.network.http.exception.URLEncodeException;
import com.github.picto.network.http.RequestBuilder;
import com.github.picto.metainfo.model.IMetaInfo;
import com.github.picto.thp.exception.THPRequestException;
import com.github.picto.thp.model.AnnounceRequestModel;
import com.github.picto.thp.model.ThpAnnounceEvent;
import com.github.picto.thp.model.peerid.PeerId;
import com.github.picto.thp.model.peerid.StaticPeerId;
import com.github.picto.util.Hasher;
import com.github.picto.util.exception.HashException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Builds a GET request for an announce, from a MetaInfo file.
 * Created by Pierre on 29/08/15.
 */
public class AnnounceGet implements GetRequest{

    private AnnounceRequestModel request;

    private PeerId peerId;

    private IMetaInfo metaInfo;
    private String selectedAnnounceUri;
    private ThpAnnounceEvent event;
    private int port;

    //TODO: should be long
    private int uploaded;
    private int downloaded;
    private int left;
    private Boolean compact;
    private Boolean noPeerId;

    //TODO: ip for proxying
    private Integer peerWanted;
    private String key;
    private String trackerId;

    /**
     * Builds a request for the selected THP event.
     */
    public AnnounceGet() {
        request = new AnnounceRequestModel();
        compact = false;
        peerId = StaticPeerId.DEFAULT;
    }

    /**
     * Event commanding the request goal.
     * @param event An instance of the ThpAnnounceEvent enumeration
     */
    public AnnounceGet event(final ThpAnnounceEvent event) {
        this.event = event;
        return this;
    }

    /**
     * The torrent meta informations.
     * @param metaInfo The torrent meta informations
     */
    public AnnounceGet metaInfo(final IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        return this;
    }

    /**
     * The chosen announce URI amongst the ones in the metainfo
     * @param announceURI The chosen announce URI amongst the ones in the metainfo
     */
    public AnnounceGet announceURI(final String announceURI) {
        this.selectedAnnounceUri = announceURI;
        return this;
    }

    /**
     * True if we want a compact response from the tracker
     * @param compact True for a compact response, false otherwise (default: false)
     */
    public AnnounceGet compact(final boolean compact) {
        this.compact = compact;
        return this;
    }

    /**
     * The ID sent to the tracker to identify the current client.
     * @param peerId Static or dynamic peer id
     */
    public AnnounceGet peerId(final PeerId peerId) {
        this.peerId = peerId;
        return this;
    }

    /**
     * Port the client is listening to.
     * @param port The port (usually between 6881 and 6889) the client is listening to for new peer connections.
     */
    public AnnounceGet port(final int port) {
        this.port = port;
        return this;
    }

    public AnnounceGet uploaded(final int uploaded) {
        this.uploaded = uploaded;
        return this;
    }

    public AnnounceGet downloaded(final int downloaded) {
        this.downloaded = downloaded;
        return this;
    }

    public AnnounceGet left(final int left) {
        this.left = left;
        return this;
    }

    public AnnounceGet noPeerId(final boolean noPeerId) {
        this.noPeerId = noPeerId;
        return this;
    }

    public AnnounceGet peerWanted(final int peerWanted) {
        this.peerWanted = peerWanted;
        return this;
    }

    public AnnounceGet key(final String key) {
        this.key = key;
        return this;
    }

    public AnnounceGet trackerId(final String trackerId) {
        this.trackerId = trackerId;
        return this;
    }

    /**
     * Builds the request
     */
    public AnnounceGet build() throws HashException {
        request.setInfoHash(Hasher.sha1(metaInfo.getInformation().getBEncodeableDictionary().getBEncodedBytes()));
        request.setPeerId(peerId.getPeerIdBytes());
        request.setPort(port);
        request.setUploaded(uploaded);
        request.setDownloaded(downloaded);
        request.setLeft(left);
        request.setCompact(compact);
        request.setNoPeerId(noPeerId);
        request.setEvent(event);
        request.setNumWant(peerWanted);
        request.setKey(key);
        request.setTrackerId(trackerId);

        return this;

    }

    @Override
    public URI getUri() throws THPRequestException {
        if (metaInfo == null || selectedAnnounceUri == null || event == null) {
            throw new IllegalStateException("Immpossible to build the URI without all the arguments");
        }
        try {
            return new RequestBuilder().buildGetUri(request, selectedAnnounceUri);
        } catch (URLEncodeException | URISyntaxException e) {
            throw new THPRequestException("Immpossible to build the request.", e);
        }
    }
}
