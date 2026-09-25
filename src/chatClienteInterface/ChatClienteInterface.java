package chatClienteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClienteInterface extends Remote{
	void receiveMessage(String sender, String message) throws RemoteException;
}
