package com.codlex.audio.final_project.model;

import com.google.common.primitives.Doubles;

import lombok.Getter;

@Getter
public class QueryResult implements Comparable<QueryResult>  {
	
	private WordModel word;
	
	private double result;
	
	// TODO: figure out how this will be represented
	private Object trelis;
	
	@Override
	public int compareTo(final QueryResult other) {
		return -Doubles.compare(this.result, other.result);
	}
	
	// dummy until I finish actual speech recoginition
	public QueryResult(WordModel word, double result) {
		this.word = word;
		this.result = result;
	}
	
	public boolean hasFound() {
		return this.word != null;
	}
	
}
