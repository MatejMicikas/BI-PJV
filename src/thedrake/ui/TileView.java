package thedrake.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import thedrake.BoardPos;
import thedrake.Move;
import thedrake.Tile;

public class TileView extends Pane {

    private final TileBackgrounds tileBackgrounds = new TileBackgrounds();

    private final Border selectionBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3)));

    private final ImageView moveImage;

    private Tile tile;

    private final BoardPos position;

    private final TileViewContext context;

    private Move move;

    public TileView(Tile tile, BoardPos position, TileViewContext context) {
        this.tile = tile;
        this.position = position;
        this.context = context;
        setPrefSize(100, 100);

        update();

        setOnMouseClicked(e -> onclick());

        moveImage = new ImageView(getClass().getResource("/assets/move.png").toString());
        moveImage.setVisible(false);
        getChildren().add(moveImage);
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        update();
    }

    private void onclick() {
        if (move != null)
            context.executeMove(move);
        else if (tile.hasTroop())
            select();
    }

    private void select() {
        setBorder(selectionBorder);
        context.tileViewSelected(this);
    }

    public void unselect() {
        setBorder(null);
    }

    private void update() {
        setBackground(tileBackgrounds.get(tile));
    }

    public BoardPos position() {
        return position;
    }

    public void setMove(Move move) {
        this.move = move;
        moveImage.setVisible(true);
    }

    public void clearMove() {
        this.move = null;
        moveImage.setVisible(false);
    }
}
