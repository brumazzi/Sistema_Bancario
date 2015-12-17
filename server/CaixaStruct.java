package server;

import java.net.Socket;

public class CaixaStruct{
	public String numero;
	public Socket conn;
	public boolean ativo;
	public CaixaStruct(String numero, Socket cli){
		this.numero = numero;
		this.conn = cli;
		this.ativo = true;
	}
}
