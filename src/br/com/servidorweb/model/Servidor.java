/**
 * 
 */
package br.com.servidorweb.model;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author mael
 *
 */
public class Servidor {

	private ServerSocket serverSocket;
	private Socket socket;
	private BufferedReader entrada;
	private PrintWriter saidaTexto;
	private BufferedOutputStream saidaBinario;

	private String pasta;

	public Servidor(int porta) {
		try {
			serverSocket = new ServerSocket(porta);
		} catch (IOException e) {
			e.printStackTrace();
		}

		pasta = "arquivos";

		File diretorio = new File(pasta);
		if (!diretorio.exists()) {
			diretorio.mkdirs();
		}
	}

	public Socket aguardandoRequisicao() {

		try {
			socket = serverSocket.accept();
			entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			saidaTexto = new PrintWriter(socket.getOutputStream(), true);
			saidaBinario = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return socket;
	}

	public String requisicao() {
		String requisicao = null;
		try {
			requisicao = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return requisicao;
	}

	public void prepararRetorno(String retorno) {
		saidaTexto.print(retorno);
	}

	public void enviarTexto() {
		saidaTexto.flush();
	}

	public void enviarArquivo(String status,byte[] arquivo, String tipo) {

		SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
		formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date data = new Date();
		String dataFormatada = formatador.format(data) + " GMT";

		String respond = "HTTP/1.1 "+status+"\r\n"
				+ "Location: http://localhost:8000/\r\n"
				+ "Date: " + dataFormatada + "\r\n"
				+ "Server: Servidor/1.0\r\n"
				+ "Content-Type: "+tipo+"\r\n"
				+ "Content-Length: " + arquivo.length + "\r\n"
				+ "Connection: close\r\n"
				+ "\r\n";
		System.out.println(respond);
		try {
			saidaBinario.write(respond.getBytes());
			if (arquivo != null) {
				saidaBinario.write(arquivo);
			}
			saidaBinario.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void encerrar() {
		try {
			entrada.close();
			saidaTexto.close();
			saidaBinario.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buscarArquivo(String caminho) {

		String fileName = "/" + caminho;
		File file = null;
		String tipo = "";
		if(caminho.equals("/"))
			file = new File(pasta+"/index.php");
		else
			file = new File(pasta + fileName);
		try {
			if(file.exists()) {
				tipo = Files.probeContentType(file.toPath());
				enviarArquivo("200 OK",getBytes(file), tipo);
			}
			else {
				//implementar 404 not found
				System.out.println("Implementar 404 not found");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(file);
	}

	public byte[] getBytes(File file) {

		try {
			long length = file.length();

			if (length > Integer.MAX_VALUE) {
				throw new IOException("File is too large!");
			}

			byte[] bytes = new byte[(int) length];

			int offset = 0;
			int numRead = 0;

			InputStream is = new FileInputStream(file);
			try {
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
			} finally {
				is.close();
			}

			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}
			return bytes;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
