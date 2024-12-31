/*
 * ************************************************************************************************
 * ***** Copyright (c) 2019. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */

package android.presenter.adapters

/**
 * This class is used for configuring the Tumbler.
 */

data class TumblerElement(
    var newlyAdded: Boolean? = null,
    var groupCount: String? = null,
    var name: String? = null,
    var type: Int = 0,
    var tumblerData: List<String>? = null,
    var group: String? = null,
    var labelName: String? = null,
    var text_header: String? = null,
    var text_imageName: String? = null,
    var text_header_available: String? = null,
    var text_imageName_available: String? = null
) {
    companion object {
        var TEXT_TYPE: Int = 0
        var NUMERIC_TYPE: Int = 1
        var NUMERIC_GROUP_TYPE: Int = 2
        var NUMERIC_TYPE_WITHOUT_DEGREE: Int = 3
    }
}
