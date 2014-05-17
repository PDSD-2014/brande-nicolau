package ro.pub.cs.thinkit.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ro.pub.cs.thinkit.R;
import android.content.Context;
import android.util.SparseArray;

public class QuestionService {

    private static QuestionService INSTANCE = null;
    private Context context;
    private SparseArray<Question> questions;

    private QuestionService(Context context) {
	this.context = context;
	this.questions = new SparseArray<Question>();
    }

    public static QuestionService getInstance(Context context) {
	if (INSTANCE == null) {
	    INSTANCE = new QuestionService(context);
	    return INSTANCE;
	} else {
	    return INSTANCE;
	}
    }

    public SparseArray<Question> getQuestions() {
	return questions;
    }

    public void readQuestions() {
	InputStream inputStream = context.getResources().openRawResource(R.raw.db);

	InputStreamReader inputreader = new InputStreamReader(inputStream);
	BufferedReader buffreader = new BufferedReader(inputreader);
	String line;

	int counter = 0;
	try {
	    while ((line = buffreader.readLine()) != null) {
		String[] split = line.split("#");
		questions.put(counter, new Question(Integer.parseInt(split[0]), split[1], split[2], split[3], split[4],
			split[5], split[6]));
		counter++;
	    }
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
