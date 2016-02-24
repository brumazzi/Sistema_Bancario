package server;

import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;
import java.io.*;

import dbmanager.DBManager;
import server.CaixaStruct;

class CaixaThread implements Runnable{
	private Socket __ = null;
	private CaixaStruct cs = null;
	private int x;

	public CaixaThread(Socket caixa, int cai){
		this.__ = caixa;
		ServerCliente.caixas.get(cai).conn = this.__;
		this.x = cai;
	}

	private void send(String msg) throws IOException{
		new PrintStream(this.__.getOutputStream()).println(msg);
	}

	public void run(){
		this.comman();
	}
	public synchronized void comman(){
		boolean ativo = false;
		Scanner s = null;
		try{
			s = new Scanner(this.__.getInputStream());
		}catch(IOException e){}
		String conta= null;
		try{
			ativo = DBManager.login(1,conta = s.nextLine(),s.nextLine());
			send(ativo ? "+" : "-");
		}catch(IOException e){}
		float value = 0;
		try{
			while(ativo && ServerCliente.caixas.get(this.x).ativo){
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
                                                send("+");
						ativo = false;
						break;
					default:
                                                send("-");
						break;
				}
			}
		}catch(IOException e){}
		try{
			this.__.close();
		}catch(IOException e){}
		ServerCliente.caixas.get(this.x).conn = null;
	}
}
class ControleThread implements Runnable{
	private Socket __ = null;
	public ControleThread(Socket controle){
		this.__ = controle;
	}

	private void send(String msg) throws IOException{
		new PrintStream(this.__.getOutputStream()).println(msg);
	}

	public void run(){
		this.comman();
	}
	public synchronized void comman(){
		boolean ativo = false;
		String caixas;
		Scanner s = null;
		try{
			s = new Scanner(this.__.getInputStream());
		}catch(IOException e){}
		String conta;
		try{
			ativo = DBManager.login(2,conta = s.nextLine(),s.nextLine());
			send(ativo ? "+" : "-");
		}catch(Exception e){}
		float value = 0;
		try{
			while(ativo){
				caixas = "";
				switch(s.nextLine()){
					case "caixas":
						try{
							for(int x=0;x<ServerCliente.caixas.size();x++)
								caixas += ""+ServerCliente.caixas.get(x).numero+" "+(ServerCliente.caixas.get(x).ativo ? "+" : "-") +"**";
							send(caixas);
						}catch(IOException e){send("-");}
						break;
					case "fechar":
						try{
							if(ServerCliente.close(s.nextLine()))
								send("+");
							else
								send("-");
						}catch(IOException e){send("-");}
						break;
					case "abrir":
						try{
							if(ServerCliente.open(s.nextLine()))
								send("+");
							else
								send("-");
						}catch(IOException e){send("-");}
						break;
					case "clientes":
						try{
							send(DBManager.listar());
						}catch(IOException e){send("-");}
						break;
					case "cadastrar":
						try{
							if(DBManager.cadastrar(1,s.nextLine(),s.nextLine(),s.nextLine()))
								send("+");
							else
								send("-");
						}catch(IOException e){send("-");}
						break;
					case "inativar":
						try{
							if(DBManager.inativar(s.nextLine()))
								send("+");
							else
								send("-");
						}catch(IOException e){send("-");}
						break;
					case "ativar":
						try{
							if(DBManager.ativar(s.nextLine()))
								send("+");
							else
								send("-");
						}catch(IOException e){send("-");}
						break;
					case "sair":
                                                send("+");
                                                ativo =false;
						break;
					default:
                                                send("-");
						break;
				}
			}
		}catch(IOException e){}
	}
}

class ServerCliente implements Runnable{
	public static ArrayList<CaixaStruct> caixas = new ArrayList<CaixaStruct>();
	private ServerSocket __;
	
	public ServerCliente() throws IOException{
		this.__ = new ServerSocket(8001);
		for(int x=1;x<7;x++)
			caixas.add(new CaixaStruct("Caixa #"+x,null));
	}

	public void run(){
		while(true){
			for(int x=0;x<6;x++){
				if(ServerCliente.caixas.get(x).conn == null && ServerCliente.caixas.get(x).ativo == true){
					try{
						new Thread(new CaixaThread(this.__.accept(),x)).start();
					}catch(Exception e){}
				}
			}
		}
	}
	public static boolean close(String caixa){
		ServerCliente.caixas.get(Integer.valueOf(caixa)-1).ativo = false;
		return true;
	}
	public static boolean open(String caixa){
		ServerCliente.caixas.get(Integer.valueOf(caixa)-1).ativo = true;
		return true;
	}
}

public class Server{
	public static void main(String args[]) throws IOException{
		new Thread(new ServerCliente()).start();
		ServerSocket __ = new ServerSocket(8002);
		while(true)
			new Thread(new ControleThread(__.accept())).start();
	}
}
