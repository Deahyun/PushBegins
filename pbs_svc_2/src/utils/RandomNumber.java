package utils;

import java.util.Random;

public class RandomNumber {
	
	//
	public String getSerialNumber() {
		Random rand = new Random();
		int min = 0;
		int max = 35;
		
		int nValue = 0;
		StringBuffer sb = new StringBuffer();

		for ( int i = 0; i < 25; i++ ) {
			// nextInt is normally exclusive of the top value,
			// so add 1 to make it inclusive
			int nRandom = rand.nextInt(max - min + 1) + min;
			if ( nRandom < 10 ) {
				nValue = nRandom + 48;
				
			} else if ( nRandom < 36 ) {
				nValue = nRandom + 55;
				
			} else {
				
			}
			String strTmp = String.format("%c", nValue);
			sb.append(strTmp);
			if ( i % 5 == 4 ) {
				if ( i < 24 ) {
					sb.append("-");
				}
			}
		}
		
		return sb.toString();
	}
	//
	
	//
	public String getRandom() {

		Random Rand = new Random(System.currentTimeMillis());
		int nRand = Rand.nextInt(999999);
		String strRand = String.format("%06d",  nRand);
		return strRand;

//		if ( false ) {
//			int start = 1;
//			int end = 6;
//			final int[] nums = new int[end - start + 1]; 
//
//			// Fill array
//			for (int i = 0; i < nums.length; ++i) { 
//				//nums[i] = start + i;
//			}
//
//			// Shuffle array 
//			final Random rnd = new Random(System.currentTimeMillis()); 
//			for (int i = nums.length - 1; i > 0; --i) { 
//				// Generate an index to swap with... 
//				final int swapIndex = rnd.nextInt(i + 1); 
//
//				// ...and swap 
//				final int temp = nums[i]; 
//				nums[i] = nums[swapIndex]; 
//				nums[swapIndex] = temp; 
//			} 
//			//return nums;
//			String strResult = String.format("%d%d%d%d%d%d", 
//					nums[0], nums[1], nums[2], nums[3], nums[4], nums[5]);
//
//			return strResult;
//		}
	}
	//
}
