package zrsApps.safenights;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUp extends Fragment {

    //private OnFragmentInteractionListener mListener;
    public static final String PREFS_NAME = "CoreSkillsPrefsFile";
    private EditText mUsername;
    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private EditText mPassword;
    private TextView appname;
    Button mSignUp;
    private AVLoadingIndicatorView indicator;

    private boolean goodUsername = false;
    private boolean goodFname = false;
    private boolean goodLname = false;
    private boolean goodEmail = false;
    private boolean goodPassword = false;

    public static SignUp newInstance() {
        SignUp fragment = new SignUp();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(rootview instanceof EditText)) {
            rootview.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        // Indicator view
        indicator = (AVLoadingIndicatorView)rootview.findViewById(R.id.avi);

        //Add Proper Logic For Each Field To Validate for Submission
        mUsername   = (EditText)rootview.findViewById(R.id.username);
        mUsername.addTextChangedListener(new TextValidator(mUsername) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mUsername.getText().toString().length() == 0 ) {
                    mUsername.setError("Username is required!");
                } else if( mUsername.getText().toString().length() > 20 ) {
                    mUsername.setError("Username cannot be longer than 20 characters!");
                } else if( mUsername.getText().toString().matches("^[A-Za-z0-9]+$")) {
                    //Matches what we want correctly
                    goodUsername = true;
                } else {
                    //Has characters not allowed
                    mUsername.setError("Username can only contain uppercase/lowercase letters and numbers 0-9!");
                }
            }
        });
        mFname   = (EditText)rootview.findViewById(R.id.fname);
        mFname.addTextChangedListener(new TextValidator(mFname) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mFname.getText().toString().length() == 0 ) {
                    mFname.setError("First name is required!");
                } else if( mFname.getText().toString().length() > 20 ) {
                    mFname.setError("First name cannot be longer than 20 characters");
                } else {
                    //Matches what we want correctly
                    goodFname = true;
                }
            }
        });
        mLname   = (EditText)rootview.findViewById(R.id.lname);
        mLname.addTextChangedListener(new TextValidator(mLname) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mLname.getText().toString().length() == 0 ) {
                    mLname.setError("Last name is required!");
                } else if( mLname.getText().toString().length() > 20 ) {
                    mLname.setError("Last name cannot be longer than 20 characters!");
                } else {
                    //Matches what we want correctly
                    goodLname = true;
                }
            }
        });
        mEmail   = (EditText)rootview.findViewById(R.id.email);
        mEmail.addTextChangedListener(new TextValidator(mEmail) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if(isValidEmail(text)) {
                    goodEmail = true;
                } else {
                    mEmail.setError("Invalid Email!");
                }
            }
        });
        mPassword   = (EditText)rootview.findViewById(R.id.password);
        mPassword.addTextChangedListener(new TextValidator(mPassword) {
            @Override public void validate(TextView textView, String text) {
                /* Validation code here */
                if( mPassword.getText().toString().length() == 0 ) {
                    mPassword.setError("Password is required!");
                } else if( mPassword.getText().toString().length() < 6 ) {
                    mPassword.setError("Password cannot be shorter than 6 characters!");
                } else if( mPassword.getText().toString().length() > 20 ) {
                    mPassword.setError("Password cannot be longer than 20 characters!");
                } else {
                    //Matches what we want correctly
                    goodPassword = true;
                }
            }
        });
        mSignUp = (Button)rootview.findViewById(R.id.signup_button);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodUsername && goodFname && goodLname & goodEmail && goodPassword) {
                    indicator.show();
                    indicator.setVisibility(View.VISIBLE);
                    mSignUp.setEnabled(false);
                    callSignUpAPI(v);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You must fulfill all requirements above!\nPlease complete and try again :)", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootview;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void callSignUpAPI(View view) {
        SafeNightsAPIInterface apiService =
                SafeNightsAPIClient.getClient().create(SafeNightsAPIInterface.class);

        //Get the strings you need for the api
        String username = mUsername.getText().toString().trim();
        final String fname = mFname.getText().toString().trim();
        final String lname = mLname.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        Call<User> call = apiService.signup(username, fname, lname, email, password);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u  = response.body();
                if(u.getPassed().equals("y")){
                    //bring them to login page
                    Fragment fragment = new SignIn();
                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment, fragment).addToBackStack("")
                            .commit();
                    Toast.makeText(getActivity().getApplicationContext(), "You have been registered!\nPlease login :)", Toast.LENGTH_LONG).show();
                }
                else {
                    indicator.hide();
                    indicator.setVisibility(View.GONE);
                    mSignUp.setEnabled(true);
                    Toast.makeText(getActivity().getApplicationContext(), "There already exists a user with that username.\nPlease choose a different one :)", Toast.LENGTH_LONG).show();
                }
                mSignUp.setEnabled(true);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Log error here since request failed
                Log.e("API Call:", t.toString());
                indicator.hide();
                indicator.setVisibility(View.GONE);
                mSignUp.setEnabled(true);
            }
        });
    }

}
