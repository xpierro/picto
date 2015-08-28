package com.github.picto.metainfo.model;

import com.github.picto.bencode.BEncodedDictionary;

import java.util.List;

/**
 * Modelize a metainfo file.
 * A metainfo file can be serialized into a bencoded string.
 *
 * Created by Pierre on 24/08/15.
 */
public interface IMetaInfo extends BEncodedDictionary {
    /**
     * Returns the configuration information for the MetaInfo file.
     * @return A IMetaInformation representing the torrent configuration.
     */
    IMetaInfoInformation getInformation();

    void setInformation(final IMetaInfoInformation information);

    /**
     * The main announce url to fetch peer informations.
     * @return The main announce url.
     */
    String getAnnounce();

    void setAnnounce(final String announce);

    /**
     * A list of secondary announce url.
     * @return A list of url.
     */
    List<String> getAnnounces();

    void setAnnounces(final List<String> announces);

    /**
     * Creation date of the meta-info, in number of seconds since unix epoch.
     * @return A date in second-based unix epoch format.
     */
    int getCreationDate();

    void setCreationDate(final int creationDate);

    /**
     * A free-form comment from the meta-info creator.
     * @return An UTF-8 encoded String.
     */
    String getComment();

    void setComment(final String comment);

    /**
     * A free-form credit of the meta-info creator.
     * @return An UTF-8 encoded String.
     */
    String getCreatedBy();

    void setCreatedBy(final String createdBy);

    /**
     * Describes the encoding of the pieces.
     * TODO: unexplained
     * @return A String representing the encoding chosen for the pieces.
     */
    String getEncoding();

    void setEncoding(final String encoding);
}
