package br.com.servidorweb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * @author mael
 *
 */
public class ControleServidor extends Controle {

	@FXML
	private Button btnIniciar;

	@FXML
	private Button btnParar;

	@FXML
	private Button btnComLog;

	@FXML
	private Button btnSemLog;

	@FXML
	private Button btnReiniciar;

	@FXML
	private TextArea txaInformacoes;

	@Override
	public void init() {

	}

	@FXML
	void clickAction(ActionEvent event) {

		Object obj = event.getSource();
		
		if(obj == btnIniciar)
		{
			btnIniciar.setVisible(false);
			btnParar.setVisible(true);
		}
		else if(obj == btnParar)
		{
			btnParar.setVisible(false);
			btnIniciar.setVisible(true);
		}
		else if(obj == btnComLog)
		{
			btnComLog.setVisible(false);
			btnSemLog.setVisible(true);
		}
		else if(obj == btnSemLog)
		{
			btnSemLog.setVisible(false);
			btnComLog.setVisible(true);
		}
		else if(obj == btnReiniciar)
		{
			
		}
		
	}

}
