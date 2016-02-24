package dbmanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

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
			String sql = "select extrato.valor, extrato.data from extrato inner join cliente on cliente.id = extrato.cliente where cliente.conta = " + conta + " and cliente.senha = " + senha;
			ResultSet res = stm.executeQuery(sql);
			String extrato = "";
			while(res.next())
				extrato += "**---------------------------**Valor: "+res.getFloat("valor")+"**Data: "+res.getString("data");
			close();

			return extrato;
		}catch(SQLException e){}
		return null;
	}
	public static float saldo(String conta, String senha){
		try{
			connect();
			String sql = "select saldo from cliente where conta = "+conta+" and senha = "+senha;
			ResultSet res = stm.executeQuery(sql);
			if(res.next()){
                                float sal = (float) res.getFloat("saldo");
				close();
				return sal;
			}
		}catch(SQLException e){}
		return 0;
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
                        ResultSet res = stm.executeQuery("select id from cliente where conta="+conta+" and senha="+senha);
                        Calendar c = Calendar.getInstance();
                        String data = ""+c.YEAR+"-"+c.MONTH+"-"+c.DAY_OF_MONTH;
                        if(res.next()){                          
                            sql = "insert into extrato(cliente,valor,data) values("+res.getInt("id")+","+-1*valor+","+"'"+data+"')";
                            stm.execute(sql);
                        }
                        
			close();
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean depositar(float valor, String conta, String senha){
		try{
			if(valor < 1)
				return false;
                        float _saldo = (float)0.0;
                        try{
                            _saldo = Float.valueOf(saldo(conta, senha));
                        }catch(Exception e){
                            _saldo = (float)0.0;
                        }
			connect();
			String sql = "update cliente set saldo = " + (_saldo + valor) + " where conta = " + conta + " and senha = " + senha;
			stm.execute(sql);
                        ResultSet res = stm.executeQuery("select id from cliente where conta="+conta+" and senha="+senha);
                        Calendar c = Calendar.getInstance();
                        String data = ""+c.YEAR+"-"+c.MONTH+"-"+c.DAY_OF_MONTH;
                        if(res.next())
                            stm.execute("insert into extrato(cliente,valor,data) values("+res.getInt("id")+","+valor+",'"+data+"')");
			close();
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean cadastrar(int uType, String nome, String conta, String senha){
            if(existes(conta))
                    return false;
		String sql = "insert into " + ((uType == GERENTE) ? "gerente" : "cliente") + "(nome,conta,senha, ativo) values('"+nome+"',"+conta+","+senha+",1)";
		try{
			connect();
			stm.execute(sql);
			close();
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean inativar(String conta){
            if(!existes(conta))
                    return false;
		try{
			connect();
			String sql = "update cliente set ativo = 0 where conta = " + conta;
			stm.execute(sql);
			return true;
		}catch(SQLException e){}
		return false;
	}
	public static boolean ativar(String conta){
                if(!existes(conta))
                    return false;
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
				sql += res.getInt("id") + " | " + res.getString("nome") + " | " + (res.getInt("ativo") == 1 ? "+" : "-")+"**";
			}
			close();
                        System.err.print(sql);
			return sql;
		}catch(SQLException e){}
		return null;
	}
        
        public static boolean existes(String conta){
            try{
                connect();
                String sql = "select * from cliente where conta = "+conta;
                ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    boolean ret = true;
                    close();
                    return ret;
                }
            }catch(SQLException err){}
            return false;
        }
}