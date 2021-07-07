package com.frontend.billify.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.frontend.billify.HomepageActivity;
import com.frontend.billify.R;
import com.frontend.billify.models.User;
import com.frontend.billify.controllers.UserService;
import com.frontend.billify.services.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);
        MaterialButton nextButton = view.findViewById(R.id.next_button);

        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                if (!isPasswordValid(passwordEditText.getText())) {
                    passwordTextInput.setError(getString(R.string.error_password));
                    valid = false;
                }

                if (!isUsernameValid(usernameEditText.getText())) {
                    usernameTextInput.setError(getString(R.string.error_username));
                    valid = false;
                }

                if (valid) {
                    passwordTextInput.setError(null); // Clear the error
                    //((NavigationHost) getActivity()).navigateTo(new ProductGridFragment(), false); // Navigate to the next Fragment
                    String userName = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    user.setUser_name(userName);
                    user.setPassword(password);

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Logging in ...", Toast.LENGTH_SHORT).show();

                    userService.loginUser(user).enqueue(
                        new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                System.out.println("response reached");
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Error code " + response.code() + " " + response.errorBody().toString(),
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                User userResponse = response.body();
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Successfully logged in", Toast.LENGTH_SHORT).show();
                                openHomepage();
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                System.out.println("failure message: " + t.getMessage());
                                Toast.makeText(getActivity().getApplicationContext(),
                                        t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    });
                }
            }

            public void openHomepage(){
                Intent intent = new Intent(getActivity(), HomepageActivity.class);
                startActivity(intent);

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

        usernameEditText.setOnKeyListener(new OnKeyListener() {
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
}