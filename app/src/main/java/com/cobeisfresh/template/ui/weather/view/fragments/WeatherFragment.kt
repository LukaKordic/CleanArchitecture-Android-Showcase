package com.cobeisfresh.template.ui.weather.view.fragments

import com.cobeisfresh.template.R
import com.cobeisfresh.template.common.convertKelvinToCelsius
import com.cobeisfresh.template.common.extensions.hideKeyboard
import com.cobeisfresh.template.common.extensions.onClick
import com.cobeisfresh.template.common.extensions.subscribe
import com.cobeisfresh.template.ui.base.BaseFragment
import com.cobeisfresh.template.ui.base.ViewState
import com.cobeisfresh.template.ui.weather.presentation.WeatherViewModel
import com.example.domain.model.WeatherInfo
import kotlinx.android.synthetic.main.fragment_weather.*
import org.koin.android.viewmodel.ext.android.viewModel

class WeatherFragment : BaseFragment() {
  
  private val viewModel: WeatherViewModel by viewModel()
  
  override fun viewReady() {
    viewModel.getWeatherForLocation()
    subscribeToData()
    getWeather.onClick {
      weatherActivityContainer.hideKeyboard()
      viewModel.getWeatherForLocation(cityInput.text.toString())
    }
    
    showWeatherDetails.onClick { activity?.run { appFragmentNavigator.showWeatherDetails(this) } }
  }
  
  override fun getLayout() = R.layout.fragment_weather
  
  private fun subscribeToData() {
    viewModel.weatherLiveData.subscribe(this, ::handleViewState)
  }
  
  private fun handleViewState(viewState: ViewState<WeatherInfo>) {
    with(viewState) {
      when (status) {
        ViewState.Status.LOADING -> showLoading(weatherLoadingProgress)
        ViewState.Status.SUCCESS -> data?.run(::showWeatherData)
        ViewState.Status.ERROR -> {
          hideLoading(weatherLoadingProgress)
          showError(error?.message, weatherActivityContainer)
        }
      }
    }
  }
  
  private fun showWeatherData(weatherInfo: WeatherInfo) {
    hideLoading(weatherLoadingProgress)
    temperature.text = convertKelvinToCelsius(weatherInfo.temperature)
    pressure.text = weatherInfo.pressure.toString()
    humidity.text = weatherInfo.humidity.toString()
  }
  
  companion object {
    fun newInstance() = WeatherFragment()
  }
}

