/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.goldze.mvvmhabit.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.goldze.mvvmhabit.R;


public final class DefaultErrorActivity extends AppCompatActivity {

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //异步可能会导致值没有储存到文件中，所以使用同步存储值
//        SPUtils.getInstance().put("CRASH_BUG", true);
        SharedPreferences sharedPreferences = getSharedPreferences("zdzf_sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("CRASH_BUG", true);
        editor.commit();
        TypedArray a = obtainStyledAttributes(R.styleable.AppCompatTheme);
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
        }
        a.recycle();

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        CustomActivityOnCrash.restartApplication(DefaultErrorActivity.this, config);
    }
}
