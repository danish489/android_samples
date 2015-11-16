/*
* Copyright 2015 The Android Open Source Project
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

package com.example.android.system.runtimepermissions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RuntimePermissionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, null);


        if (!PermissionUtil.isMNC()) {
            /*
            The contacts permissions have been declared in the AndroidManifest for Android M only.
            They are not available on older platforms, so we are hiding the button to access the
            contacts database.
            This shows how new runtime-only permissions can be added, that do not apply to older
            platform versions. This can be useful for automated updates where additional
            permissions might prompt the user on upgrade.
             */
            root.findViewById(R.id.button_camera).setVisibility(View.GONE);
        }


        return root;
    }
}
