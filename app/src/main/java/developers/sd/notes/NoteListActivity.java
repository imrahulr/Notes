package developers.sd.notes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends SingleFragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private NavigationView navigationView;
    private View navHeader;
    private DrawerLayout drawer;
    private Toolbar tToolbar;
    private ImageView imgProfile;
    private TextView txtName, txtEmailId;
    private Button button_signin;
    private GoogleApiClient mGoogleApiClient;
    private ImageButton mAccountChange;

    public static int navItemIndex = 0;
    private String[] activityTitles;
    private static final String TAG = "NoteListActivity";
    private static final int RC_SIGN_IN = 007;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.fragment_container);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        tToolbar = (Toolbar) findViewById(R.id.tToolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navHeader = navigationView.getHeaderView(0);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtEmailId = (TextView) navHeader.findViewById(R.id.email_id);
        button_signin = (Button) navHeader.findViewById(R.id.sign_in);
        mAccountChange = (ImageButton) navHeader.findViewById(R.id.acc_chg);

        loadHomeFragment();
        setUpNavigationView();
        navigationView.getMenu().getItem(0).setChecked(true);
        new BackgroundFunctions().execute();

        button_signin.setOnClickListener(this);
        mAccountChange.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void loadHomeFragment() {
        drawer.closeDrawers();
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_all_notes:
                        if (navItemIndex != 0) {
                            NoteListFragment noteListFragment = new NoteListFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteListFragment).commit();
                        }
                        navigationView.getMenu().getItem(0).setChecked(true);
                        navItemIndex = 0;
                        break;
                    case R.id.nav_recent:
                        if (navItemIndex != 1) {
                            NoteListFragmentRecent noteListFragmentRecent = new NoteListFragmentRecent();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteListFragmentRecent).commit();
                        }
                        navigationView.getMenu().getItem(1).setChecked(true);
                        navItemIndex = 1;
                        break;
                    case R.id.nav_fav:
                        if (navItemIndex != 2) {
                            NoteListFragmentFav noteListFragmentFav = new NoteListFragmentFav();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteListFragmentFav).commit();
                        }
                        navigationView.getMenu().getItem(2).setChecked(true);
                        navItemIndex = 2;
                        break;
                    case R.id.nav_bin:
                        if (navItemIndex != 3) {
                            NoteListFragmentBin noteListFragmentBin = new NoteListFragmentBin();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteListFragmentBin).commit();
                        }
                        navigationView.getMenu().getItem(3).setChecked(true);
                        navItemIndex = 3;
                        break;
                    case R.id.nav_upload_drive:
                        Intent intent1 = new Intent(NoteListActivity.this, UploadToDriveActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_restore_drive:
                        Intent intent2 = new Intent(NoteListActivity.this, RestoreDriveActivity.class);
                        startActivity(intent2);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        Intent intent3 = new Intent(NoteListActivity.this, AboutUs.class );
                        startActivity(intent3);
                        break;
                    default:
                        navItemIndex = 0;

                }
                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, tToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        mAccountChange.setVisibility(View.GONE);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            button_signin.setVisibility(View.GONE);
            mAccountChange.setVisibility(View.VISIBLE);
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : null;
            String email = acct.getEmail();

            txtName.setVisibility(View.VISIBLE);
            txtEmailId.setVisibility(View.VISIBLE);
            txtName.setText(personName);
            txtEmailId.setText(email);
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
        } else {
            button_signin.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sign_in) {
            loadHomeFragment();
            signIn();
        }
        if(id==R.id.acc_chg)
        {
            button_signin.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.GONE);
            txtEmailId.setVisibility(View.GONE);
            imgProfile.setVisibility(View.GONE);
            loadHomeFragment();
            signOut();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG);
                    }
                });
            }

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                navigationView.getMenu().getItem(0).setChecked(true);
                NoteListFragment noteListFragment = new NoteListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, noteListFragment).commit();
            } else {
                super.onBackPressed();
            }
        }
    }

    private  boolean checkAndRequestPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (audio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private class BackgroundFunctions extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground (Void...params) {
            checkAndRequestPermissions();
            return null;
        }
    }

}
