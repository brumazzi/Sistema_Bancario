package gerente;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Gerente{
	public static void main(String args[]) throws IOException{
		Socket conn;// = new Socket("127.0.0.1", 8001);
		Scanner scan = new Scanner(System.in);
		Scanner in = new Scanner(conn.getInputStream());
		PrintStream out = new PrintStream(conn.getOutputStream());
		String ac = null;
		boolean on;

		while(true){
			conn = new Socket("127.0.0.1", 8002);
			System.out.print("Conta: ");
			String conta = scan.nextLine();
			System.out.print("Senha: ");
			String senha = scan.nextLine();

			out.println(conta);
			out.println(senha);

			on = in.nextLine().equals("+") ? true : false;
			while(on){
				System.out.println("---------------------");
				System.out.println("---Menu de Gerente---");
				System.out.println("---------------------");
				System.out.println("*caixas (lista caixas)");
				System.out.println("*fechar (fecha um caixa)");
				System.out.println("*abrir (abre um caixa)");
				System.out.println("*clientes (lista clientes)");
				System.out.println("*cadastrar (cadastra cliente)");
				System.out.println("*inativar (inativa cliente)");
				System.out.println("*ativar (ativa cliente)");
				System.out.println("*sair\n\n");
				System.out.println("->");

				ac = scan.nextLine();
				if(ac.equals("sair"))
					on = false;
				out.println(ac);

				switch(ac){
					case "abrir":
						System.out.print("Digite o numero do caixa: ");
						out.println(scan.nextLine());
						break;
					case "fechar":
						System.out.print("Digite o numero do caixa: ");
					case "cadastrar":
						System.out.print("Nome: ");
						out.println(scan.nextLine());
						System.out.print("Senha: ");
						out.println(scan.nextLine());
						break;
					case "inativar":
						System.out.print("Digite a conta do cliente: ");
						out.println(scan.nextLine());
						break;
					case "ativar":
						System.out.print("Digite a conta do cliente: ");
						out.println(scan.nextLine());
						break;
				}

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
