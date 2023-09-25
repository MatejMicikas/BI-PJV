package thedrake;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import static thedrake.GameResult.IN_PLAY;

public class GameState implements JSONSerializable {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if (army(PlayingSide.ORANGE).boardTroops().troopPositions().contains(pos))
            return army(PlayingSide.ORANGE).boardTroops().at(pos).get();

        if (army(PlayingSide.BLUE).boardTroops().troopPositions().contains(pos))
            return army(PlayingSide.BLUE).boardTroops().at(pos).get();

        return board.at(pos);
    }

    private boolean canStepFrom(TilePos origin) {

        if (origin.equals(TilePos.OFF_BOARD))
            return false;

        if (!result.equals(GameResult.IN_PLAY))
            return false;

        if (board.at(origin).hasTroop())
            return false;

        if (army(sideOnTurn).boardTroops().at(origin).isPresent() &&
                army(sideOnTurn).boardTroops().isLeaderPlaced() &&
                !army(sideOnTurn).boardTroops().isPlacingGuards() &&
                this.sideOnTurn == army(sideOnTurn).side())
            return true;

        return false;
    }

    private boolean canStepTo(TilePos target) {
        if (!result.equals(GameResult.IN_PLAY)) return false;
        if (target.equals(TilePos.OFF_BOARD)) return false;

        return tileAt(target).canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        if (!result.equals(GameResult.IN_PLAY)) return false;
        if (!armyNotOnTurn().boardTroops().at(target).isPresent()) return false;
        if (target.equals(TilePos.OFF_BOARD)) return false;

        return true;
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if (!result.equals(GameResult.IN_PLAY)) return false;
        if (armyOnTurn().stack().isEmpty()) return false;
        if (!canStepTo(target)) return false;

        if (!armyOnTurn().boardTroops().isLeaderPlaced()) {
            if (armyOnTurn().side().equals(PlayingSide.BLUE)) {
                return target.row() == 1;
            } else {
                return target.row() == this.board.dimension();
            }
        }

        if (armyOnTurn().boardTroops().isPlacingGuards()) {
            for (TilePos neighbour : armyOnTurn().boardTroops().leaderPosition().neighbours()) {
                if (neighbour.equalsTo(target.i(), target.j()))
                    return true;
            }
            return false;
        }

        if (!armyOnTurn().stack().isEmpty()) {
            for (BoardPos troop : armyOnTurn().boardTroops().troopPositions()) {
                for (TilePos neighbour : troop.neighbours()) {
                    if (neighbour.equalsTo(target.i(), target.j()))
                        return true;
                }
            }
        }

        return false;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"result\":");
        this.result.toJSON(writer);
        writer.printf(",\"board\":");
        this.board.toJSON(writer);
        writer.printf(",\"blueArmy\":");
        this.blueArmy.toJSON(writer);
        writer.printf(",\"orangeArmy\":");
        this.orangeArmy.toJSON(writer);
        writer.printf("}");
    }
}