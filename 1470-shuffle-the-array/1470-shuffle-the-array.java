class Solution {
    public int[] shuffle(int[] nums, int n) {
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