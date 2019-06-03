package br.com.servidorweb.controller;

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
	private static int porta;
	private boolean log;
	private Service<Object> service;

	@Override
	public void init() {

		log = true;
		servidor = new Servidor(porta);
		lblPorta.setText(porta+"");

		service = new Service<Object>() {

			@Override
			protected Task<Object> createTask() {
				return new Task<Object>() {

					@Override
					protected Object call() throws Exception {

						while(true)
						{
							if(isCancelled())
								break;
							
							try {
								servidor.aguardandoRequisicao();

								while (true) {
									String linha = servidor.requisicao();
									if(linha == null || linha.equals("")) {
										break;
									}

									atualizarLog(linha);
								}

								atualizarLog("----------------------------------------------------------------------------------------------------------------------------------");
								System.out.println("Preparando Retorno");
								servidor.prepararRetorno("HTTP/1.1 200 OK\r\n");
								servidor.prepararRetorno("Date: Fri, 12 Apr 2019 23:40:23 GMT\r\n");
								servidor.prepararRetorno("Server: Eclipse/2020 (Ubuntu)\r\n");
								servidor.prepararRetorno("Content-Type: text/html\r\n");
								servidor.prepararRetorno("\r\n");
								servidor.prepararRetorno("<html>");
								servidor.prepararRetorno("<head>");
								servidor.prepararRetorno("<title>Testando servidor no Eclipse</title>");
								servidor.prepararRetorno("</head>");
								servidor.prepararRetorno("<body>");
								servidor.prepararRetorno("<h1>Olá mundo!</h1>");
								servidor.prepararRetorno("</body>");
								servidor.prepararRetorno("</html>");

								System.out.println("Enviando Retorno");
								servidor.enviar();

								System.out.println("Fim");

								servidor.encerrar();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return null;

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

	public static void setPorta(int porta) {
		ControleServidor.porta = porta;
	}
}
