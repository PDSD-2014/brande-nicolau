package ro.pub.cs.thinkit.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import ro.pub.cs.thinkit.R;
import ro.pub.cs.thinkit.game.Constants;
import ro.pub.cs.thinkit.game.Question;
import ro.pub.cs.thinkit.game.QuestionService;
import ro.pub.cs.thinkit.network.NetworkManager;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
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
	private GameTimer gameTimer;
	private ProgressBar myProgressBar;
	private ProgressBar opponentProgressBar;
	private int questionId;
	private QuestionService qs = QuestionService.getInstance(getActivity());
	private Question question;
	private ArrayList<Button> buttons;
	private int myScoreSituation = 0;
	private int opponentScoreSituation = 0;
	private boolean roundEnded = false;
	private boolean timerStarted = false;

	private HashMap<Integer, Boolean> previousQuestions = new HashMap<Integer, Boolean>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.game_fragment, container, false);
		connectWithFrameFields();
		populateFrameFields(questionId);

		myName.setText(Constants.MY_NAME);
		opponentName.setText(Constants.OPPONENT_NAME);
		buttons = new ArrayList<Button>();
		buttons.add(answer1);
		buttons.add(answer2);
		buttons.add(answer3);
		buttons.add(answer4);

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
		gameTimer = new GameTimer(this);
	}

	public TextView getTimer() {
		return timer;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public void populateFrameFields(int questionId) {
		myScore.setText(String.valueOf(myScoreSituation) + " pts");
		opponentScore.setText(String.valueOf(opponentScoreSituation) + " pts");
		question = qs.getQuestions().get(questionId);
		questionText.setText(question.getQuestion());

		ArrayList<String> answers = question.getAnswers();
		Collections.shuffle(answers);

		answer1.setText(answers.get(0));
		answer2.setText(answers.get(1));
		answer3.setText(answers.get(2));
		answer4.setText(answers.get(3));

		if (!timerStarted) {
			gameTimer.start();
			timerStarted = true;
		}

	}

	public void sendId(String tag) {
		Random rand = new Random();
		int randInt;
		do {
			randInt = rand.nextInt(qs.getQuestions().size());
		} while (previousQuestions.get(randInt) != null);
		String message = tag + randInt;
		networkManager.write(message.getBytes());
		previousQuestions.put(randInt, true);
		questionId = randInt;
	}

	/**
	 * Calculates the round score.
	 * 
	 * @param correctAnswer
	 *            1 if the correct answer was selected, 0 if the incorrect
	 *            answer was selected
	 * @return
	 */
	private int calculateRoundScore(int correctAnswer) {
		int remainingTime = gameTimer.getRemainingTime();
		return correctAnswer * (Constants.QUESTION_SCORE * question.getDifficulty() + remainingTime);
	}

	/**
	 * Updates the gui with the current user's round results.
	 * 
	 * @param correctAnswer
	 *            1 if the correct answer was selected, 0 if the incorrect
	 *            answer was selected
	 */
	private void updateMyRoundResult(int correctAnswer) {
		roundEnded = true;
		myScoreSituation += calculateRoundScore(correctAnswer);
		myScore.setText(String.valueOf(myScoreSituation) + " pts");
		myProgressBar.setProgress(myScoreSituation);
		networkManager.write((Constants.REPORTED_ROUND_RESULT + myScoreSituation).getBytes());
	}

	/**
	 * Update the gui with the opponent's round result.
	 * 
	 * @param roundResult
	 */
	public void updateOpponentRoundResult(int roundResult) {
		opponentScoreSituation += roundResult;
		opponentScore.setText(String.valueOf(opponentScoreSituation) + " pts");
		opponentProgressBar.setProgress(opponentScoreSituation);
	}

	/**
	 * Updates the GUI when the time is exceeded.
	 */
	public void handleTimeExpired() {
		roundEnded = true;
		viewCorrectAnswer();
		updateMyRoundResult(0);
	}

	/**
	 * Colors the right answer in CYAN.
	 */
	private void viewCorrectAnswer() {
		for (Button btn : buttons) {
			if (btn.getText().equals(question.getCa())) {
				btn.setBackgroundColor(Color.CYAN);
				break;
			}
		}
	}

	private void cancelTimer() {
		gameTimer.cancel();
	}

	private class ButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (roundEnded == false) {
				Button selectedButton = (Button) v;
				if (selectedButton.getText().equals(question.getCa())) {
					selectedButton.setBackgroundColor(Color.GREEN);
					updateMyRoundResult(1);
					Log.v(TAG, "Correct answer pressed.");
				} else {
					selectedButton.setBackgroundColor(Color.RED);
					updateMyRoundResult(0);
					Log.v(TAG, "Wrong answer pressed.");
					viewCorrectAnswer();
				}
				cancelTimer();
			}
		}

	}

}
