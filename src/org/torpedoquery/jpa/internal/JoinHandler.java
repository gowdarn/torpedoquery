/**
 *
 *   Copyright 2011 Xavier Jodoin xjodoin@gmail.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.torpedoquery.jpa.internal;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.objenesis.ObjenesisHelper;

public abstract class JoinHandler<T> implements QueryHandler<T>
{

	private final TorpedoMethodHandler methodHandler;
	private final ProxyFactoryFactory proxyFactoryFactory;
	private Class<T> realType;

	public JoinHandler(TorpedoMethodHandler methodHandler, ProxyFactoryFactory proxyFactoryFactory)
	{
		this.methodHandler = methodHandler;
		this.proxyFactoryFactory = proxyFactoryFactory;
	}

	public JoinHandler(TorpedoMethodHandler fjpaMethodHandler, ProxyFactoryFactory proxyFactoryFactory, Class<T> realType)
	{
		methodHandler = fjpaMethodHandler;
		this.realType = realType;
		this.proxyFactoryFactory = proxyFactoryFactory;
	}

	@Override
	public T handleCall(Map<Object, QueryBuilder<?>> proxyQueryBuilders, Deque<MethodCall> methodCalls)
	{

		MethodCall pollFirst = methodCalls.pollFirst();

		final QueryBuilder queryImpl = proxyQueryBuilders.get(pollFirst.getProxy());
		final Method thisMethod = pollFirst.getMethod();
		Class<?> returnType = thisMethod.getReturnType();

		if (Collection.class.isAssignableFrom(returnType))
		{
			returnType = (Class<?>) ((ParameterizedType) thisMethod.getGenericReturnType()).getActualTypeArguments()[0];
		}
		else if (Map.class.isAssignableFrom(returnType))
		{
			returnType = (Class<?>) ((ParameterizedType) thisMethod.getGenericReturnType()).getActualTypeArguments()[1];
		}

		try
		{

			final ProxyFactory proxyFactory = proxyFactoryFactory.getProxyFactory();
			proxyFactory.setFilter(new MethodFilter()
			{

				@Override
				public boolean isHandled(Method m)
				{
					return !m.getDeclaringClass().equals(Object.class);
				}
			});
			Class<? extends Object> goodType = getGoodType(returnType);
			proxyFactory.setSuperclass(goodType);
			proxyFactory.setInterfaces(new Class[] { Proxy.class });

			Class proxyClass = proxyFactory.createClass();
			ProxyObject join = (ProxyObject) ObjenesisHelper.newInstance(proxyClass);
			join.setHandler(methodHandler);

			final QueryBuilder queryBuilder = methodHandler.addQueryBuilder(join, new QueryBuilder(goodType));

			queryImpl.addJoin(createJoin(queryBuilder, FieldUtils.getFieldName(thisMethod)));

			return (T) join;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private Class<? extends Object> getGoodType(Class<?> returnType)
	{
		return realType != null ? realType : returnType;
	}

	protected abstract Join createJoin(QueryBuilder queryBuilder, String fieldName);

}