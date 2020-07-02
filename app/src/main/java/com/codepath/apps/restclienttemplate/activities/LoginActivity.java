package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.app.TwitterApp;
import com.codepath.apps.restclienttemplate.app.TwitterClient;
import com.codepath.apps.restclienttemplate.models.sample.SampleModel;
import com.codepath.apps.restclienttemplate.models.sample.SampleModelDao;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
	/* Sample Room Application */
	SampleModelDao sampleModelDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final SampleModel sampleModel = new SampleModel();
		sampleModel.setName("CodePath");
		sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				sampleModelDao.insertModel(sampleModel);
			}
		});
	}

	/* OAuth authenticated successfully, launch primary authenticated activity */
	@Override
	public void onLoginSuccess() {
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
	}

	/* OAuth authentication flow failed, handle the error */
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	public void loginToRest(View view) {
		getClient().connect();
	}

}
