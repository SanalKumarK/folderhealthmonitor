package maths.soln.pblm;

/**
 * Mathematical Sum Problem
 * Find all the Sum Groups from the input array. 
 */
import java.util.ArrayList;

public class MathematicSumProblem2 {
	
	public static void main(String[] args) {
		int weight;
		
		ArrayList<Integer> nums = new ArrayList<>();
		nums.add(-1);
		nums.add(-2);
		nums.add(-4);
		nums.add(2);
		nums.add(5);		
		nums.add(4);
		nums.add(3);
		nums.add(1);
		weight = 6;
		new MathematicSumProblem2().
			findSumGroups(nums, weight, weight).
			forEach(System.out::println);
		
		ArrayList<Integer> nums1 = new ArrayList<>();
		
		nums1.add(22);		
		nums1.add(54);
		nums1.add(39);
		nums1.add(60);
		nums1.add(94);
		nums1.add(99);
		nums1.add(60);
		nums1.add(94);
		nums1.add(60);
		nums1.add(94);
		nums1.add(60);
		nums1.add(94);		
		weight = 99;
		new MathematicSumProblem2().
			findSumGroups(nums1, weight, weight)
			.forEach(System.out::println);
	}
	

	/**
	 * DO class to hold the Sum calculation result.
	 * 
	 * @author Sanal
	 *
	 */
	class Result {
		ArrayList<Integer> nums ;
		Integer sum;
		
		private Result(ArrayList<Integer> nums , Integer sum) {
			this.nums = nums;
			this.sum = sum;
		}
		
		@Override
		public String toString() {
			return sum.toString()+ " - " + nums.toString() ;
		}
	}
	
	/**
	 * Find the sum groups of the input array and sum.
	 * 
	 * @param nums array holding numbers
	 * @param sum 
	 * @param origSum origSum to find the result
	 * @return
	 */
	private ArrayList<Result> findSumGroups(ArrayList<Integer> nums, int sum, int origSum) {
		ArrayList<Result> results = new ArrayList<>();
		
		//if one element, returns the element.
		if (nums.size() == 1) {
			Integer num = nums.get(0);
			results.add(new Result(nums, num));
		} else {
			//Get all the combinations of numbers, and try to match Sum required 		
			
			//Get first element from the array
			Integer num = nums.get(0);
			
			//If num is equal to sum,
			if(num == sum) {
				ArrayList<Integer> tempArr = new ArrayList<>();
				tempArr.add(num);
				results.add(new Result(tempArr, sum));
			}
			
			//Number array without the current number.
			ArrayList<Integer> remArr = trimElement(nums, num);
			
			//Try to find the sum from the remaining array including the first element of the array
			ArrayList<Result> resultIncNum = findSumGroups(remArr, sum - num, origSum);
			
			//Try to find the sum from the remaining array excluding the first element of the array
			ArrayList<Result> resultExcNum = findSumGroups(remArr, sum, origSum);
			
			//Loop through the results by including the current element and try to find a match, 
			resultIncNum.forEach(result -> {
				//Try to find a match by adding the number and the sum from the remaining elements array
				int tempSum = num + result.sum;				
				if(tempSum == sum) { //If match found add the elements to the results.
					result.nums.add(num);
					result.sum = tempSum;
					results.add(result);
				}
			});
			
			//Loop through the results by excluding the current element
			resultExcNum.forEach(result -> {
				//Check if any match found for the SUM, if found add the element to the result. 
				if(result.sum == sum) {
					results.add(result);
				}
			});
		}
		return results;
	}
	
	/**
	 * Create a new cloned array and removes the element from the input array.
	 *  
	 * @param arr
	 * @param element
	 * @return
	 */
	private static ArrayList<Integer> trimElement(ArrayList<Integer> arr, Integer element) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer> newArr = (ArrayList<Integer>) arr.clone();
		newArr.remove(element);
		return newArr;
	}
}
