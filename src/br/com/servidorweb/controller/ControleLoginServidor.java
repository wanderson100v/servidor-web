package br.com.servidorweb.controller;

import java.io.IOException;

import br.com.servidorweb.app.AppServidorWeb;
import br.com.servidorweb.util.MaskFieldUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * @author mael
 *
 */
public class ControleLoginServidor extends Controle {

	@FXML
	private TextField tfdPorta;

	@FXML
	private Button btnEntrar;

	@FXML
	private Button btnCancelar;

	private Pane servidor;

	@Override
	public void init() {
		MaskFieldUtil.numericField(tfdPorta);
		MaskFieldUtil.maxField(tfdPorta, 5);
	}

	@FXML
	void clickAction(ActionEvent event) {

		Object obj = event.getSource();

		if (obj == btnEntrar) {
			if (!tfdPorta.getText().trim().isEmpty()) {
				try {
					ControleServidor.setPorta(Integer.parseInt(tfdPorta.getText().trim()));
					if (servidor == null)
						servidor = FXMLLoader.load(getClass().getClassLoader().getResource("br/com/servidorweb/view/Servidor.fxml"));
					AppServidorWeb.changeStage(servidor);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else if (obj == btnCancelar) {
			System.exit(0);
		}
	}

}
