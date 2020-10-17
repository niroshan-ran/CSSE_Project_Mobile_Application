package com.csse.mobileapp.ui.home.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.csse.mobileapp.R;
import com.csse.mobileapp.ui.home.HomeActivity;
import com.csse.mobileapp.ui.home.ui.gallery.GalleryFragment;
import com.csse.mobileapp.ui.home.ui.home.HomeFragment;
import com.csse.mobileapp.ui.login.LoginActivity;

import java.util.Objects;


public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);


        builder = new AlertDialog.Builder(root.getContext());

        builder.setMessage("Are you sure you want to Logout ? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_nav_slideshow_to_nav_home);
            }
        });


        AlertDialog alert = builder.create();

        alert.setTitle("Confirm Logout");
        alert.show();


        return root;
    }
}