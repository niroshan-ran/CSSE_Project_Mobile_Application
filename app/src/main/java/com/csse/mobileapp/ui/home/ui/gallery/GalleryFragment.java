package com.csse.mobileapp.ui.home.ui.gallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.csse.mobileapp.R;
import com.csse.mobileapp.models.Payment;
import com.csse.mobileapp.ui.home.HomeActivity;
import com.csse.mobileapp.ui.login.LoginActivity;
import com.csse.mobileapp.utilities.DBConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private EditText amount, holder, card_number, exp_date, cvc;
    String cardHolderName, userEmail = "";
    int securityCode;
    long cardNumber = 0;
    Date goodThru = null;
    double depositAmount = 0;
    Button submitButton, resetButton;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        amount = root.findViewById(R.id.editTextDepositAmount);
        holder = root.findViewById(R.id.editTextCardHolderName);
        card_number = root.findViewById(R.id.editTextCardNumber);
        exp_date = root.findViewById(R.id.editTextExpDate);
        cvc = root.findViewById(R.id.editTextCVC);
        submitButton = root.findViewById(R.id.buttonAddCash);
        resetButton = root.findViewById(R.id.buttonReset);

        Intent intent = requireActivity().getIntent();

        userEmail = intent.getStringExtra("User Email");


        DatePickerDialog monthYearPickerDialog = new DatePickerDialog(root.getContext(),
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String setDate = year + "-" + (month + 1);

                exp_date.setText(setDate);
            }
        }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue() - 1, LocalDateTime.now().getDayOfMonth());

        monthYearPickerDialog.getDatePicker().setCalendarViewShown(false);
        ((ViewGroup) monthYearPickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

        monthYearPickerDialog.setTitle("Select Month and Year");

        exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthYearPickerDialog.show();
            }
        });

        exp_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    monthYearPickerDialog.show();
                }
            }
        });

        submitButton.setOnClickListener(this::submitButtonClick);
        resetButton.setOnClickListener(this::resetButtonClick);


        return root;
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void submitButtonClick(View view) {

        if (!card_number.getText().toString().trim().isEmpty() && !holder.getText().toString().isEmpty() &&
                !amount.getText().toString().trim().isEmpty() && !cvc.getText().toString().trim().isEmpty() && !exp_date.getText().toString().isEmpty()) {


            if (card_number.getText().toString().trim().length() < 16) {
                card_number.setError("Card Number has to be 16 digits");
            } else if (cvc.getText().toString().trim().length() < 3) {
                cvc.setError("Please Enter Valid Security Code");
            } else if (Double.parseDouble(amount.getText().toString()) < 50) {
                amount.setError("Please Enter amount greater than Rs.50/=");
            } else {

                try {

                    cardNumber = Long.parseLong(card_number.getText().toString());
                    cardHolderName = holder.getText().toString();
                    depositAmount = Double.parseDouble(amount.getText().toString());
                    securityCode = Integer.parseInt(cvc.getText().toString());

                    String[] expiryDate = exp_date.getText().toString().trim().split("-");

                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Integer.parseInt(expiryDate[0]), Integer.parseInt(expiryDate[1]) - 1, 1);

                    goodThru = calendar.getTime();


                    Payment payment = new Payment();

                    payment.setCardHolderName(cardHolderName);
                    payment.setCardNumber(cardNumber);
                    payment.setDepositAmount(depositAmount);
                    payment.setSecurityCode(securityCode);
                    payment.setExpiryDate(goodThru);
                    payment.setUserEmail(userEmail);

                    new AddCashTask(requireActivity()).execute(payment);


                } catch (Exception ex) {
                    Snackbar.make(view, "Exception Occurred", Snackbar.LENGTH_LONG)
                            .setAction("Warning", null).show();
                }

            }
        } else {


            Snackbar.make(view, "Please Enter all Required Field", Snackbar.LENGTH_LONG)
                    .setAction("Warning", null).show();
        }

    }


    public void resetButtonClick(View view) {
        card_number.setText("");
        holder.setText("");
        amount.setText("");
        cvc.setText("");
        exp_date.setText("");
    }

    private void showSuccessMessage() {

        card_number.setText("");
        holder.setText("");
        amount.setText("");
        cvc.setText("");
        exp_date.setText("");

        Toast.makeText(requireActivity().getApplicationContext(), "Payment Added Successfully", Toast.LENGTH_LONG).show();
    }

    private void showErrorMessage() {
        Toast.makeText(requireActivity().getApplicationContext(), "Failed to Add the Payment", Toast.LENGTH_LONG).show();
    }

    private class AddCashTask extends AsyncTask<Payment, Void, Integer> {

        private ProgressDialog dialog;

        public AddCashTask(FragmentActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Please Wait");
            dialog.setTitle("Adding Payment");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Payment... payments) {

            return new DBConnection().SavePayment(payments[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (dialog.isShowing())
                dialog.dismiss();

            if (integer == 0) {
                showSuccessMessage();
            } else if (integer == -1) {
                showErrorMessage();
            }

        }
    }


}