package gr.novidea.weatherpay.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.LANGUAGE
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.LOCALIZATION
import gr.novidea.weatherpay.databinding.FragmentLanguageBinding

class LanguageFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(LOCALIZATION, Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString(LANGUAGE, "en")
        if (savedLanguage == "en") {
            binding.english.isChecked = true
            binding.greek.isChecked = false
        } else {
            binding.english.isChecked = false
            binding.greek.isChecked = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.greek.setOnClickListener {
            if (binding.greek.isChecked) {
                saveLanguage("el")
                val localeList = LocaleListCompat.forLanguageTags("el")
                AppCompatDelegate.setApplicationLocales(localeList)
                updateUI()
            }
        }

        binding.english.setOnClickListener {
            if (binding.english.isChecked) {
                saveLanguage("en")
                val localeList = LocaleListCompat.forLanguageTags("en")
                AppCompatDelegate.setApplicationLocales(localeList)
                updateUI()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val toolbar = requireActivity().findViewById<View>(R.id.toolbar)
        val topNav = requireActivity().findViewById<View>(R.id.topNav)
        toolbar.visibility = View.VISIBLE
        topNav.visibility = View.GONE
        requireActivity().title = requireContext().getString(R.string.language_selection)
    }

    private fun updateUI() {
        binding.english.text = requireContext().getString(R.string.english)
        binding.greek.text = requireContext().getString(R.string.greek)
    }

    private fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(LANGUAGE, language).apply()
    }

}