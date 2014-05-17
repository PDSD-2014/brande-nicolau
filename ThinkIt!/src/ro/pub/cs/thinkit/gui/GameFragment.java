package ro.pub.cs.thinkit.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import ro.pub.cs.thinkit.R;
import ro.pub.cs.thinkit.game.Question;
import ro.pub.cs.thinkit.game.QuestionService;
import ro.pub.cs.thinkit.network.NetworkManager;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameFragment extends Fragment implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "GAME";
	private View view;
	private NetworkManager networkManager;
	private Button answer1;
	private Button answer2;
	private Button answer3;
	private Button answer4;
	private TextView myName;
	private TextView myScore;
	private TextView opponentName;
	private TextView opponentScore;
	private TextView questionText;
	private TextView timer;
	private ProgressBar myProgressBar;
	private ProgressBar opponentProgressBar;
	private int questionId = 5;
	private Question question;
	private ArrayList<Button> buttons;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.game_fragment, container, false);
		connectWithFrameFields();
		buttons = new ArrayList<Button>();
		buttons.add(answer1);
		buttons.add(answer2);
		buttons.add(answer3);
		buttons.add(answer4);
		populateFrameFields(questionId);

		ButtonListener buttonListener = new ButtonListener();
		answer1.setOnClickListener(buttonListener);
		answer2.setOnClickListener(buttonListener);
		answer3.setOnClickListener(buttonListener);
		answer4.setOnClickListener(buttonListener);
		return view;
	}

	public void setNetworkManager(NetworkManager obj) {
		networkManager = obj;
	}

	private void connectWithFrameFields() {
		answer1 = (Button) view.findViewById(R.id.answer1);
		answer2 = (Button) view.findViewById(R.id.answer2);
		answer3 = (Button) view.findViewById(R.id.answer3);
		answer4 = (Button) view.findViewById(R.id.answer4);
		myName = (TextView) view.findViewById(R.id.myName);
		myScore = (TextView) view.findViewById(R.id.myScore);
		opponentName = (TextView) view.findViewById(R.id.opponentName);
		opponentScore = (TextView) view.findViewById(R.id.opponentScore);
		questionText = (TextView) view.findViewById(R.id.question);
		timer = (TextView) view.findViewById(R.id.timer);
		myProgressBar = (ProgressBar) view.findViewById(R.id.myProgress);
		opponentProgressBar = (ProgressBar) view.findViewById(R.id.opponentProgress);
	}

	private void populateFrameFields(int questionId) {
		QuestionService qs = QuestionService.getInstance(getActivity());
		question = qs.getQuestions().get(questionId);
		questionText.setText(question.getQuestion());

		ArrayList<String> answers = question.getAnswers();
		Collections.shuffle(answers);

		answer1.setText(answers.get(0));
		answer2.setText(answers.get(1));
		answer3.setText(answers.get(2));
		answer4.setText(answers.get(3));
	}

	private class ButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Button selectedButton = (Button) v;
			if (selectedButton.getText().equals(question.getCa())) {
				selectedButton.setBackgroundColor(Color.GREEN);
				Log.v(TAG, "Correct answer pressed.");
			} else {
				selectedButton.setBackgroundColor(Color.RED);
				Log.v(TAG, "Wrong answer pressed.");
				for (Button btn : buttons) {
					if (btn.getText().equals(question.getCa())) {
						btn.setBackgroundColor(Color.CYAN);
						break;
					}
				}
			}
		}

	}

}
