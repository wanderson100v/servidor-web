package br.com.servidorweb.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Teste {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(8888);
		
		System.out.println("Esperando...");
		Socket socket = serverSocket.accept();
		System.out.println("Conexão estabelecida...");

		BufferedReader entrada = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		
		while (true) {
			String linha = entrada.readLine();
			if(linha == null || linha.equals("")) {
				break;
			}
			System.out.println("Chrome >>> " + linha);
		}
		
		// auto flush (auto envio) desativado = false)
		PrintWriter saida = new PrintWriter(socket.getOutputStream(), false);
		
		saida.print("HTTP/1.1 200 OK\r\n");
		saida.print("Date: Fri, 12 Apr 2019 23:40:23 GMT\r\n");
		saida.print("Server: Eclipse/2020 (Ubuntu)\r\n");
		saida.print("Content-Type: text/html\r\n");
		saida.print("\r\n");
//		saida.print("Olá mundo! Meu primeiro pacote HTTP!");
		saida.print("<html>");
		saida.print("<head>");
		saida.print("<title>Testando servidor no Eclipse</title>");
		saida.print("</head>");
		saida.print("<body>");
		saida.print("<h1>Olá mundo!</h1>");
		saida.print("</body>");
		saida.print("</html>");
		
		saida.flush(); //enviando...
		
		
	}

}
