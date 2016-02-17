package dbmanager;

import java.sql.*;
import java.util.ArrayList;

public class DBManager{
	public static Connection conn = null;
	public static Statement stm = null;
	public static int CLIENTE = 1;
	public static int GERENTE = 2;

	public static boolean connect() throws SQLException{
		conn = DriverManager.getConnection("jdbc:mysql://myhost/banco","root","daniel21");
		stm = conn.createStatement();
		return true;
	}
	public static boolean close() throws SQLException{
		conn.close();
		conn = null;
		stm = null;
		return true;
	}

	public static boolean login(int uType, String conta, String senha){
		try{
			connect();
			String sql = "select conta from " + ((uType == CLIENTE) ? "cliente" : "gerente") + " where conta = " + conta + " and senha = " + senha + " and ativo = 1";
			ResultSet res = stm.executeQuery(sql);
			if(res.next()){
				close();
				return true;
			}
		}catch(SQLException e){}
		return false;
	}
	public static String extrato(String conta, String senha){
		try{
			connect();
			String sql = "select valor, data from extrato inner join cliente on cliente.id = extrato.cliente where conta = " + conta + " senha = " + senha;
			ResultSet res = stm.executeQuery(sql);
			String extrato = "";
			while(res.next())
				extrato += "\n---------------------------\nValor: "+res.getString(0)+"\nData: "+res.getString(1);
			close();

			return extrato;
		}catch(SQLException e){}
		return null;
	}
	public static String saldo(String conta, String senha){
		try{
			connect();
			String sql = "select saldo from cliente where conta = "+conta+" and senha = "+senha;
			ResultSet res = stm.executeQuery(sql);
			if(res.next()){
				close();
				return res.getString(0);
			}
		}catch(SQLException e){}
		return null;
	}
	public static boolean sacar(float valor, String conta, String senha){
		try{
			float _saldo = Float.valueOf(saldo(conta, senha));
			if(_saldo < valor || valor < 1.0){
				return false;
			}
			connect();
			String sql = "update cliente set saldo = " + (_saldo - valor) + " where conta = " + conta + " and senha = " + senha;
			stm.execute(sql);
			close();
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean depositar(float valor, String conta, String senha){
		try{
			if(valor < 1)
				return false;
			float _saldo = Float.valueOf(saldo(conta, senha));
			connect();
			String sql = "update cliente set saldo = " + (_saldo + valor) + " where conta = " + conta + " and senha = " + senha;
			stm.execute(sql);
			close();
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean cadastrar(int uType, String nome, String conta, String senha){
		String sql = "insert into " + ((uType == GERENTE) ? "gerente" : "cliente") + "(nome,conta,senha) values('"+nome+"',"+conta+","+senha+",1)";
		try{
			connect();
			stm.execute(sql);
			close();
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean inativar(String conta){
		try{
			connect();
			String sql = "update cliente set ativo = 0 where conta = " + conta;
			stm.execute(sql);
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean ativar(String conta){
		try{
			connect();
			String sql = "update cliente set ativo = 1 where conta = " + conta;
			stm.execute(sql);
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static String listar(){
		try{
			connect();
			String sql = "select * from cliente";
			ResultSet res = stm.executeQuery(sql);
			sql = "";
			while(res.next()){
				sql += res.getString(0) + " | " + res.getString(2) + " | " + (res.getString(3).equals("1") ? "+" : "-")+"\n";
			}
			close();
			return sql;
		}catch(SQLException e){}
		return null;
	}
}
