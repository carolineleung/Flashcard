package com.caroldev.flashcard.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.widget.Toast;

import com.caroldev.flashcard.R;
import com.caroldev.flashcard.common.FlashcardAppConstants;
import com.caroldev.flashcard.model.Card;
import com.caroldev.flashcard.model.CardDeck;

public class ImportExportManager {

	private static final String TAB = "\t";
	private final Activity activity;

	public ImportExportManager(Activity activity) {
		this.activity = activity;
	}

	public CardDeck importFromResourceFile() {
		Toast.makeText(activity.getApplicationContext(), "Loading list from embedded resource now...", Toast.LENGTH_LONG).show();
		InputStream inputStream = activity.getResources().openRawResource(R.raw.testdeck1_mod);
		return importItemsFromFile(inputStream);
	}

	private CardDeck importItemsImpl(BufferedReader br) throws IOException {
		CardDeck deck = new CardDeck();
		deck.setName(FlashcardAppConstants.TEST_DECK1);
		List<Card> cards = new ArrayList<Card>();
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			Card card = new Card();
			StringTokenizer tokenizer = new StringTokenizer(line, TAB);
			int numAttributes = tokenizer.countTokens();
			if (numAttributes > 2) {
				int i = 0;
				Map<String, String> fields = new HashMap<String, String>(tokenizer.countTokens());
				while (tokenizer.hasMoreTokens()) {
					fields.put(Card.FIELD_PREFIX + i++, tokenizer.nextToken());
				}
				card.setFields(fields);
			} else {
				AppLogger.d(getClass(), "Card attributes < 2, line is: " + line);
			}
			cards.add(card);
			// TODO insert item into DB
			// activity.getApplication().getContentManager().insertItemToDB(activity.getShoppingListId(), line, labels);
		}
		deck.setCards(cards);
		return deck;
	}

	public CardDeck importItemsFromFile(InputStream aFile) {
		CardDeck deck = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(aFile));
			deck = importItemsImpl(br);
		} catch (IOException e) {
			AppLogger.e(getClass(), "Error importing items from inputStream", e);
		}
		return deck;
	}

}
