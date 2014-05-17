package ro.pub.cs.thinkit.gui;

import java.util.ArrayList;
import java.util.List;

import ro.pub.cs.thinkit.R;
import ro.pub.cs.thinkit.network.NetworkManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class QuizFragment extends Fragment {

	private View view;
	private NetworkManager networkManager;
	private TextView chatLine;
	private ListView listView;
	MessageAdapter adapter = null;
	private List<String> items = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_quiz, container, false);
		chatLine = (TextView) view.findViewById(R.id.txtChatLine);
		listView = (ListView) view.findViewById(android.R.id.list);
		adapter = new MessageAdapter(getActivity(), android.R.id.text1, items);
		listView.setAdapter(adapter);
		view.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (networkManager != null) {
					networkManager.write(chatLine.getText().toString().getBytes());
					pushMessage("Me: " + chatLine.getText().toString());
					chatLine.setText("");
					chatLine.clearFocus();
				}
			}
		});
		return view;
	}

	public interface MessageTarget {
		public Handler getHandler();
	}

	public void setNetworkManager(NetworkManager obj) {
		networkManager = obj;
	}

	public void pushMessage(String readMessage) {
		adapter.add(readMessage);
		adapter.notifyDataSetChanged();
	}

	/**
	 * ArrayAdapter to manage messages.
	 */
	public class MessageAdapter extends ArrayAdapter<String> {

		List<String> messages = null;

		public MessageAdapter(Context context, int textViewResourceId, List<String> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(android.R.layout.simple_list_item_1, null);
			}
			String message = items.get(position);
			if (message != null && !message.isEmpty()) {
				TextView nameText = (TextView) v.findViewById(android.R.id.text1);

				if (nameText != null) {
					nameText.setText(message);
					if (message.startsWith("Me: ")) {
						nameText.setTextAppearance(getActivity(), R.style.normalText);
					} else {
						nameText.setTextAppearance(getActivity(), R.style.boldText);
					}
				}
			}
			return v;
		}
	}
}
