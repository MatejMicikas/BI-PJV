package thedrake.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import thedrake.*;

import java.util.Random;

public class TheDrakeApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static GameState newSampleGameState() {
        Board board = new Board(4);
        Random rand = new Random();
        int randomRow = rand.nextInt(2) + 1;
        int randomColumn = rand.nextInt(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(randomColumn, randomRow), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainView = new BorderPane();
        BoardView boardView = new BoardView(newSampleGameState(), primaryStage);
        StackView stackViewBlueArmy = new StackView(newSampleGameState(), PlayingSide.BLUE, boardView);
        StackView stackViewOrangeArmy = new StackView(newSampleGameState(), PlayingSide.ORANGE, boardView);
        CaptureView captureViewBlueArmy = new CaptureView(newSampleGameState(), PlayingSide.ORANGE);
        CaptureView captureViewOrangeArmy = new CaptureView(newSampleGameState(), PlayingSide.BLUE);

        stackViewBlueArmy.setStyle("-fx-background-color: #d8fff9");

        boardView.stackViewBlueArmy = stackViewBlueArmy;
        boardView.stackViewOrangeArmy = stackViewOrangeArmy;
        boardView.captureViewBlueArmy = captureViewBlueArmy;
        boardView.captureViewOrangeArmy = captureViewOrangeArmy;

        mainView.setTop(stackViewOrangeArmy);
        mainView.setCenter(boardView);
        mainView.setBottom(stackViewBlueArmy);
        mainView.setLeft(captureViewOrangeArmy);
        mainView.setRight(captureViewBlueArmy);

        primaryStage.setScene(new Scene(mainView));
        primaryStage.setTitle("The Drake");
        primaryStage.show();
    }
}
