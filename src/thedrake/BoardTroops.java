package thedrake;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements JSONSerializable {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        this.playingSide = playingSide;
        this.troopMap = Collections.emptyMap();
        this.leaderPosition = TilePos.OFF_BOARD;
        this.guards = 0;
    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        if (troopMap.get(pos) != null)
            return Optional.of(troopMap.get(pos));
        else
            return Optional.empty();
    }

    public PlayingSide playingSide() {
        return this.playingSide;
    }

    public TilePos leaderPosition() {
        return this.leaderPosition;
    }

    public int guards() {
        return this.guards;
    }

    public boolean isLeaderPlaced() {
        return this.leaderPosition != TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        return isLeaderPlaced() && this.guards < 2;
    }

    public Set<BoardPos> troopPositions() {
        return troopMap.keySet();
    }

    public BoardTroops placeTroop(Troop troop, BoardPos target) {
        if (at(target).isPresent())
            throw new IllegalStateException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = new TroopTile(troop, this.playingSide, TroopFace.AVERS);
        newTroops.put(target, tile);

        if (!isLeaderPlaced()) {
            return new BoardTroops(this.playingSide, newTroops, target, this.guards);
        } else if (this.guards < 2) {
            return new BoardTroops(this.playingSide, newTroops, this.leaderPosition, this.guards + 1);
        } else {
            return new BoardTroops(this.playingSide, newTroops, this.leaderPosition, this.guards);
        }
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        if (!isLeaderPlaced() || isPlacingGuards())
            throw new IllegalStateException();

        if (!at(origin).isPresent() || at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.get(origin);
        newTroops.remove(origin);
        newTroops.put(target, tile.flipped());

        if (this.leaderPosition.equalsTo(origin.i(), origin.j()))
            return new BoardTroops(this.playingSide, newTroops, target, this.guards);
        else
            return new BoardTroops(this.playingSide, newTroops, this.leaderPosition, this.guards);
    }

    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {
        if (!isLeaderPlaced() || isPlacingGuards())
            throw new IllegalStateException();

        if (!at(target).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        newTroops.remove(target);

        if (this.leaderPosition.equalsTo(target.i(), target.j()))
            return new BoardTroops(this.playingSide, newTroops, TilePos.OFF_BOARD, this.guards);
        else
            return new BoardTroops(this.playingSide, newTroops, this.leaderPosition, this.guards);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"side\":");
        this.playingSide.toJSON(writer);
        writer.printf(",\"leaderPosition\":");
        this.leaderPosition.toJSON(writer);
        writer.printf(",\"guards\":%s,\"troopMap\":", this.guards);
        this.troopMapToJSON(writer);
        writer.printf("}");
    }

    private void troopMapToJSON(PrintWriter writer) {
        writer.printf("{");
        int count = 0;

        List<BoardPos> list = new ArrayList<>(troopMap.keySet());
        list.sort((o1, o2) -> CharSequence.compare(o1.toString(), o2.toString()));

        while (count < list.size()) {
            BoardPos pos = list.get(count);
            ++count;
            pos.toJSON(writer);
            writer.printf(":");
            this.troopMap.get(pos).toJSON(writer);
            if (count < this.troopMap.size()) {
                writer.printf(",", pos.column(), pos.row());
            }
        }

        writer.printf("}");
    }
}
