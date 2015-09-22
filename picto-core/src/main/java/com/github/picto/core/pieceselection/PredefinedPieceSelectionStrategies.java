package com.github.picto.core.pieceselection;

import java.util.BitSet;
import java.util.List;

/**
 * TODO: better architecture
 * Created by Pierre on 23/09/15.
 */
public enum PredefinedPieceSelectionStrategies implements IPieceSelectionStrategy {
    RANDOM() {
        @Override
        public int getNextBestPiece(List<BitSet> swarmPieces, BitSet downloadedPieces) {
            return new RandomPieceSelectionStrategy().getNextBestPiece(swarmPieces, downloadedPieces);
        }
    },
    RAREST_FIRST() {
        @Override
        public int getNextBestPiece(List<BitSet> swarmPieces, BitSet downloadedPieces) {
            return new RandomPieceSelectionStrategy().getNextBestPiece(swarmPieces, downloadedPieces);
        }
    },
    STREAMING() {
        @Override
        public int getNextBestPiece(List<BitSet> swarmPieces, BitSet downloadedPieces) {
            return new StreamingPieceSelectionStrategy().getNextBestPiece(swarmPieces, downloadedPieces);
        }
    }
}
