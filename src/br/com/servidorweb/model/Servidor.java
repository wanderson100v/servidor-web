/**
 * 
 */
package br.com.servidorweb.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author mael
 *
 */
public class Servidor {

	private ServerSocket serverSocket;
	private Socket socket;
	BufferedReader entrada;
	private PrintWriter saida;

	public Servidor(int porta) {
		try {
			serverSocket = new ServerSocket(porta);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket aguardandoRequisicao() {
		
		try {
			socket = serverSocket.accept();
			entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			saida = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return socket;
	}
	
	public String requisicao()
	{
		String requisicao = null;
		try {
			requisicao = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return requisicao;
	}
	
	public void prepararRetorno(String retorno) 
	{
		saida.print(retorno);
	}
	
	public void enviar()
	{
		saida.flush();
	}
	
	public void  encerrar(){
		try {
			entrada.close();
			saida.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
