package com.pepper.lucene.common;

public abstract interface Operator<P, R> {
	public abstract R operate(P p) throws Exception;
}