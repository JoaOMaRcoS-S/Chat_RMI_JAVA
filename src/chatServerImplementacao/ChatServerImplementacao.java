package chatServerImplementacao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import chatClienteInterface.ChatClienteInterface;
import chatServerInterface.ChatServerInterface;

@SuppressWarnings("serial")
public class ChatServerImplementacao extends UnicastRemoteObject implements ChatServerInterface{
	private final Map<String, ChatClienteInterface> clients = new ConcurrentHashMap<>();
	
	public ChatServerImplementacao() throws RemoteException {
		super();
	}

	@Override
	public synchronized void registerClient(String username, ChatClienteInterface cliente) throws RemoteException {
		if(clients.containsKey(username)) {
			throw new RemoteException("Nome do aluno "+ username+" já está na sala");
		}
		clients.put(username, cliente);
		System.out.println("O "+ username + " acessou a sala");
		
		StringBuilder presentes = new StringBuilder();
		for (String nome : clients.keySet()) {
			if (!nome.equals(username)) {
				if (presentes.length() > 0) presentes.append(", ");
				presentes.append(nome);
			}
		}
		if (presentes.length() > 0) {
			cliente.receiveMessage("ChatSD", "Já estão na sala: " + presentes);
		} else {
			cliente.receiveMessage("ChatSD", "Você é o primeiro na sala");
		}
		sendMessage("ChatSD",username + " entrou da sala");
	}

	@Override
	public synchronized void unRegisterClient(String username) throws RemoteException {
		if(clients.remove(username)!= null){
			System.out.println("Aluno "+ username + " saiu da sala");
			sendMessage("ChatSD",username + "saiu da sala");
		}
	}

	@Override
	public void sendMessage(String sender, String message) throws RemoteException {
		System.out.println("["+sender+"] "+message);
		for(Map.Entry<String, ChatClienteInterface> entry:clients.entrySet()) {
			try {
				entry.getValue().receiveMessage(sender, message);
			}catch (RemoteException e) {
				System.out.println("Erro ao enviar mensagem para "+ entry.getKey()+". Removendo cliente");
				e.printStackTrace();
				clients.remove(entry.getKey());
			}
		}
	}
	
}
