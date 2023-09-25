package thedrake;

import java.io.PrintWriter;

public class Board implements JSONSerializable {
    private final int dimension;
    private final BoardTile[][] array;

    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
    public Board(int dimension) {
        this.dimension = dimension;
        this.array = new BoardTile[dimension][dimension];
        for (int i = 0; i < this.array.length; i++)
            for (int j = 0; j < this.array.length; j++)
                this.array[i][j] = BoardTile.EMPTY;
    }

    // Rozměr hrací desky
    public int dimension() {
        return this.dimension;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(TilePos pos) {
        return this.array[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {
        Board newBoard = new Board(this.dimension);
        for (int i = 0; i < this.dimension; i++)
            newBoard.array[i] = this.array[i].clone();
        for (TileAt tile : ats)
            newBoard.array[tile.pos.i()][tile.pos.j()] = tile.tile;
        return newBoard;
    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        return new PositionFactory(this.dimension);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"dimension\":%d,\"tiles\":[", this.dimension);
        int count = 0;

        for (int j = 0; j < this.array.length; ++j) {
            for (int i = 0; i < this.array[j].length; ++i) {
                ++count;
                this.array[i][j].toJSON(writer);
                if (count < this.dimension * this.dimension) {
                    writer.printf(",");
                }
            }
        }

        writer.printf("]}");
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }
}

