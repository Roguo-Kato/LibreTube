package com.github.libretube.ui.preferences

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.github.libretube.R
import com.github.libretube.constants.PreferenceKeys
import com.github.libretube.ui.base.BasePreferenceFragment
import com.github.libretube.util.LocaleHelper
import com.github.libretube.util.PreferenceHelper

class PlayerSettings : BasePreferenceFragment() {
    override val titleResourceId: Int = R.string.player

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.player_settings, rootKey)

        val playerOrientation =
            findPreference<ListPreference>(PreferenceKeys.FULLSCREEN_ORIENTATION)
        val autoRotateToFullscreen =
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.AUTO_FULLSCREEN)

        // only show the player orientation option if auto fullscreen is disabled
        playerOrientation?.isEnabled = !PreferenceHelper.getBoolean(
            PreferenceKeys.AUTO_FULLSCREEN,
            false
        )

        autoRotateToFullscreen?.setOnPreferenceChangeListener { _, newValue ->
            playerOrientation?.isEnabled = newValue != true
            true
        }

        val defaultSubtitle = findPreference<ListPreference>(PreferenceKeys.DEFAULT_SUBTITLE)
        defaultSubtitle?.let { setupSubtitlePref(it) }

        val systemCaptionStyle =
            findPreference<SwitchPreferenceCompat>(PreferenceKeys.SYSTEM_CAPTION_STYLE)
        val captionSettings = findPreference<Preference>(PreferenceKeys.CAPTION_SETTINGS)

        captionSettings?.isVisible =
            PreferenceHelper.getBoolean(PreferenceKeys.SYSTEM_CAPTION_STYLE, true)
        systemCaptionStyle?.setOnPreferenceChangeListener { _, newValue ->
            captionSettings?.isVisible = newValue as Boolean
            true
        }

        captionSettings?.setOnPreferenceClickListener {
            try {
                val captionSettingsIntent = Intent(Settings.ACTION_CAPTIONING_SETTINGS)
                startActivity(captionSettingsIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
            }
            true
        }

        val pictureInPicture = findPreference<SwitchPreferenceCompat>(
            PreferenceKeys.PICTURE_IN_PICTURE
        )!!
        val pauseOnQuit = findPreference<SwitchPreferenceCompat>(PreferenceKeys.PAUSE_ON_QUIT)
        pictureInPicture.isVisible = SDK_INT >= Build.VERSION_CODES.O
        pauseOnQuit?.isVisible = !pictureInPicture.isVisible || !pictureInPicture.isChecked

        pictureInPicture.setOnPreferenceChangeListener { _, newValue ->
            pauseOnQuit?.isVisible = !(newValue as Boolean)
            true
        }
    }

    private fun setupSubtitlePref(preference: ListPreference) {
        val locales = LocaleHelper.getAvailableLocales().sortedBy { it.name }
        val localeNames = locales.map { it.name }
            .toMutableList()
        localeNames.add(0, requireContext().getString(R.string.none))

        val localeCodes = locales.map { it.code }
            .toMutableList()
        localeCodes.add(0, "")

        preference.entries = localeNames.toTypedArray()
        preference.entryValues = localeCodes.toTypedArray()
        preference.summaryProvider =
            Preference.SummaryProvider<ListPreference> {
                it.entry
            }
    }
}
