package ro.pub.cs.thinkit.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import ro.pub.cs.thinkit.gui.WiFiServiceDiscoveryActivity;
import android.os.Handler;
import android.util.Log;

public class ClientSocketHandler extends Thread {

	private static final String TAG = "ClientSocketHandler";
	private Handler handler;
	private NetworkManager networkManager;
	private InetAddress mAddress;

	public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
		this.handler = handler;
		this.mAddress = groupOwnerAddress;
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(), WiFiServiceDiscoveryActivity.SERVER_PORT), 5000);
			Log.d(TAG, "Launching the I/O handler");
			networkManager = new NetworkManager(socket, handler);
			new Thread(networkManager).start();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

}
