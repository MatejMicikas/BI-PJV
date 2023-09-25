package thedrake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import thedrake.*;

public class CaptureView extends GridPane implements TileViewContext {

    public GameState gameState;

    public PlayingSide playingSide;

    public CaptureView(GameState gameState, PlayingSide playingSide) {
        this.gameState = gameState;
        this.playingSide = playingSide;

        setHgap(5);
        setVgap(5);
        setMinWidth(200);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_CENTER);
    }

    public void capture(GameState gameState) {
        PositionFactory positionFactory = gameState.board().positionFactory();
        if (!gameState.armyNotOnTurn().captured().isEmpty() && gameState.armyNotOnTurn().side() == playingSide) {
            for (int y = 0; y < gameState.armyNotOnTurn().captured().size(); y++) {
                BoardPos boardPos = positionFactory.pos(0, y);
                add(new TileView(new TroopTile(gameState.armyNotOnTurn().captured().get(y), gameState.armyOnTurn().side(), TroopFace.AVERS), boardPos, this), 0, y);
            }
        }
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        tileView.unselect();
    }

    @Override
    public void executeMove(Move move) {
    }
}
