package com.revbingo.spiff.binders;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
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
		CollectionBinder unit = new CollectionBinder(CollectionTestClass.class.getDeclaredField("integers"));
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		
		assertThat(target.integers, is(instanceOf(ArrayList.class)));
	}
	
	@Test
	public void subsequentBindsAddsToCollection() throws Exception {
		CollectionBinder unit = new CollectionBinder(CollectionTestClass.class.getDeclaredField("integers"));
		
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
		CollectionBinder unit = new CollectionBinder(CollectionTestClass.class.getDeclaredField("aSet"));
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		unit.bind(target, 11);
		unit.bind(target, 10);
		
		assertThat(target.aSet, instanceOf(HashSet.class));
		assertThat(target.aSet.size(), is(2));
	}
	
	@Test
	public void queuesAreSupported() throws Exception {
		CollectionBinder unit = new CollectionBinder(CollectionTestClass.class.getDeclaredField("aQueue"));
		
		CollectionTestClass target = new CollectionTestClass();
		
		unit.bind(target, 10);
		unit.bind(target, 11);
		unit.bind(target, 12);
		
		assertThat(target.aQueue, instanceOf(LinkedList.class));
		assertThat(target.aQueue.size(), is(3));
	}
	
	@Test
	public void doesntOverwriteExistingCollection() throws Exception {
		CollectionBinder unit = new CollectionBinder(CollectionTestClass.class.getDeclaredField("aTreeSet"));
		
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
		CollectionBinder unit = new CollectionBinder(CollectionTestClass.class.getDeclaredField("notACollection"));
	}
	
	public static class CollectionTestClass {
		
		public List<Integer> integers;
		public Set<Integer> aSet;
		public Queue<Integer> aQueue;
		public TreeSet<Integer> aTreeSet;
		public int notACollection;
		
	}
}
