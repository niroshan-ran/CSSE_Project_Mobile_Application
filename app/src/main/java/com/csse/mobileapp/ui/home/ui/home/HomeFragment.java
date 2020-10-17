package com.csse.mobileapp.ui.home.ui.home;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.csse.mobileapp.R;
import com.csse.mobileapp.ui.home.ui.gallery.GalleryFragment;
import com.csse.mobileapp.utilities.DBConnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutionException;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_nav_home_to_nav_gallery);

        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadDataTask(view).execute(requireActivity().getIntent().getStringExtra("User Email"));

    }

    private class LoadDataTask extends AsyncTask<String, Void, Double> {

        private final ProgressBar progressBar;
        private final TextView textView;
        View currentActivity;

        public LoadDataTask(View activity) {
            currentActivity = activity;
            progressBar = currentActivity.findViewById(R.id.progressBar);
            textView = currentActivity.findViewById(R.id.balanceText);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Double doInBackground(String... params) {

            return new DBConnection().GetBalanceAmount(params[0]);

        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);

            progressBar.setVisibility(View.GONE);

            String text = aDouble + "";

            textView.append(text);


        }
    }
}