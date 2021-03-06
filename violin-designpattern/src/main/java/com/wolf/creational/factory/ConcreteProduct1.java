package com.wolf.creational.factory;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/14
 * Time: 9:29
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ConcreteProduct1 implements Product {

	//用来被加载类时调用，注册到工厂中
	static {
		SimpleFactory.register("concreteProduct1",ConcreteProduct1.class);
	}

	@Override
	public void print() {
		System.out.println("ConcreteProduct1...");
	}

	@Override
	public Product getProduct() {
		return new ConcreteProduct1();
	}


}
