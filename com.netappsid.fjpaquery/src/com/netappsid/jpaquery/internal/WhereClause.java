package com.netappsid.jpaquery.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.netappsid.jpaquery.OnGoingCondition;
import com.netappsid.jpaquery.OnGoingLogicalOperation;

public class WhereClause<T> implements OnGoingCondition<T>, OnGoingLogicalOperation {
	private Condition<?> condition;
	private final QueryBuilder queryBuilder;
	private final Method method;

	public WhereClause(QueryBuilder queryBuilder, Method method) {
		this.queryBuilder = queryBuilder;
		this.method = method;
	}

	@Override
	public OnGoingLogicalOperation eq(T value) {
		condition = new EqualCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), value);
		return this;
	}

	@Override
	public OnGoingLogicalOperation neq(T value) {
		condition = new NotEqualCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), value);
		return this;
	}

	@Override
	public OnGoingLogicalOperation lt(T value) {
		condition = new LtCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), value);
		return this;
	}

	@Override
	public OnGoingLogicalOperation lte(T value) {
		condition = new LteCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), value);
		return this;
	}

	@Override
	public OnGoingLogicalOperation gt(T value) {
		condition = new GtCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), value);
		return this;
	}

	@Override
	public OnGoingLogicalOperation gte(T value) {
		condition = new GteCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), value);
		return this;
	}

	@Override
	public OnGoingLogicalOperation isNull() {
		condition = new IsNullCondition(new SimpleMethodCallSelector(method));
		return this;
	}

	@Override
	public OnGoingLogicalOperation isNotNull() {
		condition = new IsNotNullCondition(new SimpleMethodCallSelector(method));
		return this;
	}

	@Override
	public OnGoingLogicalOperation in(T... values) {
		return in(Arrays.asList(values));
	}

	@Override
	public OnGoingLogicalOperation in(List<T> values) {
		condition = new InCondition<T>(new SimpleMethodCallSelector(method), queryBuilder.generateVariable(method), values);
		return this;
	}

	public String createQueryFragment(QueryBuilder queryBuilder) {
		return condition.createQueryFragment(queryBuilder);
	}

	public String getVariableName() {
		return condition.getVariableName();
	}

	public Object getValue() {
		return condition.getValue();
	}

	@Override
	public <T1> OnGoingCondition<T1> and(T1 property) {
		return (OnGoingCondition<T1>) this;
	}

	@Override
	public <T1> OnGoingCondition<T1> or(T1 property) {
		return (OnGoingCondition<T1>) this;
	}

}
