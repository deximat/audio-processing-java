package com.codlex.audio.transform;

public class ComplexNumber {

	private double real;
	private double immaginery;
	
	public ComplexNumber(double real) {
		this.real = real;
	}
	
	public ComplexNumber(double real, double immaginary) {
		this.real = real;
		this.immaginery = immaginary;
	}
	
	public double abs() {
		return Math.sqrt(this.real*this.real + this.immaginery * this.immaginery);
	}
	
	public ComplexNumber plus(final ComplexNumber other) {
		return new ComplexNumber(this.real + other.real, this.immaginery + other.immaginery);
	}
	
	public ComplexNumber multiply(final ComplexNumber other) {
		double realPart = this.real * other.real - this.immaginery * other.immaginery;
		double immagineryPart = this.real * other.immaginery + this.immaginery * other.real;
		return new ComplexNumber(realPart, immagineryPart);
	}

	public ComplexNumber minus(ComplexNumber other) {
		return plus(other.negative());
	}

	public ComplexNumber negative() {
		return new ComplexNumber(-this.real, -this.immaginery);
	}
}
