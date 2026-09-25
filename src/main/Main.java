package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import chatServerImplementacao.ChatServerImplementacao;

public class Main {
	public static final int PORTA = 1050;
	public static final String NOME_SERVICO = "ChatSD";
	
	private static Registry registry;
	
	public static void main(String[]args) {
		try {
			ChatServerImplementacao servidor = new ChatServerImplementacao();
			registry = LocateRegistry.createRegistry(PORTA);
			registry.rebind(NOME_SERVICO, servidor);
			
			System.out.println("Servidor "+ NOME_SERVICO+" rodando na porta "+ PORTA);
			System.out.println("aguardando alunos...");
		}catch (Exception e) {
			System.err.println("Erro ao iniciar o Servidor "+ NOME_SERVICO + e.getMessage());
			e.printStackTrace();
		}
	}
}
