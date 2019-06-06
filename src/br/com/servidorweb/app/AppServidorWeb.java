package br.com.servidorweb.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author mael
 *
 */
public class AppServidorWeb extends Application {

	private static Stage stage;
	private Scene sceneServidor;
	private Pane servidor;
	
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		
		try {
			servidor = FXMLLoader.load(getClass().getClassLoader().getResource("br/com/servidorweb/view/LoginServidor.fxml"));
			sceneServidor = new Scene(servidor);
			primaryStage.setScene(sceneServidor);
			primaryStage.setTitle("Servidor Web");
			primaryStage.setResizable(false);
			primaryStage.centerOnScreen();
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}
	
	public static void changeStage(Pane pane)
	{
		stage.setScene(new Scene(pane));
	}
	
	public static Stage getStage() {
		return stage;
	}
	public static void main(String[] args) {
		launch(args);
	}
}
