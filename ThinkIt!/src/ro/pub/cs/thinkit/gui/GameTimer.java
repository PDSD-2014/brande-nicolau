package ro.pub.cs.thinkit.gui;

import ro.pub.cs.thinkit.game.Constants;
import android.os.CountDownTimer;
import android.widget.TextView;

public class GameTimer {

	private GameFragment gameFragment;
	private TextView timer;
	private CountDownTimer count;
	private int remainingTime;

	public GameTimer(GameFragment gameFragment) {
		this.gameFragment = gameFragment;
		this.timer = gameFragment.getTimer();
	}

	public void start() {
		count = new CountDownTimer(Constants.QUESTION_LIMIT_TIME, Constants.COUNTDOWN_INTERVAL) {
			public void onTick(long millisUntilFinished) {
				remainingTime = (int) (millisUntilFinished / 1000);
				timer.setText(String.valueOf(millisUntilFinished / 1000));
			}

			public void onFinish() {
				remainingTime = 0;
				timer.setText("Time Up!");
				gameFragment.handleTimeExpired();
			}
		};
		count.start();
	}

	public void cancel() {
		remainingTime = Integer.parseInt(timer.getText().toString());
		count.cancel();
	}

	public int getRemainingTime() {
		return remainingTime;
	}
}
