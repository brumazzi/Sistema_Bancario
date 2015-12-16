package server;

import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;
import java.io.*;

import dbmanager.DBManager;

class CaixaThread implements Runnable{
	private Socket __ = null;
	public CaixaThread(Socket caixa){
		this.__ = caixa;
	}

	private void send(String msg){
		new PrintStream(this.__.getOutputStream()).println(msg);
	}

	public void run(){
		try{
			Scanner s = new Scanner(this.__.getInputStream());
		}catch(IOException e){}
		String conta;
		try{
			boolean ativo = DBManager.login(1,conta = s.nextLine(),s.nextLine());
			send(ativo ? "+" : "-");
		}catch(SQLException e){}
		float value = 0;
		try{
			while(ativo){
				switch(s.nextLine()){
					case "saque":
						value = Float.valueOf(s.nextLine());
						try{
							if(DBManager.sacar(value, conta, s.nextLine()))
								send("+");
							else
								send("-");
						}catch(Exception e){send("-");}
						break;
					case "deposito":
						value = Float.valueOf(s.nextLine());
						try{
							if(DBManager.depositar(value, conta, s.nextLine()))
								send("+");
							else
								send("-");
						}catch(Exception e){send("-");}
						break;
					case "saldo":
						try{
							send(""+DBManager.saldo(conta, s.nextLine()));
						}catch(Exception e){send("-");}
						break;
					case "extrato":
						try{
							send(""+DBManager.extrato(conta, s.nextLine()));
						}catch(Exception e){send("-");}
						break;
					case "sair":
						ativo = false;
						break;
					default:
						break;
				}
			}
		}catch(IOException e){}
		try{
			this.__.close();
		}catch(IOException e){}
	}
}
class ControleThread implements Runnable{
	private Socket __ = null;
	public ControleThread(Socket controle){
		this.__ = Controle;
	}

	public void run(){}
}

class ServerCliente implements Runnable{
	public void run(){
		SocketServer __ = new SocketServer(8001);
		while(true){
			new Thread(new CaixaThread(__.accept())).start();
		}
	}
}

public class Server{
	public static void main(String args[]){
		new Thread(new ServerCliente()).start();
		SocketServer __ = new SocketServer(8002);
		while(true)
			new Thread(new ControleThread(__.accept())).start();
	}
}
