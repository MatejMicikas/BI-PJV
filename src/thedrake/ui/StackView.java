package thedrake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import thedrake.*;

import java.util.List;

public class StackView extends GridPane implements TileViewContext {

    private GameState gameState;

    private PlayingSide playingSide;

    private BoardView boardView;

    public TileView selected;

    private ValidMoves validMoves;

    public StackView(GameState gameState, PlayingSide playingSide, BoardView boardView) {
        this.gameState = gameState;
        this.playingSide = playingSide;
        this.boardView = boardView;
        validMoves = new ValidMoves(gameState);

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int x = 0; x < gameState.army(playingSide).stack().size(); x++) {
            BoardPos boardPos = positionFactory.pos(x, 0);
            add(new TileView(new TroopTile(gameState.army(playingSide).stack().get(x), playingSide, TroopFace.AVERS), boardPos, this), x, 0);
        }

        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
    }

    public void updateStackTiles(GameState gameState) {
        this.gameState = gameState;
        validMoves = new ValidMoves(gameState);
        int stackSize = gameState.armyNotOnTurn().stack().size();

        if (selected != null) {
            selected.unselect();
            selected = null;
        }

        if (gameState.sideOnTurn() != playingSide) {
            TileView lastTileView = (TileView) getChildren().get(stackSize);
            if (!gameState.armyOnTurn().stack().isEmpty()) {
                for (int i = 0; i < stackSize; i++) {
                    TileView tileView = (TileView) getChildren().get(i);
                    tileView.setTile(new TroopTile(gameState.army(playingSide).stack().get(i), playingSide, TroopFace.AVERS));
                }
                lastTileView.setTile(BoardTile.EMPTY);
            } else lastTileView.setTile(BoardTile.EMPTY);
        }
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView)
            selected.unselect();

        if (playingSide != gameState.sideOnTurn())
            tileView.unselect();

        if (!tileView.position().equalsTo(0, 0))
            tileView.unselect();

        if (boardView.selected != null) {
            boardView.selected.unselect();
            boardView.selected = null;
            boardView.clearMoves();
        }

        selected = tileView;

        clearMoves();

        if (playingSide == gameState.sideOnTurn() && tileView.position().equalsTo(0, 0))
            showMoves(validMoves.movesFromStack());
    }

    @Override
    public void executeMove(Move move) {
    }

    private void clearMoves() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private void showMoves(List<Move> moves) {
        for (Move move : moves) {
            tileViewAt(move.target()).setMove(move);
        }
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (gameState.board().dimension() - 1 - target.j()) * 4 + target.i();
        return (TileView) boardView.getChildren().get(index);
    }
}
