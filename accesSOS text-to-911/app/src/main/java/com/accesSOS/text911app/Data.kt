package com.accesSOS.text911app

import android.app.Application
import android.util.Log

class Data : Application() {
    companion object
    {
        fun printTest(message: String)
        {
            Log.d("message ", message)
        }

        fun generateSMSString()
        {
            var longitude = locationInfo.gpsLongitude
            var roundedLongitude = String.format("%.5f", longitude)
            var latitude = locationInfo.gpsLatitude
            var roundedLatitude = String.format("%.7f", latitude)

            var requestingServices = ""
            if (emergencyInfo.police)
            {
                requestingServices += "- Police\n"
            }
            if(emergencyInfo.fire)
            {
                requestingServices += "- Fire\n"
            }
            if(emergencyInfo.medical)
            {
                requestingServices += "- Medical\n"
            }

            var weapons = ""
            if (weaponsInfo.weaponsYes)
            {
                weapons = "Yes"
            }
            else if (weaponsInfo.weaponsNo)
            {
                weapons = "No"
            }
            else if (weaponsInfo.weaponsNotSure)
            {
                weapons = "Unsure"
            }

            var emergencyType : String
            if (optionalDetailSelected() && !optionalDetails.noDetails)
            {
                emergencyType = ""
                if (optionalDetails.physical)
                {
                    emergencyType += "- Physically Unstable\n"
                }
                if (optionalDetails.breathing)
                {
                    emergencyType += "- Breathing Problem\n"
                }
                if (optionalDetails.traffic)
                {
                    emergencyType += "- Traffic Accident\n"
                }
                if (optionalDetails.chest)
                {
                    emergencyType += "- Chest Pain\n"
                }
                if (optionalDetails.social)
                {
                    emergencyType += "- Social Services\n"
                }
                if (optionalDetails.bleeding)
                {
                    emergencyType += "- Bleeding\n"
                }
                if (optionalDetails.assault)
                {
                    emergencyType += "- Assault\n"
                }
                if (optionalDetails.crime)
                {
                    emergencyType += "- Crime Active\n"
                }
            }
            else
            {
                emergencyType = "No specifics provided\n"
            }

            var locationSpecifics : String
            if (!locationDetails.indoors && !locationDetails.outdoors && !locationDetails.moving)
            {
                locationSpecifics = "No specifics provided\n"
            }
            else
            {
                locationSpecifics = ""
                if (locationDetails.indoors)
                {
                    locationSpecifics = "Indoors"
                    if (!locationDetails.indoorFloorNumber.equals(""))
                    {
                        locationSpecifics += "\n- Floor: " + locationDetails.indoorFloorNumber
                    }
                    if (!locationDetails.indoorRoomNumber.equals(""))
                    {
                        locationSpecifics += "\n- Room: " + locationDetails.indoorRoomNumber
                    }
                    locationSpecifics += "\n"
                }
                else if (locationDetails.outdoors)
                {
                    locationSpecifics = "Outdoors"
                    if (!locationDetails.outdoorDetails.equals(""))
                    {
                        locationSpecifics += "\n- Details: " + locationDetails.outdoorDetails
                    }
                    locationSpecifics += "\n"
                }
                else if (locationDetails.moving)
                {
                    locationSpecifics = "Moving"
                    if (!locationDetails.movingBusTrainNumber.equals(""))
                    {
                        locationSpecifics += "\n- Bus/Train: " + locationDetails.movingBusTrainNumber
                    }
                    if (!locationDetails.movingHighwayExitNumber.equals(""))
                    {
                        locationSpecifics += "\n- Highway/Exit: " + locationDetails.movingHighwayExitNumber
                    }
                    locationSpecifics += "\n"
                }
            }

            smsString =
            "Emergency at \n" +
            "Street Address:\n" + locationInfo.address + "\n\n" +
            "Latitude: " + roundedLatitude + "\n" +
            "Longitude: " + roundedLongitude + "\n\n" +
            "Requesting: \n" + requestingServices + "\n" +
            "Weapons: " + weapons + "\n\n" +
            "Type of Emergency: \n" + emergencyType + "\n" +
            "Location Details: \n" + locationSpecifics + "\n\n" +
            "Person may be deaf or unable to speak out loud, may not speak English"

            updateSummaryStrings()
        }

        fun resetAll()
        {
            emergencyInfo.medical = false
            emergencyInfo.police = false
            emergencyInfo.fire = false

            locationInfo.gpsLatitude = 0.0
            locationInfo.gpsLongitude = 0.0
            locationInfo.address = ""
            locationInfo.state = ""
            locationInfo.county = ""

            weaponsInfo.weaponsYes = false
            weaponsInfo.weaponsNo = false
            weaponsInfo.weaponsNotSure = false

            optionalDetails.physical = false
            optionalDetails.breathing = false
            optionalDetails.traffic = false
            optionalDetails.chest = false
            optionalDetails.social = false
            optionalDetails.bleeding = false
            optionalDetails.assault = false
            optionalDetails.crime = false
            optionalDetails.noDetails = false

            locationDetails.indoors = false
            locationDetails.outdoors = false
            locationDetails.moving = false
            locationDetails.indoorRoomNumber = ""
            locationDetails.indoorFloorNumber = ""
            locationDetails.outdoorDetails = ""
            locationDetails.movingBusTrainNumber = ""
            locationDetails.movingHighwayExitNumber = ""
        }

        fun emergencyServicesSelected() : Boolean{
            return emergencyInfo.medical || emergencyInfo.fire || emergencyInfo.police
        }

        fun weaponSelected() : Boolean{
            return weaponsInfo.weaponsYes || weaponsInfo.weaponsNo || weaponsInfo.weaponsNotSure
        }

        fun optionalDetailSelected() : Boolean{
            return optionalDetails.physical || optionalDetails.breathing || optionalDetails.traffic ||
                    optionalDetails.chest || optionalDetails.social || optionalDetails.bleeding ||
                    optionalDetails.assault || optionalDetails.crime || optionalDetails.noDetails
        }

        fun resetOptionalDetails()
        {
            optionalDetails.physical = false
            optionalDetails.breathing = false
            optionalDetails.traffic = false
            optionalDetails.chest = false
            optionalDetails.social = false
            optionalDetails.bleeding = false
            optionalDetails.assault = false
            optionalDetails.crime = false
            optionalDetails.noDetails = true
        }

        fun updateSummaryStrings()
        {
            // Location
            var address = locationInfo.address
            if (address!="none")
            {
                var commaLoc = address.indexOf(",")
                var numberAndStreet = address.substring(0, commaLoc)
                var cityStateZip = address.substring(commaLoc + 2)
                summaryStrings.locationString = numberAndStreet + "\n" + cityStateZip
            }

            // Emergency Services Requested
            var requestingServices = ""
            if (emergencyInfo.police)
            {
                requestingServices += "- Police"
            }
            if(emergencyInfo.fire)
            {
                if (requestingServices.equals(""))
                {
                    requestingServices += "- Fire"
                }
                else
                {
                    requestingServices += "\n- Fire"
                }
            }
            if(emergencyInfo.medical)
            {
                if (requestingServices.equals(""))
                {
                    requestingServices += "- Medical"
                }
                else
                {
                    requestingServices += "\n- Medical"
                }
            }
            summaryStrings.emergencyServicesRequestedString = requestingServices

            // Emergency Details
            var emergencyType: String
            if ((!optionalDetails.physical && !optionalDetails.breathing && !optionalDetails.traffic &&
                !optionalDetails.chest && !optionalDetails.social && !optionalDetails.bleeding &&
                !optionalDetails.assault && !optionalDetails.crime && !optionalDetails.noDetails) || optionalDetails.noDetails)
            {
                emergencyType = "No specifics provided"
            }
            else
            {
                emergencyType = ""
            }
            if (optionalDetails.physical)
            {
                emergencyType += "Physically Unstable"
            }
            if (optionalDetails.breathing)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Breathing Problem"
                }
                else
                {
                    emergencyType += ", Breathing Problem"
                }
            }
            if (optionalDetails.traffic)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Traffic Accident"
                }
                else
                {
                    emergencyType += ", Traffic Accident"
                }
            }
            if (optionalDetails.chest)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Chest Pain"
                }
                else
                {
                    emergencyType += ", Chest Pain"
                }
            }
            if (optionalDetails.social)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Social Services"
                }
                else
                {
                    emergencyType += ", Social Services"
                }
            }
            if (optionalDetails.bleeding)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Bleeding"
                }
                else
                {
                    emergencyType += ", Bleeding"
                }
            }
            if (optionalDetails.assault)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Assault"
                }
                else
                {
                    emergencyType += ", Assault"
                }
            }
            if (optionalDetails.crime)
            {
                if (emergencyType.equals(""))
                {
                    emergencyType += "Crime Active"
                }
                else
                {
                    emergencyType += ", Crime Active"
                }
            }
            summaryStrings.emergencyDetailsString = emergencyType


            // Weapons
            var weapons = ""
            if (weaponsInfo.weaponsYes)
            {
                weapons = "Yes"
            }
            else if (weaponsInfo.weaponsNo)
            {
                weapons = "No"
            }
            else if (weaponsInfo.weaponsNotSure)
            {
                weapons = "Unsure"
            }
            summaryStrings.weaponsString = weapons

            // Location Details
            var locationDetailsString = "No specifics provided"
            if (locationDetails.indoors)
            {
                locationDetailsString = "Indoors"
                if (locationDetails.indoorRoomNumber != "")
                {
                    locationDetailsString += "\n- Apartment/Room # " + locationDetails.indoorRoomNumber
                }
                if (locationDetails.indoorFloorNumber != "")
                {
                    locationDetailsString += "\n- Floor # "+ locationDetails.indoorFloorNumber
                }
            }
            else if (locationDetails.outdoors)
            {
                locationDetailsString = "Outdoors"
                if (locationDetails.outdoorDetails != "")
                {
                    locationDetailsString += "\n- " + locationDetails.outdoorDetails
                }
            }
            else if (locationDetails.moving)
            {
                locationDetailsString = "Moving"
                if (locationDetails.movingBusTrainNumber != "")
                {
                    locationDetailsString += "\n- Bus/Train # " + locationDetails.movingBusTrainNumber
                }
                if (locationDetails.movingHighwayExitNumber != "")
                {
                    locationDetailsString += "\n- Highway/Exit # " + locationDetails.movingHighwayExitNumber
                }
            }
            summaryStrings.locationDetailsString = locationDetailsString
        }

        var smsString : String = ""
        var emergencyInfo : EmergencyInfo = EmergencyInfo()
        var locationInfo : LocationInfo = LocationInfo()
        var weaponsInfo : WeaponsInfo = WeaponsInfo()
        var locationDetails : LocationDetails = LocationDetails()
        var optionalDetails : OptionalDetails = OptionalDetails()
        var summaryStrings : SummaryStrings = SummaryStrings()

        var countyData = HashMap<String, HashMap<String, Boolean>>()
    }

    class SummaryStrings
    {
        var locationString : String = ""
        var emergencyServicesRequestedString = ""
        var emergencyDetailsString = ""
        var weaponsString = ""
        var locationDetailsString = ""
    }

    class EmergencyInfo
    {
        var medical : Boolean = false
        var police : Boolean = false
        var fire : Boolean = false
    }

    class LocationInfo
    {
        // SMS info set in MapActivity.kt
        var gpsLatitude : Double = 0.0
        var gpsLongitude : Double = 0.0
        var address : String = ""
        var state : String = ""
        var county : String = ""
    }

    class WeaponsInfo
    {
        var weaponsYes : Boolean = false
        var weaponsNo : Boolean = false
        var weaponsNotSure : Boolean = false
    }

    class OptionalDetails
    {
        var physical : Boolean = false
        var breathing : Boolean = false
        var traffic : Boolean = false
        var chest : Boolean = false
        var social : Boolean = false
        var bleeding : Boolean = false
        var assault : Boolean = false
        var crime : Boolean = false
        var noDetails : Boolean = false
    }

    class LocationDetails
    {
        var indoors : Boolean = false
        var outdoors : Boolean = false
        var moving : Boolean = false

        var indoorRoomNumber : String = ""
        var indoorFloorNumber : String = ""

        var outdoorDetails : String = ""

        var movingBusTrainNumber : String = ""
        var movingHighwayExitNumber : String = ""
    }

}