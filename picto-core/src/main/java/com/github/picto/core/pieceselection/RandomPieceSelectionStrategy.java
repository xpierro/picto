package com.github.picto.core.pieceselection;

import java.util.BitSet;
import java.util.List;

/**
 * This selection strategy takes the first random non-downloaded piece that is present in the swarm.
 * Created by Pierre on 23/09/15.
 */
public class RandomPieceSelectionStrategy extends AbstractPieceSelectionStrategy {
    @Override
    public int getNextBestPiece(List<BitSet> swarmPieces, BitSet downloadedPieces) {
        return 0;
    }
}
