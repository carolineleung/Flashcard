package com.caroldev.flashcard.model;

import java.util.List;
import java.util.Map;

public class Card {

	public static final String FIELD_PREFIX = "f";
	private static final String QUESTION_KEY = FIELD_PREFIX + 0;
	private static final String ANSWER_KEY = FIELD_PREFIX + 1;

	private List<String> tags;
	private Map<String, String> fields;

	public String getQuestion() {
		return fields.get(QUESTION_KEY);
	}

	public String getAnswer() {
		return fields.get(ANSWER_KEY);
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

}
