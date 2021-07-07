package com.frontend.billify.login;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.billify.NavigationHost;
import com.frontend.billify.R;
import com.frontend.billify.controllers.UserService;
import com.frontend.billify.models.User;
import com.frontend.billify.services.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserService userService = new UserService(retrofitService);

    private final User user = new User("", "", "");

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.register, container, false);

        final TextInputLayout emailTextInput = view.findViewById(R.id.email_register_input);
        final TextInputEditText emailEditText = view.findViewById(R.id.email_register_edit_text);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_register_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_register_edit_text);
        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_register_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_register_edit_text);
        final ProgressBar registerProgress = view.findViewById(R.id.registerProgressBar);
        MaterialButton registerBtn = view.findViewById(R.id.register_button);

        // Set an error if the password is less than 8 characters.
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                if (!isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(getString(R.string.error_password));
                    valid = false;
                }

                if (!isEmailValid(emailEditText.getText())) {
                    emailTextInput.setError(getString(R.string.error_email));
                    valid = false;
                }

                if (!isUsernameValid(usernameEditText.getText())) {
                    usernameTextInput.setError(getString(R.string.error_username));
                    valid = false;
                }

                if (valid) {
                    registerProgress.setVisibility(View.VISIBLE);
                    passwordTextInput.setError(null); // Clear the error
                    ((NavigationHost) getActivity()).navigateTo(new LoginFragment(), false); // Navigate to the next Fragment
                    String userName = usernameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    user.setUser_name(userName);
                    user.setPassword(password);
                    user.setEmail(email);

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Signing up ...", Toast.LENGTH_SHORT).show();

                    userService.signupUser(user).enqueue(
                            new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    System.out.println("response reached");
                                    registerProgress.setVisibility(View.GONE);
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Error code " + response.code() + " " + response.errorBody().toString(),
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    User userResponse = response.body();
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Successfully registered", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    registerProgress.setVisibility(View.GONE);
                                    System.out.println("failure message: " + t.getMessage());
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        // Clear the error once more than 8 characters are typed.
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        emailEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isEmailValid(emailEditText.getText())) {
                    emailTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        usernameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isUsernameValid(usernameEditText.getText())) {
                    usernameTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });
        return view;
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

    private boolean isUsernameValid(@Nullable Editable text) {
        return text != null && text.length() > 0;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean isEmailValid(@Nullable Editable text) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text.toString());
        return matcher.find();
    }
}
