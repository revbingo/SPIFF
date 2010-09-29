package com.revbingo.spiff.binders;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;

public class TestCaseCollectionBinder {

	@Test
	public void collectionIsInstantiatedOnFirstCall() throws Exception {
		ObjectCollectionBinder unit = new ObjectCollectionBinder(CollectionTestClass.class.getDeclaredField("integers"), Integer.class);
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		
		assertThat(target.integers, is(instanceOf(ArrayList.class)));
	}
	
	@Test
	public void subsequentBindsAddsToCollection() throws Exception {
		ObjectCollectionBinder unit = new ObjectCollectionBinder(CollectionTestClass.class.getDeclaredField("integers"),Integer.class);
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		unit.bind(target, 11);
		unit.bind(target, 12);
		
		assertThat(target.integers.size(), is(3));
		assertThat(target.integers.get(0), is(10));
		assertThat(target.integers.get(1), is(11));
		assertThat(target.integers.get(2), is(12));
	}
	
	@Test
	public void setsAreSupported() throws Exception {
		ObjectCollectionBinder unit = new ObjectCollectionBinder(CollectionTestClass.class.getDeclaredField("aSet"), Integer.class);
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		unit.bind(target, 11);
		unit.bind(target, 10);
		
		assertThat(target.aSet, instanceOf(HashSet.class));
		assertThat(target.aSet.size(), is(2));
	}
	
	@Test
	public void queuesAreSupported() throws Exception {
		ObjectCollectionBinder unit = new ObjectCollectionBinder(CollectionTestClass.class.getDeclaredField("aQueue"), Integer.class);
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		unit.bind(target, 11);
		unit.bind(target, 12);
		
		assertThat(target.aQueue, instanceOf(LinkedList.class));
		assertThat(target.aQueue.size(), is(3));
	}
	
	@Test
	public void doesntOverwriteExistingCollection() throws Exception {
		ObjectCollectionBinder unit = new ObjectCollectionBinder(CollectionTestClass.class.getDeclaredField("aTreeSet"), Integer.class);
		
		CollectionTestClass target = new CollectionTestClass();
		
		TreeSet<Integer> existingSet = new TreeSet<Integer>();
		existingSet.add(10);
		
		target.aTreeSet = existingSet;
		
		unit.bind(target, 11);
		unit.bind(target, 12);
		
		assertThat(target.aTreeSet, is(existingSet));
		assertThat(target.aTreeSet.size(), is(3));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfCollectionBinderCreatedForNonCollectionField() throws Exception {
		new ObjectCollectionBinder(CollectionTestClass.class.getDeclaredField("notACollection"), Integer.class);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfCreateAndBindCalledForPrimitiveCollectionBinder() throws Exception {
		new PrimitiveCollectionBinder(CollectionTestClass.class.getDeclaredField("integers")).createAndBind(null);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfPrimitiveCollectionBinderUsedForUnknownCollectionType() throws Exception {
		Binder unit = new PrimitiveCollectionBinder(CollectionTestClass.class.getDeclaredField("unknownCollection"));
		unit.bind(new CollectionTestClass(), null);
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfFieldIsMadeInaccessibleAgain() throws Exception {
		Field f = CollectionTestClass.class.getDeclaredField("privates");
		Binder unit = new PrimitiveCollectionBinder(f);
		f.setAccessible(false);
		unit.bind(new CollectionTestClass(), null);
	}
	
	@Test
	public void newInstanceOfWrappedTypeCreatedAndAddedToCollection() throws Exception {
		Field f = CollectionTestClass.class.getDeclaredField("basic");
		Binder unit = new ObjectCollectionBinder(f, BasicType.class);
		
		CollectionTestClass target = new CollectionTestClass();
		Object newInstance = unit.createAndBind(target);
		
		assertThat(target.basic, is(notNullValue()));
		assertThat(target.basic.size(), is(1));
		
		assertThat(target.basic.get(0), instanceOf(BasicType.class));
		assertThat(target.basic.get(0), sameInstance(newInstance));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfWrappedTypeHasPrivateConstructor() throws Exception {
		Field f = CollectionTestClass.class.getDeclaredField("privateConstructor");
		Binder unit = new ObjectCollectionBinder(f, TestCaseFieldBinder.PrivateConstructor.class);
		
		CollectionTestClass target = new CollectionTestClass();
		unit.createAndBind(target);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfWrappedTypeRequiresArgsForConstruction() throws Exception {
		Field f = CollectionTestClass.class.getDeclaredField("gimmeArgs");
		Binder unit = new ObjectCollectionBinder(f, TestCaseFieldBinder.ArgsRequired.class);
		
		CollectionTestClass target = new CollectionTestClass();
		unit.createAndBind(target);
	}
	
	public static class CollectionTestClass {
		
		public List<Integer> integers;
		public Set<Integer> aSet;
		public Queue<Integer> aQueue;
		public TreeSet<Integer> aTreeSet;
		public int notACollection;
		public UnknownCollection unknownCollection;
		
		private List<Integer> privates;
		public List<BasicType> basic;
		public List<TestCaseFieldBinder.PrivateConstructor> privateConstructor;
		public List<TestCaseFieldBinder.ArgsRequired> gimmeArgs;
	}
	
	public interface UnknownCollection extends Collection {
		
	}
	
	public static class BasicType {
		
	}
}
