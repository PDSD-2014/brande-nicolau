package ro.pub.cs.thinkit.gui;

import java.io.Serializable;

import ro.pub.cs.thinkit.R;
import ro.pub.cs.thinkit.network.NetworkManager;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartGameFragment extends Fragment implements Serializable {
	private static final long serialVersionUID = 1L;
	private View view;
	private NetworkManager networkManager;
	private QuizFragment quizFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		quizFragment = (QuizFragment) args.getSerializable("quizFragment");
		view = inflater.inflate(R.layout.start_game_fragment, container, false);
		view.findViewById(R.id.startGame).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (networkManager != null) {
					networkManager.write("I want to start a game.".getBytes());
					Log.v("START_GAME", "I want to start a game.");
				}
				getFragmentManager().beginTransaction().replace(R.id.container_root, quizFragment).commit();
				Log.v("START_GAME", "Change to quizFragment.");
			}
		});
		return view;
	}

	public void setNetworkManager(NetworkManager obj) {
		networkManager = obj;
	}

}
