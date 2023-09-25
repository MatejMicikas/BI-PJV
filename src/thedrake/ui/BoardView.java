package thedrake.ui;

import client.ClientApplication;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import thedrake.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BoardView extends GridPane implements TileViewContext {

    private GameState gameState;

    private final Stage stage;

    public TileView selected;

    private ValidMoves validMoves;

    public StackView stackViewBlueArmy;

    public StackView stackViewOrangeArmy;

    public CaptureView captureViewBlueArmy;

    public CaptureView captureViewOrangeArmy;

    public BoardView(GameState gameState, Stage stage) {
        this.gameState = gameState;
        this.stage = stage;
        validMoves = new ValidMoves(gameState);

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                BoardPos boardPos = positionFactory.pos(x, positionFactory.dimension() - y - 1);
                add(new TileView(gameState.tileAt(boardPos), boardPos, this), x, y);
            }
        }

        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
    }

    public void updateTiles() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.setTile(gameState.tileAt(tileView.position()));
        }

        if (gameState.sideOnTurn() == PlayingSide.BLUE) {
            stackViewBlueArmy.setStyle("-fx-background-color: #d8fff9");
            stackViewOrangeArmy.setStyle("-fx-background-color: white");
        } else {
            stackViewOrangeArmy.setStyle("-fx-background-color: #ffe4d4");
            stackViewBlueArmy.setStyle("-fx-background-color: white");
        }
    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView)
            selected.unselect();

        if (stackViewBlueArmy.selected != null) {
            stackViewBlueArmy.selected.unselect();
            stackViewBlueArmy.selected = null;
        } else if (stackViewOrangeArmy.selected != null) {
            stackViewOrangeArmy.selected.unselect();
            stackViewOrangeArmy.selected = null;
        }

        selected = tileView;

        clearMoves();
        showMoves(validMoves.boardMoves(tileView.position()));
    }

    @Override
    public void executeMove(Move move) {
        if (selected != null) {
            selected.unselect();
            selected = null;
        }

        clearMoves();

        gameState = move.execute(gameState);
        validMoves = new ValidMoves(gameState);

        updateTiles();
        stackViewBlueArmy.updateStackTiles(gameState);
        stackViewOrangeArmy.updateStackTiles(gameState);
        captureViewBlueArmy.capture(gameState);
        captureViewOrangeArmy.capture(gameState);

        if (gameState.result().equals(GameResult.VICTORY))
            GameOver(stage);

        if (gameState.armyNotOnTurn().boardTroops().isLeaderPlaced() && gameState.armyNotOnTurn().boardTroops().isPlacingGuards()) {
            if (validMoves.allMoves().size() == 1 && gameState.armyOnTurn().boardTroops().guards() == 0)
                GameOver(stage);
        }
    }

    private void showMoves(List<Move> moves) {
        for (Move move : moves) {
            tileViewAt(move.target()).setMove(move);
        }
    }

    public void clearMoves() {
        for (Node node : getChildren()) {
            TileView tileView = (TileView) node;
            tileView.clearMove();
        }
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (gameState.board().dimension() - 1 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);
    }

    public void GameOver(Stage stage) {
        Color customColor = new Color(188, 219, 213, 255);
        JFrame frame = new JFrame("Konec hry!");
        frame.setSize(480, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        setLocationToCenter(frame);

        JPanel panel = new JPanel();
        frame.add(panel);

        panel.setBackground(customColor);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel txtField = new JLabel();
        txtField.setHorizontalAlignment(SwingConstants.CENTER);
        txtField.setFont(new Font("Lato", Font.PLAIN, 20));
        txtField.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        if (gameState.armyNotOnTurn().side() == PlayingSide.BLUE) {
            txtField.setText("Blue Army wins!");
            txtField.setForeground(Color.BLUE);
        } else {
            txtField.setText("Orange Army wins!");
            txtField.setForeground(Color.RED);
        }

        JButton btn1 = new JButton("návrat na úvodní obrazovku");
        JButton btn2 = new JButton("nová hra");

        btn1.setFocusPainted(false);
        btn2.setFocusPainted(false);

        btn1.setFont(new Font("Lato", Font.PLAIN, 17));
        btn1.setSize(220, 50);
        btn1.setBackground(Color.WHITE);
        btn1.setOpaque(true);
        btn1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(customColor, 5),
                        BorderFactory.createMatteBorder(3, 3, 6, 3, Color.BLACK)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        btn2.setFont(new Font("Lato", Font.PLAIN, 17));
        btn2.setSize(220, 50);
        btn2.setBackground(Color.white);
        btn2.setOpaque(true);
        btn2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(customColor, 5),
                        BorderFactory.createMatteBorder(3, 3, 6, 3, Color.BLACK)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        panel.add(txtField, gbc);
        panel.add(btn1, gbc);
        panel.add(btn2, gbc);

        btn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                btn1.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(11, 5, 5, 5, customColor),
                                BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK)),
                        BorderFactory.createEmptyBorder(7, 10, 10, 10)));
                btn1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent ev) {
                btn1.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(customColor, 5),
                                BorderFactory.createMatteBorder(3, 3, 6, 3, Color.BLACK)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            }
        });

        btn2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent ev) {
                btn2.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(11, 5, 5, 5, customColor),
                                BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK)),
                        BorderFactory.createEmptyBorder(7, 10, 10, 10)));
                btn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent ev) {
                btn2.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(customColor, 5),
                                BorderFactory.createMatteBorder(3, 3, 6, 3, Color.BLACK)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            }
        });

        btn1.addActionListener(e ->
        {
            Platform.runLater(() -> {
                try {
                    new ClientApplication().start(stage);
                    frame.dispose();
                } catch (Exception err) {
                    throw new RuntimeException(err);
                }
            });
        });

        btn2.addActionListener(e ->
        {
            Platform.runLater(() -> {
                try {
                    new TheDrakeApp().start(stage);
                    frame.dispose();
                } catch (Exception err) {
                    throw new RuntimeException(err);
                }
            });

        });
    }

    static void setLocationToCenter(JFrame frame) {
        GraphicsConfiguration config = frame.getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();

        int x = bounds.x + (bounds.width / 2) - (frame.getWidth() / 2);
        int y = bounds.y + (bounds.height / 2) - frame.getHeight();
        frame.setLocation(x, y);
    }
}

