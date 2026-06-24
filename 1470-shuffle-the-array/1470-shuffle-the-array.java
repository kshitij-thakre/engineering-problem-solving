class Solution {
    public int[] shuffle(int[] nums, int n) {
           int[] ans = new int[2 * n];

        for(int i = 0; i < n; i++) {

            ans[2 * i] = nums[i];

            ans[2 * i + 1] = nums[i + n];
        }

        return ans;
    }
}

///VS code
package ShuffleTheArray;

import java.util.Arrays;

public class Solution {
    public static void main(String [] args){
        int[] nums = {2,5,1,3,4,7};
        int n = 3;

        int[] ans = shuffleBruteForce(nums,n);

        System.out.println(Arrays.toString(ans));
    }

/*  public static int[] shuffle(int[] nums, int n) {
        int[] a1 = Arrays.copyOfRange(nums,0,n);
        int[] a2 = Arrays.copyOfRange(nums,n,nums.length);

        int i = 0;
        int j = 0;

        for(int k = 0; k< nums.length; k++){
            nums[k] = a1[i];
            i++;
            nums[k] = a2[j];
            j++;
        }
        return nums;
    }*/

    public static int[] shuffleOP(int[] nums, int n) {
        
        int [] ans = new int[2*n];
        for(int i = 0; i<n; i++){
            ans[2*i] = nums[i];
            ans[2*i+1] = nums[i+n];
        }

        return ans;
    }

    ///Brrute force solution.
    
    public static int[] shuffleBruteForce(int[] nums, int n) {
        int[]a1 = new int[n];
        int[]a2 = new int[n];

        for(int i = 0; i<n; i++){
            a1[i] = nums[i];
        }
        for(int i=0; i<n; i++){
            a2[i] = nums[i+n];
        }

        int []ans = new int[2*n];
        int index = 0;
        for(int i = 0; i<n; i++){
            ans[index++] = a1[i];
            ans[index++] = a2[i];
        }
        return ans;
    }

    ///Final optimal solution by not creating extra space.
    ///Use constrain given properly.
    
    ///The constraints are usually:
    // 1 <= nums[i] <= 1000
    public static int[] shuffle(int[] nums, int n) {
        for(int i =0; i<n; i++){
            nums[i] = nums[i] + 1024 * nums[i+n];
        }
                int index = 2*n - 1;

        for(int i = n - 1; i >= 0; i--) {

            int y = nums[i] / 1024;

            int x = nums[i] % 1024;

            nums[index--] = y;

            nums[index--] = x;
        }
        return nums;
    }
    
}
