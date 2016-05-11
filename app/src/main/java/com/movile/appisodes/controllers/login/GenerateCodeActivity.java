package com.movile.appisodes.controllers.login;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.movile.appisodes.R;
import com.movile.appisodes.custom_views.progress_bars.ProgressWheel;
import com.movile.appisodes.utils.ViewUtils;
import com.movile.common.model.authentication.GeneratedCode;
import com.movile.common.services.ISessionService;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * This activity generates and shows to the user a generated code to sign-in using Trakt.tv
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 */
@ContentView(R.layout.activity_generate_code)
public class GenerateCodeActivity extends RoboActionBarActivity
        implements View.OnClickListener, ISessionService.IAuthenticationResultCallback {

    /** Main Content LinearLayout **/
    @InjectView(R.id.main_content_ll)
    private View mMainContentLl;

    /** Progress Wheel **/
    @InjectView(R.id.progress_wheel)
    private ProgressWheel mProgressWheel;

    /** Go to Trakt.tv TextView **/
    @InjectView(R.id.go_trakt_rtv)
    private TextView mGoTraktRtv;

    /** Generated code TextView **/
    @InjectView(R.id.code_rtv)
    private TextView mCodeRtv;

    /** Session service **/
    @Inject
    private ISessionService mSessionService;

    /** Authentication URL **/
    private String mAuthenticationUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GenerateCodeAsyncTask().execute();

        mGoTraktRtv.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v
     *         The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_trakt_rtv:
                Intent verifyCodeIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mAuthenticationUrl));
                startActivity(verifyCodeIntent);
                break;
        }
    }

    /**
     * This method will be called if the user has already accepted the app integration and has
     * granted permissions
     */
    @Override
    public void onSuccess() {
        Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show();
    }

    /**
     * This method will be called if expiration time has been reached and the user has not already
     * accepted the app integration
     */
    @Override
    public void onFailure() {
        Toast.makeText(this, "Faliure :(", Toast.LENGTH_LONG).show();
    }

    /**
     * This method will be called if the user denies access to the app integration
     */
    @Override
    public void onNotAuthorized() {
        Toast.makeText(this, "Not authorized :'(", Toast.LENGTH_LONG).show();
    }

    /**
     * This method enable/disable the Progress wheel and its related views
     *
     * @param enable
     *         Enable/Disable
     */
    private void enableProgressWheel(boolean enable) {
        ViewUtils.enableProgressWheel(mProgressWheel, enable, mMainContentLl);
    }

    /**
     * This AsyncTask generates the code by Trakt.tv
     */
    private class GenerateCodeAsyncTask extends AsyncTask<Void, Void, GeneratedCode> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            enableProgressWheel(true);
        }

        @Override
        protected GeneratedCode doInBackground(Void... params) {
            return mSessionService.doAuthentication(GenerateCodeActivity.this);
        }

        @Override
        protected void onPostExecute(GeneratedCode generatedCode) {
            super.onPostExecute(generatedCode);
            enableProgressWheel(false);

            if (generatedCode == null) {
                // TODO: Manage connection failure
                return;
            }

            mCodeRtv.setText(generatedCode.getUserCode());
            mAuthenticationUrl = generatedCode.getVerificationUrl();
        }
    }

}