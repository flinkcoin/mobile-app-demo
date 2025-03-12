package org.flinkcoin.mobile.demo.util;

import android.graphics.Bitmap;

import pdqhashing.hasher.PDQHasher;
import pdqhashing.types.HashAndQuality;
import pdqhashing.utils.MatrixUtil;

public class PDQHasherUtil {

    private static final PDQHasher PDQ_HASHER = new PDQHasher();

    private PDQHasherUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String getHash(Bitmap bitmap) {
        int numRows = bitmap.getHeight();
        int numCols = bitmap.getWidth();

        float[] buffer1 = MatrixUtil.allocateMatrixAsRowMajorArray(numRows, numCols);
        float[] buffer2 = MatrixUtil.allocateMatrixAsRowMajorArray(numRows, numCols);
        float[][] buffer64x64 = MatrixUtil.allocateMatrix(64, 64);
        float[][] buffer16x64 = MatrixUtil.allocateMatrix(16, 64);
        float[][] buffer16x16 = MatrixUtil.allocateMatrix(16, 16);

        HashAndQuality hashAndQuality = PDQ_HASHER.fromBitmap(bitmap, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16);

        return hashAndQuality.getHash().toString();
    }

}
