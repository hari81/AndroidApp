package au.com.infotrak.infotrakmobile.business.MSI

import java.util.ArrayList

interface MSI_Model_API_Interface {

    // Equipment images
    fun getEquipmentMandatoryImages(customerAuto: Long, modelAuto: Long): ArrayList<ArrayList<String>>

    // Sync
    fun PostImage(json: String): Boolean

    // Post data
    fun syncValidateEquipment(json: String): String
    fun syncEquipment(json: String): Boolean
}