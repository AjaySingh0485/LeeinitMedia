package com.pelkinsoft.shopdm.main


data class NavigationOptionModel(
    var optionName: String = "",
    var optionDrawable: Int = 0,
    var optionSelected: Boolean = false,
    var selectedPosition: Int = 0

)