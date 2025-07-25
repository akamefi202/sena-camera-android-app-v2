package com.sena.senacamera.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.ads.mobile.sdk.internal.signals.ClientRequestBuildingData;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.BuildConfig;
import com.sena.senacamera.ui.component.MenuInfo;
import com.sena.senacamera.ui.component.MenuLink;
import com.sena.senacamera.utils.ClickUtils;
import com.sena.senacamera.utils.SenaXmlParser;

public class FragmentHelpGuide extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentHelpGuide.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    MenuLink userGuideLayout, quickGuideLayout, videoGuideLayout, supportLayout, forumLayout, mailingListLayout, termsLayout, privacyPolicyLayout;
    MenuInfo appVersionMenu;

    SenaXmlParser senaXmlParser = SenaXmlParser.getInstance();

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_help_guide, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.userGuideLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.user_guide_layout);
        this.quickGuideLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.quick_guide_layout);
        this.videoGuideLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.video_guide_layout);
        this.supportLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.support_layout);
        this.forumLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.forum_layout);
        this.mailingListLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.mailing_list_layout);
        this.termsLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.terms_layout);
        this.privacyPolicyLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.privacy_policy_layout);
        this.appVersionMenu = (MenuInfo) this.fragmentLayout.findViewById(R.id.app_version_layout);

        this.userGuideLayout.setOnClickListener(this);
        this.quickGuideLayout.setOnClickListener(this);
        this.videoGuideLayout.setOnClickListener(this);
        this.supportLayout.setOnClickListener(this);
        this.forumLayout.setOnClickListener(this);
        this.mailingListLayout.setOnClickListener(this);
        this.termsLayout.setOnClickListener(this);
        this.privacyPolicyLayout.setOnClickListener(this);
        this.backButton.setOnClickListener(v -> onBack());

        this.appVersionMenu.setValue(BuildConfig.VERSION_NAME);

        return this.fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.backButton = null;
        this.userGuideLayout = null;
        this.quickGuideLayout = null;
        this.videoGuideLayout = null;
        this.supportLayout = null;
        this.forumLayout = null;
        this.mailingListLayout = null;
        this.termsLayout = null;
        this.privacyPolicyLayout = null;
        this.appVersionMenu = null;
    }

    public void updateFragment() {

    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (ClickUtils.isFastClick()) {
            return;
        }

        if (id == R.id.user_guide_layout) {
            if (senaXmlParser.userGuideUrl.isEmpty()) {
                return;
            }

            // open user guide
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.userGuideUrl));
            startActivity(browserIntent);
        } else if (id == R.id.quick_guide_layout) {
            if (senaXmlParser.quickGuideUrl.isEmpty()) {
                return;
            }

            // open quick guide
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.quickGuideUrl));
            startActivity(browserIntent);
        } else if (id == R.id.video_guide_layout) {
            if (senaXmlParser.videoGuideUrl.isEmpty()) {
                return;
            }

            // open video guide
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.videoGuideUrl));
            startActivity(browserIntent);
        } else if (id == R.id.support_layout) {
            if (senaXmlParser.supportUrl.isEmpty()) {
                return;
            }

            // open support
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.supportUrl));
            startActivity(browserIntent);
        } else if (id == R.id.forum_layout) {
            if (senaXmlParser.forumUrl.isEmpty()) {
                return;
            }

            // open forum
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.forumUrl));
            startActivity(browserIntent);
        } else if (id == R.id.mailing_list_layout) {
            if (senaXmlParser.mailingListUrl.isEmpty()) {
                return;
            }

            // open mailing list
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.mailingListUrl));
            startActivity(browserIntent);
        } else if (id == R.id.terms_layout) {
            if (senaXmlParser.termsUrl.isEmpty()) {
                return;
            }

            // open terms of use
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.termsUrl));
            startActivity(browserIntent);
        } else if (id == R.id.privacy_policy_layout) {
            if (senaXmlParser.privacyPolicyUrl.isEmpty()) {
                return;
            }

            // open privacy policy
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(senaXmlParser.privacyPolicyUrl));
            startActivity(browserIntent);
        }
    }
}
