package com.frontend.billify.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.billify.HomepageActivity;
import com.frontend.billify.NavigationHost;
import com.frontend.billify.MainActivity;
import com.frontend.billify.R;
import com.frontend.billify.controllers.UserService;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserService userService = new UserService(retrofitService);

    private final User user = new User("test@gmail.com", "", "");

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);
        final TextInputLayout emailTextInput = view.findViewById(R.id.email_text_input);
        final TextInputEditText emailEditText = view.findViewById(R.id.email_edit_text);
        final ProgressBar loginProgress = view.findViewById(R.id.loginProgressBar);
        MaterialButton nextButton = view.findViewById(R.id.next_button);
        MaterialButton signupButton = view.findViewById(R.id.signup_button);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean valid = true;
                if (!isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(getString(R.string.error_password));
                    valid = false;
                }

                if (!isEmailValid(emailEditText.getText())) {
                    emailTextInput.setError(getString(R.string.error_username));
                    valid = false;
                }

                if (valid) {
                    loginProgress.setVisibility(View.VISIBLE);
                    passwordTextInput.setError(null); // Clear the error
                    //((NavigationHost) getActivity()).navigateTo(new ProductGridFragment(), false); // Navigate to the next Fragment
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    user.setEmail(email);
                    user.setPassword(password);

                    Toast loginToast = Toast.makeText(getActivity().getApplicationContext(),
                            "Logging in ...", Toast.LENGTH_SHORT);
                    loginToast.show();

                    userService.loginUser(user).enqueue(
                        new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                loginProgress.setVisibility(View.GONE);
                                loginToast.cancel();
                                if (!response.isSuccessful()) {
                                    try {
                                        JSONObject error = new JSONObject(response.errorBody().string());
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                error.getString("error"),
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Sorry :( Something went wrong.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }
                                User user = response.body(); // only userId is returned
                                Persistence.saveUserId(getActivity(), user.getId());
                                System.out.println("userId" + Persistence.getUserId(getActivity()));
                                openHomepage();
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                loginProgress.setVisibility(View.GONE);
                                loginToast.cancel();
                                System.out.println("Error: " + t.getMessage());
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Cannot connect to login server", Toast.LENGTH_LONG).show();
                            }
                    });
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new RegisterFragment(), true);
            }
        });

        // Clear the error once more than 8 characters are typed.
        passwordEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        emailEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isEmailValid(emailEditText.getText())) {
                    emailTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });
        return view;
    }

    public void openHomepage(){
        Intent intent = new Intent(getActivity(), HomepageActivity.class);
        startActivity(intent);
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

    private boolean isEmailValid(@Nullable Editable text) {
        return text != null && text.length() > 0;
    }

}