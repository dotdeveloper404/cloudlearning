package com.sparkmembership.sparkowner.presentation.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var appConfig: AppConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initializeNavigation()

    }

    fun showToolbar() {
        binding.appBarlayout.customToolbar.toVisible()
    }

    fun hideToolbar() {
        binding.appBarlayout.customToolbar.toGone()
    }


    fun initializeNavigation() {
        val isLoggedIn = checkUserAuthentication()

        if (isLoggedIn) {
            setMainGraph()
        } else {
            setAuthGraph()
        }
    }

    fun setAuthGraph() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.auth_nav_graph)
        setBottomBarNavigationVisibility(false)

        lifecycleScope.launch {

            delay(2000)
            navController.navigate(R.id.onBoardingFragment)
        }
    }

    fun setMainGraph() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav_graph)
        setupWithNavController(binding.bottomNav, navController)
        setBottomBarNavigationVisibility(true)

    }

    private fun checkUserAuthentication(): Boolean {

        val userToken = appConfig.IS_LOGGED_IN
        return userToken
    }

    fun setBottomBarNavigationVisibility(visible: Boolean) {
        if (visible) {
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }


    fun initializeCustomToolbar(
        title: String,
        toolbarColor: Int? = null,
        showBackButton: Boolean,
        icons: List<Pair<Int, () -> Unit>> = emptyList(),
        onBackPress: () -> Unit
    ) {
        val resolvedColor = ContextCompat.getColor(binding.root.context, toolbarColor!!)
        binding.appBarlayout.customToolbar.setBackgroundColor(resolvedColor)

        binding.appBarlayout.backButton.visibility = if (showBackButton) View.VISIBLE else View.GONE
        binding.appBarlayout.backButton.setOnClickListener {
            onBackPress()
        }

        binding.appBarlayout.toolbarTitle.text = title

        binding.appBarlayout.icon1.toGone()
        binding.appBarlayout.icon2.toGone()
        binding.appBarlayout.icon3.toGone()

        icons.forEachIndexed { index, (iconRes, onClick) ->
            when (index) {
                0 -> {
                    binding.appBarlayout.icon1.setImageResource(iconRes)
                    binding.appBarlayout.icon1.visibility = View.VISIBLE
                    binding.appBarlayout.icon1.setOnClickListener { onClick() }
                }

                1 -> {
                    binding.appBarlayout.icon2.setImageResource(iconRes)
                    binding.appBarlayout.icon2.visibility = View.VISIBLE
                    binding.appBarlayout.icon2.setOnClickListener { onClick() }
                }

                2 -> {
                    binding.appBarlayout.icon3.setImageResource(iconRes)
                    binding.appBarlayout.icon3.visibility = View.VISIBLE
                    binding.appBarlayout.icon3.setOnClickListener { onClick() }
                }
            }
        }
    }


}