/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.likeornot;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;

import tgio.parselivequery.LiveQueryClient;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("8YOjnV3Rlke2hrb1cJfBOgTmEd0GCOJzsxXu2414")
            .clientKey("9gV0TaUhW9W6QS1LF0qepS4SzqnFizTPjKquyrjO")
            .server("https://parseapi.back4app.com/")
            .build()
    );

/*
    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
    installation.put("GCMSenderId", "943965271670");
    installation.saveInBackground();
*/
    LiveQueryClient.init("wss://noname.back4app.io", "8YOjnV3Rlke2hrb1cJfBOgTmEd0GCOJzsxXu2414", true);
    LiveQueryClient.connect();
    Log.i("connect", "successful");

/*
    ParseObject object = new ParseObject("ExampleObject");
    object.put("myNumber", "123");
    object.put("myString", "rob");

    object.saveInBackground(new SaveCallback () {
      @Override
      public void done(ParseException ex) {
        if (ex == null) {
          Log.i("Parse Result", "Successful!");
        } else {
          Log.i("Parse Result", "Failed" + ex.toString());
        }
      }
    });

*/
    //ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

  }
}
