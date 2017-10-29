package com.codlex.audio.hmm;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;

public class Sequence {

	final List<List<Double>> vectors;
	private int vectorSize;
	
	@FXML
	private String name;
	
	public Sequence(String name, int vectorSize) {
		this.name = name;
		this.vectorSize = vectorSize;
		this.vectors = new ArrayList<>();
		this.vectors.add(generateEmptyVector());
	}

	private List<Double> generateEmptyVector() {
		List<Double> arrayList = new ArrayList<>();
		for (int i = 0; i < vectorSize; i++) {
			arrayList.add(0.0);
		}
		return arrayList;
	}
	
	public void deleteVector(int index) {
		this.vectors.remove(index);
	}
	
	public void insertVector(int index, List<Double> vector) {
		this.vectors.add(index, vector);
	}
	
	public void insertEmpty(int index) {
		insertVector(index, generateEmptyVector());
		System.out.println("Now we have: " + this.vectors.size() + " vectors!");
	}

	public List<List<Double>> getVectors() {
		return this.vectors;
	}

	public int getVectorSize() {
		return this.vectorSize;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getName() {
		System.out.println("name : " + this.name);
		return this.name;
	}
	
	public Sequence getReference() {
		return this;
	}
}
