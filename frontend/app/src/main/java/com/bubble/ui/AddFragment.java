package com.bubble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bubble.R;

public class AddFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add, container, false);

        // 设置 ic_add_card 点击监听
        int[] addCardIds = {
            R.id.iv_add_card_1,
            R.id.iv_add_card_2,
            R.id.iv_add_card_3,
            R.id.iv_add_card_4,
            R.id.iv_add_card_5
        };

        for (int id : addCardIds) {
            ImageView ivAddCard = view.findViewById(id);
            if (ivAddCard != null) {
                ivAddCard.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), CreateActivity.class);
                    startActivity(intent);
                });
            }
        }

        return view;
    }
}
