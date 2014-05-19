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
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
	private ArrayList<Button> buttons;
	private Drawable originalButtonDrawable;

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
	private int myScoreSituation = 0;
	private int opponentScoreSituation = 0;
	private boolean iEndedRound = false;
	private boolean opponentEndedRound = false;
	private boolean displayed = false;
	private boolean gameMaster = false;
	private boolean timerStarted = false;

	private Handler handler = new Handler();
	private HashMap<Integer, Boolean> previousQuestions = new HashMap<Integer, Boolean>();
	private StartFragment startFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = getArguments();
		startFragment = (StartFragment) args.getSerializable("startFragment");
		view = inflater.inflate(R.layout.game_fragment, container, false);

		connectWithFrameFields();
		populateFrameFields(questionId);

		myName.setText(Constants.MY_NAME);
		opponentName.setText(Constants.OPPONENT_NAME);

		// handle buttons
		originalButtonDrawable = answer1.getBackground();
		buttons = new ArrayList<Button>();
		buttons.add(answer1);
		buttons.add(answer2);
		buttons.add(answer3);
		buttons.add(answer4);
		ButtonListener buttonListener = new ButtonListener();
		for (Button b : buttons) {
			b.setOnClickListener(buttonListener);
		}
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

	/**
	 * Sets the user as game master.
	 */
	public void setGameMaster() {
		gameMaster = true;
	}

	/**
	 * Informs the user if he is the game master.
	 * 
	 */
	private boolean gameMaster() {
		return gameMaster;
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
	 * Adds question id to map of played questions.
	 * 
	 * @param id
	 */
	public void indexQuestion(int id) {
		previousQuestions.put(id, true);
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
		iEndedRound = true;
		myScoreSituation += calculateRoundScore(correctAnswer);
		myScore.setText(String.valueOf(myScoreSituation) + " pts");
		myProgressBar.setProgress(myScoreSituation);
		networkManager.write((Constants.REPORTED_ROUND_RESULT + myScoreSituation).getBytes());

		if (timerStarted)
			cancelTimer();

		handler.postDelayed(new Runnable() {
			public void run() {
				if (gameMaster() && roundFinished() && !isFinalRound()) {
					initiateNextRound();
				} else if (gameFinished()) {
					if (!displayed) {
						displayResult(getMatchResult());
						displayed = true;
						gameMaster = false;
					}
				}
			}
		}, 2000);
	}

	/**
	 * Update the gui with the opponent's round result.
	 * 
	 * @param roundResult
	 */
	public void updateOpponentRoundResult(int roundResult) {
		opponentEndedRound = true;
		opponentScoreSituation = roundResult;
		opponentScore.setText(String.valueOf(opponentScoreSituation) + " pts");
		opponentProgressBar.setProgress(opponentScoreSituation);

		handler.postDelayed(new Runnable() {
			public void run() {
				if (gameMaster() && roundFinished() && !isFinalRound()) {
					initiateNextRound();
				} else if (gameFinished()) {
					if (!displayed) {
						displayResult(getMatchResult());
						displayed = true;
						gameMaster = false;
					}
				}
			}
		}, 2000);
	}

	/**
	 * 
	 * @return true if this is the final round, false otherwise.
	 */
	private boolean isFinalRound() {
		return previousQuestions.size() == Constants.NO_ROUNDS;
	}

	private void resetGame() {
		myScore.setText("0 pts");
		opponentName.setText("0 pts");
		myProgressBar.setProgress(0);
		opponentProgressBar.setProgress(0);
		myScoreSituation = 0;
		opponentScoreSituation = 0;
		iEndedRound = false;
		opponentEndedRound = false;
		displayed = false;
		timerStarted = false;
		previousQuestions.clear();
		buttons.clear();
	}

	/**
	 * 
	 * @return true if the game has ended, false otherwise.
	 */
	private boolean gameFinished() {
		return roundFinished() && isFinalRound();
	}

	/**
	 * Returns string based on score situation.
	 */
	private String getMatchResult() {
		if (myScoreSituation < opponentScoreSituation) {
			return "You Lost!";
		} else if (myScoreSituation > opponentScoreSituation) {
			return "You Won!";
		} else {
			return "It's a tie!";
		}
	}

	/**
	 * Shows a pop-up with the match results: Winner, Loser or Tie.
	 * 
	 * @param message
	 */
	public void displayResult(String message) {
		new AlertDialog.Builder(view.getContext()).setTitle("Game Results").setMessage(message)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						getFragmentManager().beginTransaction().replace(R.id.container_root, startFragment).commit();
						resetGame();
					}
				}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	/**
	 * Resets the round's data.
	 */
	public void resetRoundData() {
		timerStarted = false;
		iEndedRound = false;
		opponentEndedRound = false;

		answer1.setBackground(originalButtonDrawable);
		answer2.setBackground(originalButtonDrawable);
		answer3.setBackground(originalButtonDrawable);
		answer4.setBackground(originalButtonDrawable);

	}

	/**
	 * Informs the user if both users have answered this round's question.
	 * 
	 */
	private boolean roundFinished() {
		return iEndedRound && opponentEndedRound;
	}

	/**
	 * A new round is initiated.
	 */
	private void initiateNextRound() {
		sendId(Constants.RENEW_QUESTION);
		resetRoundData();
		populateFrameFields(questionId);
	}

	/**
	 * Updates the GUI when the time is exceeded.
	 */
	public void handleTimeExpired() {
		iEndedRound = true;
		timerStarted = false;
		viewCorrectAnswer();
		updateMyRoundResult(0);
	}

	/**
	 * Colors the right answer in GREEN.
	 */
	private void viewCorrectAnswer() {
		for (Button b : buttons) {
			if (b.getText().equals(question.getCa())) {
				b.setBackgroundColor(Color.GREEN);
				break;
			}
		}
	}

	/**
	 * Stops the timer.
	 */
	private void cancelTimer() {
		gameTimer.cancel();
		/* Reset the timer for the new round. */
		timerStarted = false;
	}

	private class ButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (iEndedRound == false) {
				Button selectedButton = (Button) v;
				if (selectedButton.getText().equals(question.getCa())) {
					selectedButton.setBackgroundColor(Color.GREEN);
					updateMyRoundResult(1);
					Log.v(TAG, "Correct answer pressed.");
				} else {
					selectedButton.setBackgroundColor(Color.RED);
					viewCorrectAnswer();
					updateMyRoundResult(0);
					Log.v(TAG, "Wrong answer pressed.");
				}
			}
		}

	}

}
