package com.github.picto.filesystem.event;

/**
 * Created by Pierre on 20/09/15.
 */
public class PieceValidationEvent {

    private final boolean valid;

    private final int pieceIndex;

    private PieceValidationEvent(boolean valid, int pieceIndex) {
        this.valid = valid;
        this.pieceIndex = pieceIndex;
    }

    public static PieceValidationEvent invalid(int pieceIndex) {
        return new PieceValidationEvent(false, pieceIndex);
    }

    public static PieceValidationEvent valid(int pieceIndex) {
        return new PieceValidationEvent(true, pieceIndex);
    }

    public boolean isValid() {
        return valid;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }
}
