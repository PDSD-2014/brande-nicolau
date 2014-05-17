package ro.pub.cs.thinkit.gui;

import ro.pub.cs.thinkit.game.Constants;
import android.os.CountDownTimer;
import android.widget.TextView;

public class GameTimer {

	private GameFragment gameFragment;
	private TextView timer;

	public GameTimer(GameFragment gameFragment) {
		this.gameFragment = gameFragment;
		this.timer = gameFragment.getTimer();
	}

	public void start() {
		CountDownTimer Count = new CountDownTimer(Constants.QUESTION_LIMIT_TIME, Constants.COUNTDOWN_INTERVAL) {
			public void onTick(long millisUntilFinished) {
				timer.setText(String.valueOf(millisUntilFinished / 1000));
			}

			public void onFinish() {
				timer.setText("Time Up!");
				gameFragment.handleTimeExpired();
			}
		};
		Count.start();
	}
}
