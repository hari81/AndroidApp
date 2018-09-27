package au.com.infotrak.infotrakmobile.datastorage.MSI

import au.com.infotrak.infotrakmobile.entityclasses.MSI.MSI_Image

interface MSI_Model_DB_Interface {

    // Equipment images
    fun deleteEquipmentMandatoryImg(imagePath: String): Boolean?
    fun selectEquipmentMandatoryImage(image: MSI_Image): Long

    // Roll back
    fun showSyncedEquipment()
}