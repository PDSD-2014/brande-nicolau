package ro.pub.cs.thinkit.gui;

import java.io.Serializable;

import ro.pub.cs.thinkit.R;
import ro.pub.cs.thinkit.game.Constants;
import ro.pub.cs.thinkit.network.NetworkManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class StartFragment extends Fragment implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "START_GAME";
	private View view;
	private NetworkManager networkManager;
	private GameFragment gameFragment;
	private ChatFragment chatFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		gameFragment = (GameFragment) args.getSerializable(Constants.GAME_FRAGMENT);
		chatFragment = (ChatFragment) args.getSerializable(Constants.CHAT_FRAGMENT);
		view = inflater.inflate(R.layout.start_game_fragment, container, false);
		view.findViewById(R.id.startGame).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (networkManager != null) {
					networkManager.write(Constants.START_GAME.getBytes());
					showToast(Constants.REQUEST_SENT);
					Log.v(TAG, "I want to start a game.");
				}
			}
		});

		view.findViewById(R.id.startChat).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (networkManager != null) {
					networkManager.write(Constants.CHAT_REQUEST_SENT.getBytes());
					showToast(Constants.CHAT_REQUEST_SENT);
					Log.v(TAG, "I want to start a chat.");
				}
			}
		});

		return view;
	}

	/**
	 * Alert window displayed when new game request occurs.
	 * 
	 * @param name
	 *            : opponent's name
	 */
	public void newChallenge(String name) {
		new AlertDialog.Builder(view.getContext()).setTitle("New Challenge!")
				.setMessage("Do you wanna play a game with " + name + "?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						networkManager.write(Constants.ACCEPT_GAME.getBytes());
						getFragmentManager().beginTransaction()
								.replace(R.id.container_root, gameFragment, Constants.GAME_FRAGMENT).commit();
						Log.v(TAG, "Accepted game request.");
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						networkManager.write(Constants.CANCEL_REQUEST.getBytes());
						Log.v(TAG, "Rejected game request.");
					}
				}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	public void newChat(String name) {
		new AlertDialog.Builder(view.getContext()).setTitle("New Chat!")
				.setMessage("Do you wanna chat with " + name + "?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						networkManager.write(Constants.ACCEPT_CHAT.getBytes());
						getFragmentManager().beginTransaction()
								.replace(R.id.container_root, chatFragment, Constants.CHAT_FRAGMENT).commit();
						Log.v(TAG, "Accepted chat request.");
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						networkManager.write(Constants.CANCEL_REQUEST.getBytes());
						Log.v(TAG, "Rejected request.");
					}
				}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	public void challengeDenied() {
		showToast(Constants.CHALLENGE_DENIED);
	}

	public void showToast(String message) {
		Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
	}

	public void setNetworkManager(NetworkManager obj) {
		networkManager = obj;
	}

}
