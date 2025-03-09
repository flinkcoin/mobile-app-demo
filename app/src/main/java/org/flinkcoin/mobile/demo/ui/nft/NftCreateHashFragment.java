package org.flinkcoin.mobile.demo.ui.nft;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.flinkcoin.mobile.demo.databinding.FragmentNftCreateHashBinding;

import java.io.FileNotFoundException;

import pdqhashing.hasher.PDQHasher;
import pdqhashing.types.HashAndQuality;
import pdqhashing.utils.MatrixUtil;

public class NftCreateHashFragment extends Fragment {

    private NftViewModel nftViewModel;
    private FragmentNftCreateHashBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftCreateHashBinding.inflate(inflater, container, false);
        nftViewModel = new ViewModelProvider(requireActivity()).get(NftViewModel.class);

        binding.selectedImage.setImageURI(nftViewModel.getSelectedImage());

        binding.buttonConfirm.setOnClickListener(v -> {

            PDQHasher hasher = new PDQHasher();
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(nftViewModel.getSelectedImage()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            int numRows = bitmap.getHeight();
            int numCols = bitmap.getWidth();
            float[] buffer1 = MatrixUtil.allocateMatrixAsRowMajorArray(numRows, numCols);
            float[] buffer2 = MatrixUtil.allocateMatrixAsRowMajorArray(numRows, numCols);
            float[][] buffer64x64 = MatrixUtil.allocateMatrix(64, 64);
            float[][] buffer16x64 = MatrixUtil.allocateMatrix(16, 64);
            float[][] buffer16x16 = MatrixUtil.allocateMatrix(16, 16);

            HashAndQuality hashAndQuality = hasher.fromBitmap(bitmap, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16);

            //TODO: save, publish new block

        });

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}