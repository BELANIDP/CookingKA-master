package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractHomeTileHelper;
import com.whirlpool.cooking.databinding.FragmentHomeTileBinding;
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView;

public class HomeTileHelper extends AbstractHomeTileHelper {

    private View parentView;

    /** ================================================================================================================
     -----------------------------------------------  General Methods  ----------------------------------------------
     ================================================================================================================ **/

    @Override
    public void setParentView( View parentview) {
        parentView = parentview;
    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public FragmentHomeTileBinding getFragmentHomeTileBinding() {
        return null;
    }

    @Override
    public View provideTileTextLineIcon() {
        return parentView.findViewById(R.id.line);
    }

    @Override
    public ResourceTextView provideTileTextView() {
        return parentView.findViewById(R.id.textView_tileText);
    }
}
