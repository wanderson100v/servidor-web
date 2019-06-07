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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public Servidor(int porta,String pasta) {
		try {
			this.serverSocket = new ServerSocket(porta);
			this.pasta = pasta;
		
			Path dir = Paths.get(pasta);
			if (!dir.toFile().exists()) 
				Files.createDirectories(dir);
		} catch (IOException e) {
			e.printStackTrace();
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
				+ "Server: EMael Server/1.0\r\n"
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
	
	public void validarProtocolo(String[] vetorString) {
		try {
			if(vetorString.length == 3 && vetorString[0].equalsIgnoreCase("GET") 
					&& vetorString[1].contains("/"))
			{
				if(vetorString[2].equalsIgnoreCase("HTTP/1.1"))// Se tudo estiver ok no protocolo, possíveis saídas são : 2OO OK ou 404 Not Found
				{
					buscarArquivo(vetorString[1]);
				}
				else //505 HTTP Version Not Supported
				{
					File file = new File(getClass().getResource("http-version-not-supported.html").toURI());
					enviarArquivo("505 HTTP Version Not Supported",getBytes(file),Files.probeContentType(file.toPath()));
				}
			}
			else //400 Bad Request
			{
				File file = new File(getClass().getResource("bad-request.html").toURI());
				enviarArquivo("400 Bad Request",getBytes(file),Files.probeContentType(file.toPath()));
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void  buscarArquivo(String caminho) {
		if(caminho.isEmpty())
			caminho = "/";
		
		File dir = Paths.get(pasta+caminho).toFile();
		try {
			if (dir.exists())//Arq ou dir existe -  implementar nesse bloco protocolo 200 OK
			{	
				if(dir.isDirectory()) // Se é diretório devera listar todos os aquivos/pastas. Entretanto, se há um arq "index" o mesmo será mostrado em tela
				{
					File[] files = dir.listFiles();
		
					StringBuffer sb = new StringBuffer(
							"<html>\n"
							+"<head> "
							+ "<meta charset='utf8'> <title>Index of /</title>"
							+ "<head>"
							+ "\t<h1> Listagem de diretórios</h1>\n"
							+ "\t<ul>\n");
					for (int i = 0; i < files.length; i++) // para cada arq ou pasta do diretório
					{
						sb.append("\t\t<li><a href ='"+caminho+files[i].getName()+((files[i].isDirectory())? "/":"")+"'>"+files[i].getName()+"</a></li>\n");
						if(files[i].getPath().contains("index")) // verifica se  é index. Se for a construção do html com diretórios para, e é retornado o arq index.
						{ 
							String tipo = Files.probeContentType(files[i].toPath());
							enviarArquivo("200 OK",getBytes(files[i]), tipo);
							return;
						}
					}// caso saia do laço, siginifica não haver um arquivo index na pasta. Portanto é retornado um html com a listagem dos arquivos e diretorios interno a pasta requisitada
					sb.append("\t</ul>\n"
							+ "</html>");
					enviarArquivo("200 OK",sb.toString().getBytes(), null);
					
				}else  // como não é diretório, só é necessário enviar (dado que o arquivo existe)
					enviarArquivo("200 OK",getBytes(dir),Files.probeContentType(dir.toPath()));
			}else  // Arq ou dir não existe -  implementar nesse bloco protocolo 404 Not Found
				enviarArquivo("404 Not Found",getBytes(new File(getClass().getResource("not-found.html").toURI())),Files.probeContentType(dir.toPath()));
		}
		catch (IOException | URISyntaxException e) 
		{
			e.printStackTrace();
		}
		
		
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
