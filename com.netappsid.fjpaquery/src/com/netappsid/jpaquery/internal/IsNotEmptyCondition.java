package com.netappsid.jpaquery.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IsNotEmptyCondition implements Condition {

	private final Selector selector;

	public IsNotEmptyCondition(Selector selector) {
		this.selector = selector;
	}

	@Override
	public String createQueryFragment(QueryBuilder queryBuilder, AtomicInteger incrementor) {
		return selector.createQueryFragment(queryBuilder, incrementor) + " is not empty ";
	}

	@Override
	public List<Parameter> getParameters() {
		return Collections.emptyList();
	}

}
