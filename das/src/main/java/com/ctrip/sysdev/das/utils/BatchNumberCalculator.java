package com.ctrip.sysdev.das.utils;

import java.util.ArrayList;
import java.util.List;

public class BatchNumberCalculator {
	
	private static long[] bucketSizes = new long[] {
			1, 2, 5, 
			10, 20, 50, 
			100, 200, 500, 
			1000, 2000, 5000};

	public static List<Long> getBuckets(long number) {
		List<Long> buckets = new ArrayList<Long>();
		while (number > 0) {
			long size = 1;
			for(int i = bucketSizes.length - 1; i >=0; i--) {
				if(number > bucketSizes[i]){
					size = bucketSizes[i];
					break;
				}
			}
				
			number -= size;
			buckets.add(size);
		}
		
		return buckets;
	}
	
	public static void main(String[] args) {
		System.out.println("12345: " + getBuckets(12345));
		System.out.println("1234: " + getBuckets(1234));
		System.out.println("123: " + getBuckets(123));
		System.out.println("12: " + getBuckets(12));
	}
}
