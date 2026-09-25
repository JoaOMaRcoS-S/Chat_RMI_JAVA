package chatServerInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chatClienteInterface.ChatClienteInterface;

public interface ChatServerInterface extends Remote{
	void registerClient(String username, ChatClienteInterface cliente) throws RemoteException;
	void unRegisterClient(String username) throws RemoteException;
	void sendMessage(String sender, String message) throws RemoteException;
}
