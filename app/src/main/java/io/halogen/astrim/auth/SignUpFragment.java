package io.halogen.astrim.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import org.spongycastle.pqc.math.ntru.util.Util;

import io.halogen.astrim.R;
import io.halogen.astrim.chat.ChatActivity;
import io.halogen.astrim.util.Utilities;

public class SignUpFragment extends Fragment {
    Utilities utils = new Utilities();
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Button usubmit = view.findViewById(R.id.usubmit);
        usubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(utils.isOnline(getContext())){
                    //connect to server
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    startActivity(intent);
                }
                else{
                    utils.showSnackbar(getContext(), (RelativeLayout) getActivity().findViewById(R.id.authView), "NO CONNECTION!");
                }
            }
        });
        return view;
    }
}
