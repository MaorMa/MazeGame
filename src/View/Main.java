package View;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        //-----------------------------
        primaryStage.setTitle("MazeGenerator");
        FXMLLoader fxmlLoader = new FXMLLoader();

        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(475);
        primaryStage.setMinWidth(590);
        //----------------------------
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.setResizeEvent();
        viewModel.addObserver(view);
        SetStageCloseEvent(primaryStage, model);
        primaryStage.show();
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                primaryStage.setFullScreen(true);
            }
        });

        Media maze_runner = new Media(new File("resources/maze_runner.mp3").toURI().toString());
        MediaPlayer maze_runner_player = new MediaPlayer(maze_runner);
        maze_runner_player.play();
    }

    /**
     * Alert to close the game
     * @param primaryStage - stage to close
     * @param model - the model we use
     */
    private void SetStageCloseEvent(Stage primaryStage,MyModel model) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                model.closeApp();//stopServers();
                windowEvent.consume();
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}