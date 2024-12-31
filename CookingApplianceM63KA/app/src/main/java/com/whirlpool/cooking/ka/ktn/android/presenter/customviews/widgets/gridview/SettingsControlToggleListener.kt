/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview

/**
 * Interface to get the callbacks of the settings widget.
 */
interface SettingsControlToggleListener {
    /**
     * returns the state of control lock setting toggle switch when clicked.
     *
     * @param isChecked true if checked, false otherwise
     */
    fun onControlLockToggled(isChecked: Boolean, gridListItemModel: GridListItemModel)

    /**
     * returns the state of mute setting toggle switch when clicked.
     *
     * @param isChecked true if checked, false otherwise
     */
    fun onMuteToggled(isChecked: Boolean)

    /**
     * returns the state of remote enable setting toggle switch when clicked.
     *
     * @param isChecked true if checked, false otherwise
     */
    fun onRemoteEnableToggled(isChecked: Boolean)

    fun showNotificationLockNotAvailable()

    fun showNotificationDemoNotAvailableInTechnicianMode()
}