package caixa;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Caixa{
	public static void main(String args[]) throws IOException{
		Socket conn;// = new Socket("127.0.0.1", 8001);
		Scanner scan = new Scanner(System.in);
		Scanner in = new Scanner(conn.getInputStream());
		PrintStream out = new PrintStream(conn.getOutputStream());
		String ac = null;
		boolean on;

		while(true){
			conn = new Socket("127.0.0.1", 8001);
			System.out.print("Conta: ");
			String conta = scan.nextLine();
			System.out.print("Senha: ");
			String senha = scan.nextLine();

			out.println(conta);
			out.println(senha);

			on = in.nextLine().equals("+") ? true : false;
			while(on){
				System.out.println("---------------------");
				System.out.println("---Menu de Usuário---");
				System.out.println("---------------------");
				System.out.println("*saque");
				System.out.println("*deposito");
				System.out.println("*saldo");
				System.out.println("*extrato");
				System.out.println("*sair\n\n");
				System.out.println("->");

				ac = scan.nextLine();
				if(ac.equals("sair"))
					on = false;
				out.println(ac);

				while(ac = in.nextLine()){
					if(ac.equals("-"))
						System.out.println("Erro ao efetuar a ação");
					else if(ac.equals("+"))
						System.out.println("Ação efetuada com sucesso!");
					else
						System.out.println(ac);
				}
			}
			conn.close();
		}
	}
}
