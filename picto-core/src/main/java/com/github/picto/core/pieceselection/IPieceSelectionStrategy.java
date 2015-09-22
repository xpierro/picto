package com.github.picto.core.pieceselection;

import java.util.BitSet;
import java.util.List;

/**
 * A piece selection strategy has to give the next best piece to download, given all the known piece status
 * in the swarm and our own currently owned pieces.
 * Created by Pierre on 23/09/15.
 */
public interface IPieceSelectionStrategy {

    int getNextBestPiece(List<BitSet> swarmPieces, BitSet downloadedPieces);
}
