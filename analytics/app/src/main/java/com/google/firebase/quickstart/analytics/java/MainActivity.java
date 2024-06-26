/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * For more information on setting up and running this sample code, see
 * https://firebase.google.com/docs/analytics/android
 */

package com.google.firebase.quickstart.analytics.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.quickstart.analytics.R;
import com.google.firebase.quickstart.analytics.databinding.ActivityMainBinding;
import java.util.Locale;
import java.util.Random;

/**
 * Activity which displays numerous background images that may be viewed. These background images
 * are shown via {@link ImageFragment}.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_FAVORITE_FOOD = "favorite_food";

    private static final ImageInfo[] IMAGE_INFOS = {
            new ImageInfo(R.drawable.favorite, R.string.pattern1_title, R.string.pattern1_id),
            new ImageInfo(R.drawable.flash, R.string.pattern2_title, R.string.pattern2_id),
            new ImageInfo(R.drawable.face, R.string.pattern3_title, R.string.pattern3_id),
            new ImageInfo(R.drawable.whitebalance, R.string.pattern4_title, R.string.pattern4_id),
    };

    private ActivityMainBinding binding;

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide fragments for each image.
     * This uses a {@link FragmentStateAdapter}, which keeps every loaded fragment in memory.
     */
    private ImagePagerAdapter mImagePagerAdapter;

    /**
     * The {@link ViewPager} that will host the patterns.
     */
    private ViewPager2 mViewPager;

    /**
     * The {@code FirebaseAnalytics} used to record screen views.
     */
    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    /**
     * The user's favorite food, chosen from a dialog.
     */
    private String mFavoriteFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // [START shared_app_measurement]
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // [END shared_app_measurement]

        for (int i = 0; i < 1000; i++) {
            juego();
        }

        // On first app open, ask the user his/her favorite food. Then set this as a user property
        // on all subsequent opens.
        String userFavoriteFood = getUserFavoriteFood();
        if (userFavoriteFood == null) {
            askFavoriteFood();
        } else {
            setUserFavoriteFood(userFavoriteFood);
        }

        // Create the adapter that will return a fragment for each image.
        mImagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), IMAGE_INFOS, getLifecycle());

        // Set up the ViewPager with the pattern adapter.
        mViewPager = binding.viewPager;
        mViewPager.setAdapter(mImagePagerAdapter);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                recordImageView();
                recordScreenView();
            }
        });

        TabLayout tabLayout = binding.tabLayout;

        // When the visible image changes, send a screen view hit.
        new TabLayoutMediator(tabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               tab.setText(IMAGE_INFOS[position].title);
            }
        }).attach();

        // Send initial screen screen view hit.
        recordImageView();
    }

    private boolean porcentaje(Random rand, float p) {
        return rand.nextFloat() < p;
    }

    private void endLevel(String name) {
        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.LEVEL_NAME, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_END, b);
    }

    private void juego() {
        Random rand = new Random();
        if (!porcentaje(rand, 0.99f)) return;
        endLevel("TUTORIAL");
        if (!porcentaje(rand, 0.9f)) return;
        endLevel("LEVEL 1");
        if (!porcentaje(rand, 0.8f)) return;
        endLevel("LEVEL 2");
        if (!porcentaje(rand, 0.7f)) return;
        endLevel("LEVEL 3");
        //PURCHASE
        if (porcentaje(rand, 0.5f)) return;
        if (!porcentaje(rand, 0.6f)) return;
        endLevel("LEVEL 4");
        if (!porcentaje(rand, 0.5f)) return;
        endLevel("LEVEL 5");
        if (!porcentaje(rand, 0.4f)) return;
        endLevel("LEVEL 6");
        if (!porcentaje(rand, 0.3f)) return;
        endLevel("LEVEL 7");
        if (!porcentaje(rand, 0.2f)) return;
        endLevel("LEVEL 8");
        if (!porcentaje(rand, 0.1f)) return;
        endLevel("LEVEL 9");
        if (!porcentaje(rand, 0.05f)) return;
        endLevel("LEVEL 10");
    }

    @Override
    public void onResume() {
        super.onResume();
        recordScreenView();
    }

    /**
     * Display a dialog prompting the user to pick a favorite food from a list, then record
     * the answer.
     */
    private void askFavoriteFood() {
        final String[] choices = getResources().getStringArray(R.array.food_items);
        AlertDialog ad = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.food_dialog_title)
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String food = choices[which];
                        setUserFavoriteFood(food);
                    }
                }).create();

        ad.show();
    }

    /**
     * Get the user's favorite food from shared preferences.
     * @return favorite food, as a string.
     */
    private String getUserFavoriteFood() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(KEY_FAVORITE_FOOD, null);
    }

    /**
     * Set the user's favorite food as an app measurement user property and in shared preferences.
     * @param food the user's favorite food.
     */
    private void setUserFavoriteFood(String food) {
        Log.d(TAG, "setFavoriteFood: " + food);
        mFavoriteFood = food;

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(KEY_FAVORITE_FOOD, food)
                .apply();

        // [START user_property]
        mFirebaseAnalytics.setUserProperty("favorite_food", mFavoriteFood);
        // [END user_property]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_share) {
            String name = getCurrentImageTitle();
            String text = "I'd love you to hear about " + name;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            // [START custom_event]
            Bundle params = new Bundle();
            params.putString("image_name", name);
            params.putString("full_text", text);
            mFirebaseAnalytics.logEvent("share_image", params);
            // [END custom_event]
        }
        return false;
    }

    /**
     * Return the title of the currently displayed image.
     *
     * @return title of image
     */
    private String getCurrentImageTitle() {
        int position = mViewPager.getCurrentItem();
        ImageInfo info = IMAGE_INFOS[position];
        return getString(info.title);
    }

    /**
     * Return the id of the currently displayed image.
     *
     * @return id of image
     */
    private String getCurrentImageId() {
        int position = mViewPager.getCurrentItem();
        ImageInfo info = IMAGE_INFOS[position];
        return getString(info.id);
    }

    /**
     * Record a screen view for the visible {@link ImageFragment} displayed
     * inside {@link FragmentStateAdapter}.
     */
    private void recordImageView() {
        String id =  getCurrentImageId();
        String name = getCurrentImageTitle();

        // [START image_view_event]
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        // [END image_view_event]
    }

    /**
     * This sample has a single Activity, so we need to manually record "screen views" as
     * we change fragments.
     */
    private void recordScreenView() {
        // This string must be <= 36 characters long.
        String screenName = getCurrentImageId() + "-" + getCurrentImageTitle();

        // [START set_current_screen]
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        // [END set_current_screen]
    }

    /**
     * A {@link FragmentStateAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ImagePagerAdapter extends FragmentStateAdapter {

        private final ImageInfo[] infos;

        public ImagePagerAdapter(FragmentManager fm, ImageInfo[] infos, Lifecycle lifecyle) {
            super(fm, lifecyle);
            this.infos = infos;
        }

        public CharSequence getPageTitle(int position) {
            if (position < 0 || position >= infos.length) {
                return null;
            }
            Locale l = Locale.getDefault();
            ImageInfo info = infos[position];
            return getString(info.title).toUpperCase(l);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            ImageInfo info = infos[position];
            return ImageFragment.newInstance(info.image);
        }

        @Override
        public int getItemCount() {
            return infos.length;
        }
    }
}
