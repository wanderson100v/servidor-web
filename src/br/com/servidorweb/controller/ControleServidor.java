package br.com.servidorweb.controller;

import br.com.servidorweb.model.Config;
import br.com.servidorweb.model.Servidor;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * @author mael
 *
 */
public class ControleServidor extends Controle {

	@FXML
	private Label lblPorta;

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

	private Servidor servidor;
	private boolean log;
	private Service<Object> service;
	private Config config = new Config();

	@Override
	public void init() {
		config.getConfig();
		log = true;
		servidor = new Servidor(config.getPorta(),config.getPasta().getPath());
		lblPorta.setText(config.getPorta()+"");

		service = new Service<Object>() {

			@Override
			protected Task<Object> createTask() {
				return new Task<Object>() {

					@Override
					protected Object call() throws Exception {

						while(true)
						{
							try {
								servidor.aguardandoRequisicao();
								
								String linha = servidor.requisicao();
								String[] vetorString;
								if(linha != null)
								{
									
									vetorString = linha.split(" ");
									for (int i =0; i < vetorString.length; i++) {
										vetorString[i] = vetorString[i].replace("%20", " ");
									}
									
									atualizarLog(linha.replace("%20", " "));
									atualizarLog("---------------------------------------------------------------------------------------------------------------------");
									servidor.validarProtocolo(vetorString);
									servidor.encerrar();
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				};

			}
		};

		service.start();
	}

	@FXML
	void clickAction(ActionEvent event) {

		Object obj = event.getSource();

		if(obj == btnIniciar)
		{
			btnIniciar.setVisible(false);
			btnParar.setVisible(true);
			iniciarServidor();
		}
		else if(obj == btnParar)
		{
			btnParar.setVisible(false);
			btnIniciar.setVisible(true);
			pararServidor();
		}
		else if(obj == btnComLog)
		{
			btnComLog.setVisible(false);
			btnSemLog.setVisible(true);
			log = true;
		}
		else if(obj == btnSemLog)
		{
			btnSemLog.setVisible(false);
			btnComLog.setVisible(true);
			log = false;
		}
		else if(obj == btnReiniciar)
		{
			btnIniciar.setVisible(false);
			btnParar.setVisible(true);
			btnComLog.setVisible(false);
			btnSemLog.setVisible(true);
			txaInformacoes.setText(null);
			reiniciarServidor();
		}
	}

	public void iniciarServidor()
	{
		service.start();
	}

	public void pararServidor()
	{
		service.cancel();
	}

	public void reiniciarServidor()
	{
		service.restart();
	}

	public void atualizarLog(String requisicao)
	{
		if(log) 
		{
			if(txaInformacoes.getText() != null)
				txaInformacoes.setText(txaInformacoes.getText()+requisicao+"\n");
			else
				txaInformacoes.setText(requisicao+"\n");
		}
	}
}
