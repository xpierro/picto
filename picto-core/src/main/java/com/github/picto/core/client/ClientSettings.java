package com.github.picto.core.client;

import com.github.picto.core.pieceselection.IPieceSelectionStrategy;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Setting builders for the client.
 * Created by Pierre on 22/09/15.
 */
public class ClientSettings {
    private int maxConnections;

    private InputStream metainfoSource;

    private Path basePath;

    private IPieceSelectionStrategy pieceSelectionStrategy;

    public static ClientSettings settingsBuilder() {
        return new ClientSettings();
    }

    private ClientSettings() {
        maxConnections = Client.DEFAULT_MAX_CONNECTION;
    }

    public ClientSettings maxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public ClientSettings metainfoSource(InputStream metainfoSource) {
        this.metainfoSource = metainfoSource;
        return this;
    }

    public ClientSettings basePath(Path basePath) {
        this.basePath = basePath;
        return this;
    }

    public ClientSettings pieceSelectionStrategy(IPieceSelectionStrategy pieceSelectionStrategy) {
        this.pieceSelectionStrategy = pieceSelectionStrategy;
        return this;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public InputStream getMetainfoSource() {
        return metainfoSource;
    }

    public Path getBasePath() {
        return basePath;
    }

    public IPieceSelectionStrategy getPieceSelectionStrategy() {
        return pieceSelectionStrategy;
    }
}
