package chatClienteImplementacao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import chatClienteInterface.ChatClienteInterface;

@SuppressWarnings("serial")
public class ChatClienteImplementacao extends UnicastRemoteObject implements ChatClienteInterface{
	private final String username;
	private volatile boolean pronto = false;
	
	public ChatClienteImplementacao(String username) throws RemoteException {
		super();
		this.username = username;
	}

	

	@Override
	public synchronized void receiveMessage(String sender, String message) throws RemoteException {
		if (sender.equals(username)) {
			// mensagem do emissor
			System.out.println("[" + username + " (você)] " + message);
		} else {
			if (pronto) System.out.println(); // sai da linha do prompt
			System.out.println("[" + sender + "] " + message);
		}
		if (pronto) System.out.print(username + "> ");
		
	}
	
	public void setPronto(boolean pronto) {
		this.pronto = pronto;
	}
	
	public String getUserName() {
		return username;
	}
}
