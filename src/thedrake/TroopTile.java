package thedrake;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TroopTile implements Tile, JSONSerializable {
    private final Troop troop;
    private final PlayingSide side;
    private final TroopFace face;

    // Konstruktor
    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    // Vrací barvu, za kterou hraje jednotka na této dlaždici
    public PlayingSide side() {
        return side;
    }

    // Vrací stranu, na kterou je jednotka otočena
    public TroopFace face() {
        return this.face;
    }

    // Jednotka, která stojí na této dlaždici
    public Troop troop() {
        return this.troop;
    }

    // Vrací False, protože na dlaždici s jednotkou se nedá vstoupit
    @Override
    public boolean canStepOn() {
        return false;
    }

    // Vrací True
    @Override
    public boolean hasTroop() {
        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<Move> movesFrom = new ArrayList<>();
        for (TroopAction action : this.troop.actions(this.face)) {
            movesFrom.addAll(action.movesFrom(pos, this.side, state));
        }
        return movesFrom;
    }

    // Vytvoří novou dlaždici, s jednotkou otočenou na opačnou stranu
    // (z rubu na líc nebo z líce na rub)
    public TroopTile flipped() {
        if (face.equals(TroopFace.AVERS))
            return new TroopTile(this.troop, this.side, TroopFace.REVERS);
        else
            return new TroopTile(this.troop, this.side, TroopFace.AVERS);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"troop\":");
        this.troop.toJSON(writer);
        writer.printf(",\"side\":");
        this.side.toJSON(writer);
        writer.printf(",\"face\":");
        this.face.toJSON(writer);
        writer.printf("}");
    }
}
