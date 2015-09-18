package com.github.picto.protocol.metainfo.model;

import com.github.picto.bencode.AbstractBEncodedDictionary;
import com.github.picto.bencode.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the IMetaInfo interface.
 *
 * Created by Pierre on 24/08/15.
 */
@BEncodeDictionary(type = MetaInfo.class)
public class MetaInfo extends AbstractBEncodedDictionary implements IMetaInfo {

    protected IMetaInfoInformation metaInfoInformation;

    protected String announce;

    protected List<String> announces;

    protected int creationDate;

    protected String comment;

    protected String createdBy;

    protected String encoding;

    @BEncodeDictionary(name = "info", type = MetaInfoInformation.class)
    @Override
    public IMetaInfoInformation getInformation() {
        return metaInfoInformation;
    }

    @Override
    public void setInformation(IMetaInfoInformation information) {
        this.metaInfoInformation = information;
    }

    @BEncodeByteArray(name = "announce")
    @Override
    public String getAnnounce() {
        return announce;
    }

    @Override
    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    @Override
    public List<String> getAnnounces() {
        return announces;
    }

    @BEncodeList(name = "announce-list", elementType = List.class, innerList = {@BEncodeInnerList(elementType = String.class)})
    public void setAnnounceList(final List<List<String>> announcesEmbeded) {
        this.announces = new ArrayList<>();
        announcesEmbeded.forEach(this.announces::addAll);
    }

    @Override
    public void setAnnounces(List<String> announces) {
        this.announces = announces;
    }

    @BEncodeInteger(name = "creation date")
    @Override
    public int getCreationDate() {
        return creationDate;
    }

    /**
     * Overload the long method for marshalling purposes.
     * @param creationDate The int representation of the creation date.
     */
    @Override
    public void setCreationDate(final int creationDate) {
        this.creationDate = creationDate;
    }

    @BEncodeByteArray(name = "comment")
    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(final String comment) {
        this.comment = comment;
    }

    @BEncodeByteArray(name = "created by")
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @BEncodeByteArray(name = "encoding")
    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }


}
