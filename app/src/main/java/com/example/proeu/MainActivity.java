package com.example.proeu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import static maes.tech.intentanim.CustomIntent.customType;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private LoginButton loginButton;
    private Button loginFacebook, btnGoogle, btnDone;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private EditText inputUsername, inputPassword, inputEmail, inputDate;
    DatePickerDialog.OnDateSetListener setListener;
    FirebaseFirestore firestore;
    String userID;
    private String TAG;
    private boolean myBoolean;
    public boolean isMyBoolean;


    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BACK

        // Initialise Registration
        inputUsername = findViewById(R.id.inputUser);
        inputPassword = findViewById(R.id.inputPassword);
        inputEmail = findViewById(R.id.inputEmail);



        inputDate = findViewById(R.id.inputDate);
        btnDone = findViewById(R.id.btn_done);

        firestore = FirebaseFirestore.getInstance();


        //Calendar
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        inputDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });






        inputUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, final boolean hasFocus) {


                inputUsername.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        final String username = inputUsername.getText().toString();
                        CollectionReference db = firestore.collection("Full_users");
                        final Query findQName = db.whereEqualTo("login_name",username);
                        searching(findQName, username);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                    if(!hasFocus){
                        inputUsername.post(new Runnable() {
                            @Override
                            public void run() {

                                final String username = inputUsername.getText().toString();
                                CollectionReference db = firestore.collection("Full_users");
                                final Query findQName = db.whereEqualTo("login",username);
                                searching(findQName, username);
                              //  Toast.makeText(MainActivity.this,"True",Toast.LENGTH_SHORT).show();
                                }

                        });


                        }
                    // Toast.makeText(MainActivity.this,"False",Toast.LENGTH_SHORT).show();

                btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkCrededentials();

                        }
                    });
                }
            /*    btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkCrededentials(hasFocus);

                    }
                });
              */

        });




       /* btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCrededentials();

            }
        });

        */


        //Facebook sdk
        FacebookSdk.sdkInitialize(MainActivity.this);

        //Initialise Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize Google login button
        btnGoogle = findViewById(R.id.btnGoogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        // Initialize Facebook Login button
        loginFacebook = findViewById(R.id.btnFacebook);
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }




   /* private void authenticateUser(Query findNameQ, String username) {
            firestore.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                authenticateUser(findNameQ, mUserName);
                            }
                        }
                    }
                }
            });
    }
*/
    private void checkCrededentials() {
        final String username = inputUsername.getText().toString();
        final String email = inputEmail.getText().toString();
        final String pass = inputPassword.getText().toString();
        final String password = sha256(pass);
        String date = inputDate.getText().toString();
        CollectionReference db = firestore.collection("Full_users");
        final Query findQName = db.whereEqualTo("login_name",username);
        if (username.isEmpty() || (username.length() > 15 ) ) {
            showError(inputUsername, "Имя пользователя введено не корректно");

        }
        else if(searching(findQName, username)){
            showError(inputUsername,"Пользователь уже существует");
        }
        else if ((email.isEmpty() || !email.contains("@")) || (!email.contains(".ru") & !email.contains(".com") & !email.contains(".net"))) {
            showError(inputEmail, "Адрес эл почты введен не корректно");
        } else if (pass.isEmpty() || pass.length() < 6) {
            showError(inputPassword, "Пароль должен быть больше 6 символов");

        } else if (date.isEmpty()) {
            showError(inputDate, "Введите дату рождения");
        } else
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("Full_users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("email_users",email);
                        user.put("login_name",username);
                        user.put("pass_users",password);
                        documentReference.set(user);
                            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                    } else {
                        showError(inputEmail,"Адрес электронной почты уже зарегистрирован");
                    }
                }
            });
        }



    private boolean searching(Query findQName, final String username) {
        findQName.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String user = document.getString("login_name");
                        if (user.equals(username)) {
                            isMyBoolean=true;
                            showError(inputUsername, "Пользователь уже существует");
                        }
                    }
                }


                          if (task.getResult().size() == 0) {
                              isMyBoolean=false;
                          }

            }


        });
    return isMyBoolean;}

    private void showError(EditText input, String s) {

            input.setError(s);
            input.requestFocus();

    }




    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signIn() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }, 2000);

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    public void onClick(View view) {
        if (view == loginFacebook) {
            loginButton.performClick();
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        if (user != null) {
            userID = mAuth.getCurrentUser().getUid();
            DocumentReference documentReference = firestore.collection("Full_users").document(userID);
            Map<String,Object> users = new HashMap<>();
            users.put("email_users",user.getEmail());
            users.put("login_name",user.getDisplayName());
            documentReference.set(users);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }, 2000);


        } else {
            Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show();
        }
    }
    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
/*
    @Override
    public void onBackPressed() {
        try {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            customType(MainActivity.this, "up-to-bottom");
            finish();

        } catch (Exception e) {
        }


    }
*/
}