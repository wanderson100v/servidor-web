/**
 * 
 */
package br.com.servidorweb.controller;

import java.net.URL;
import java.util.ResourceBundle;

import br.com.servidorweb.view.Notificacao;
import javafx.fxml.Initializable;

/**
 * @author mael
 *
 */
public abstract class Controle implements Initializable{

	protected Notificacao notificacao;
	
	public abstract void init();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		notificacao = Notificacao.getInstance();
		init();
	}
}
