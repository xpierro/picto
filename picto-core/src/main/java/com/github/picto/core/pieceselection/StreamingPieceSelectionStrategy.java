package com.github.picto.core.pieceselection;

import java.util.BitSet;
import java.util.List;

/**
 * Created by Pierre on 23/09/15.
 */
public class StreamingPieceSelectionStrategy extends AbstractPieceSelectionStrategy {
    @Override
    public int getNextBestPiece(List<BitSet> swarmPieces, BitSet downloadingPieces) {
        int nextPieceIndex;
        int firstOkayPieceIndex = -1;
        while((nextPieceIndex = getNextNotDownloaded(downloadingPieces, 0)) != -1) {
            if (firstOkayPieceIndex == -1) {
                firstOkayPieceIndex = nextPieceIndex;
            }
            for (BitSet bitSet : swarmPieces) {
                if (bitSet.get(nextPieceIndex)) {
                    return nextPieceIndex;
                }
            }

        }
        return firstOkayPieceIndex;

    }
}
