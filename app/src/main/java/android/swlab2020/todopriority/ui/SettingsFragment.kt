package android.swlab2020.todopriority.ui

import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.R
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.*
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
        findPreference<CheckBoxPreference>("nightModeManual")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                if (PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getBoolean("nightMode", false)
                ) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true
        }
        findPreference<SwitchPreference>("nightMode")?.apply {
            if (isChecked)
                setTitle(R.string.setting_night_mode_night)
            else
                setTitle(R.string.setting_night_mode_day)

            setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                true
            }
        }

        findPreference<EditTextPreference>("bugReport")
            ?.setOnPreferenceChangeListener { preference, message ->
                (preference as EditTextPreference).setOnBindEditTextListener {
                    it.text.clear()
                }
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("ileilliat@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "<Feedback> ToDoPriority Bug Report")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "AppCode: ToDoPriority (T:${System.currentTimeMillis()})\n" +
                                "Detail: $message"
                    )
                }
                try {
                    startActivity(emailIntent)
                } catch (exception: Exception) {
                    Snackbar.make(
                        this.requireView(),
                        R.string.setting_bug_report_fail,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                true
            }
    }

    override fun onResume() {
        super.onResume()
        setAutoSync()
    }

    private fun setAutoSync() {
        findPreference<SwitchPreference>("auto_sync")?.apply {
            val isLogin = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean("isLogin", false)
            if (!isLogin)
                sharedPreferences.edit {
                    putBoolean("auto_sync", false)
                    commit()
                }
            isSelectable = isLogin
            isEnabled = isLogin
        }
    }
}
