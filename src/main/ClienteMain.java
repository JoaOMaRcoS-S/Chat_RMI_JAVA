package main;

import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import chatClienteImplementacao.ChatClienteImplementacao;
import chatServerInterface.ChatServerInterface;

public class ClienteMain {
	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : "localhost";
		Scanner scanner = new Scanner(System.in, System.console() != null ? System.console().charset()
                : java.nio.charset.StandardCharsets.UTF_8);
		
		try {
			Registry registry = LocateRegistry.getRegistry(host,Main.PORTA);
			ChatServerInterface servidor = (ChatServerInterface) registry.lookup(Main.NOME_SERVICO);
			ChatClienteImplementacao cliente = null;
			while (cliente == null) {
				System.out.println("Digite seu nome: ");
				if(!scanner.hasNextLine()) {
					return;
				}
				String nome = scanner.nextLine().trim();
				if(nome.isEmpty()) {
					continue;
				}
				ChatClienteImplementacao tentativa = new ChatClienteImplementacao(nome);
				try{
					servidor.registerClient(nome, tentativa);
					cliente = tentativa;
				}catch (RemoteException e) {
					Throwable causa = (e instanceof ServerException && e.getCause() != null) ? e.getCause() : e;
					System.out.println(causa.getMessage());
					UnicastRemoteObject.unexportObject(tentativa, true);
				}
			}
			final String nome = cliente.getUserName();
			System.out.println("Conectado! Digite suas mensagens (/sair para sair).");
			cliente.setPronto(true);
			System.out.print(nome + "> ");
			while(scanner.hasNextLine()) {
				String linha = scanner.nextLine();
				if(linha.equalsIgnoreCase("/sair"))break;
				if(linha.isBlank()) {
					servidor.sendMessage(nome, "> ");
					continue;
				}
				servidor.sendMessage(nome, linha);
			}
			servidor.unRegisterClient(nome);
			UnicastRemoteObject.unexportObject(cliente, true);
			System.out.println("voce saiu do chat");
		}catch (Exception e) {
			System.err.println("Erro no cliente: "+ e.getMessage());
			e.printStackTrace();
		}finally {
			scanner.close();
			System.exit(0);
		}
	}
}
