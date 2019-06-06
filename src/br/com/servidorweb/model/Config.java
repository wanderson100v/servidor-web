package br.com.servidorweb.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class Config implements Serializable{
	
	private static final long serialVersionUID = -4047117547644500104L;

	private int porta;
	
	private File pasta;
	
	public Config(int porta, File pasta) {
		this.porta = porta;
		this.pasta = pasta;
	}
	
	public Config() {}
	
	public void getConfig() {
		try {
			File file = new File(getClass().getResource("config.txt").toURI());
			try(FileInputStream fis = new FileInputStream(file)){
				try(ObjectInputStream ois = new ObjectInputStream(fis)){
					Config config = (Config) ois.readObject();
					this.setPasta(config.getPasta());
					this.setPorta(config.getPorta());
				}
			}
		}catch (Exception e) {}
	}
	
	public void saveConfig() {
		try {
			File file = new File(getClass().getResource("config.txt").toURI());
			try(FileOutputStream fos = new FileOutputStream(file)){
				try(ObjectOutputStream oos = new ObjectOutputStream(fos)){
					oos.writeObject(this);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public File getPasta() {
		return pasta;
	}

	public void setPasta(File pasta) {
		this.pasta = pasta;
	}
	
}
