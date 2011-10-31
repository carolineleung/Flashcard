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
	private int TOTAL_NUM_QUESTIONS = 10;

	private int numQuestionsRight, numQuestionsWrong;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcard_main);

		initializeFields();
		populateQuestionAndHideAnswer(false);
		setupButtonListners();

	}

	private void populateQuestionAndHideAnswer(boolean incrementQuestion) {
		if (incrementQuestion && moreQuestions()) {
			questionIndex++;
		}

		int displayIndex = questionIndex + 1;
		question.setText("Question " + displayIndex);
		animateAnswerVisibility(View.INVISIBLE);
		StringBuilder questionOrderText = new StringBuilder("<b>").append(displayIndex).append("</b>/").append(TOTAL_NUM_QUESTIONS);
		Spanned renderedText = Html.fromHtml(questionOrderText.toString());
		questionOrder.setText(renderedText);
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
			int displayIndex = questionIndex + 1;
			answer.setText("Answer " + displayIndex + " - Blah Blah Blah ...");
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
		builder.setMessage("Total number of questions: " + TOTAL_NUM_QUESTIONS + "\nNumber Right: " + numQuestionsRight + "\nNumber Wrong: " + numQuestionsWrong);
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
		return questionIndex < TOTAL_NUM_QUESTIONS - 1;
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