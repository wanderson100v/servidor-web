package br.com.servidorweb.controller;

import java.io.File;
import java.io.IOException;

import br.com.servidorweb.app.AppServidorWeb;
import br.com.servidorweb.model.Config;
import br.com.servidorweb.util.MaskFieldUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

/**
 * @author mael
 *
 */
public class ControleLoginServidor extends Controle {

	@FXML
	private TextField tfdPorta;
	
    @FXML
    private TextField pastaFld;

	@FXML
	private Button btnEntrar;
	
    @FXML
    private Button pastaBtn;

	@FXML
	private Button btnCancelar;
	
	Config c = new Config();

	private Pane servidor;

	@Override
	public void init() {
		MaskFieldUtil.numericField(tfdPorta);
		MaskFieldUtil.maxField(tfdPorta, 5);
		
		c.getConfig();
		tfdPorta.setText((c.getPorta() != 0)? c.getPorta()+"" : "");
		pastaFld.setText((c.getPasta() != null)? c.getPasta().getPath() : "");
	}

	@FXML
	void clickAction(ActionEvent event) {

		Object obj = event.getSource();

		if (obj == btnEntrar) {
			if (!tfdPorta.getText().trim().isEmpty()) {
				try {
					c.setPorta(Integer.parseInt("0"+tfdPorta.getText().trim()));
					c.setPasta(new File(pastaFld.getText()));
					c.saveConfig();
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
	

    @FXML
    void selectFolder(ActionEvent event) {
    	File file = new DirectoryChooser().showDialog(AppServidorWeb.getStage());
    	if(file != null)
    		pastaFld.setText(file.getPath());
    }

}
