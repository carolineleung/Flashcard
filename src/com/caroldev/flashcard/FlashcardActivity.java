package com.caroldev.flashcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.caroldev.flashcard.model.Card;
import com.caroldev.flashcard.model.CardDeck;
import com.caroldev.flashcard.util.ImportExportManager;

public class FlashcardActivity extends Activity {

	private TextView question;
	private TextView answer;

	private TextView questionOrder;
	private TextView rightAnswerCount;
	private TextView wrongAnswerCount;

	private Button viewAnswerBtn;
	private Button rightBtn;
	private Button wrongBtn;

	private int questionIndex;

	private int numQuestionsRight, numQuestionsWrong;
	private CardDeck currDeck;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcard_main);
		importFlashcards();
		initializeFields();
		populateQuestionAndHideAnswer(false);
		setupButtonListners();
	}

	private void importFlashcards() {
		ImportExportManager importMgr = new ImportExportManager(this);
		currDeck = importMgr.importFromResourceFile();
	}

	private void populateQuestionAndHideAnswer(boolean incrementQuestion) {
		if (incrementQuestion && moreQuestions()) {
			questionIndex++;
		}
		question.setText(getCurrentCard().getQuestion());
		animateAnswerVisibility(View.INVISIBLE);

		StringBuilder questionOrderText = new StringBuilder("<b>").append("" + (questionIndex + 1)).append("</b>/").append(getNumCards());
		Spanned renderedText = Html.fromHtml(questionOrderText.toString());
		questionOrder.setText(renderedText);
	}

	private Card getCurrentCard() {
		return currDeck.getCards().get(questionIndex);
	}

	private int getNumCards() {
		return currDeck.getCards().size();
	}

	private void setupButtonListners() {
		viewAnswerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateButtonsState(true);
			}
		});

		View.OnClickListener rightWrongButtonListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.right_button:
					rightOrWrongButtonClicked(true);
					break;
				case R.id.wrong_button:
					rightOrWrongButtonClicked(false);
					break;
				}
			}
		};
		rightBtn.setOnClickListener(rightWrongButtonListener);
		wrongBtn.setOnClickListener(rightWrongButtonListener);
	}

	private void updateButtonsState(boolean viewAnswer) {
		if (viewAnswer) {
			answer.setText(getCurrentCard().getAnswer());
			question.setTypeface(Typeface.DEFAULT);
		} else {
			question.setTypeface(Typeface.DEFAULT_BOLD);
		}
		int answerVisibility = viewAnswer ? View.VISIBLE : View.INVISIBLE;
		int viewAnswerBtnVisibility = viewAnswer ? View.INVISIBLE : View.VISIBLE;

		animateAnswerVisibility(answerVisibility);
		rightBtn.setVisibility(answerVisibility);
		wrongBtn.setVisibility(answerVisibility);
		viewAnswerBtn.setVisibility(viewAnswerBtnVisibility);
	}

	private void animateAnswerVisibility(int answerVisibility) {
		int answerFadeInOrOut = answerVisibility == View.VISIBLE ? android.R.anim.fade_in : android.R.anim.fade_out;
		answer.startAnimation(AnimationUtils.loadAnimation(this, answerFadeInOrOut));
		answer.setVisibility(answerVisibility);
	}

	private void rightOrWrongButtonClicked(boolean rightAnswer) {
		updateAnswerCount(rightAnswer);
		if (moreQuestions()) {
			updateButtonsState(false);
			populateQuestionAndHideAnswer(true);
		} else {
			disableButtons();
			displayResultInDialog();
		}
	}

	private void disableButtons() {
		rightBtn.setEnabled(false);
		wrongBtn.setEnabled(false);
	}

	private void displayResultInDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Quiz Results");
		builder.setMessage("Total number of questions: " + getNumCards() + "\nNumber Right: " + numQuestionsRight + "\nNumber Wrong: " + numQuestionsWrong);
		builder.setNegativeButton("Start over", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				restartFlashcard();
			}

		});

		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alertDialog = builder.create();
		alertDialog.show();

	}

	private void restartFlashcard() {
		Intent intent = new Intent(FlashcardActivity.this, FlashcardActivity.class);
		startActivity(intent);
	}

	private boolean moreQuestions() {
		return questionIndex < getNumCards() - 1;
	}

	private void updateAnswerCount(boolean rightAnswer) {
		if (rightAnswer) {
			numQuestionsRight++;
			rightAnswerCount.setText("Right: " + numQuestionsRight);
		} else {
			numQuestionsWrong++;
			wrongAnswerCount.setText("Wrong: " + numQuestionsWrong);
		}
	}

	private void initializeFields() {
		question = (TextView) findViewById(R.id.question);
		answer = (TextView) findViewById(R.id.answer);
		questionOrder = (TextView) findViewById(R.id.question_number);
		viewAnswerBtn = (Button) findViewById(R.id.view_answer);
		rightBtn = (Button) findViewById(R.id.right_button);
		wrongBtn = (Button) findViewById(R.id.wrong_button);
		rightAnswerCount = (TextView) findViewById(R.id.right_answer_count);
		wrongAnswerCount = (TextView) findViewById(R.id.wrong_answer_count);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.flashcard_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_start_over:
			restartFlashcard();
		}
		return super.onOptionsItemSelected(item);
	}
}