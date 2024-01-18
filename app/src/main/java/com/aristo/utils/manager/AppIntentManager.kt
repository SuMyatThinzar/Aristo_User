package com.aristo.utils.manager

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.widget.TextView


fun openDialApp(activity : Activity, phNo : String){

    val intent = Intent(Intent.ACTION_DIAL)
    intent.setData(Uri.parse("tel:$phNo"))
    activity.startActivity(intent)

}

fun openEmailApp(activity : Activity, email : String){
    val emailIntent = Intent(Intent.ACTION_SENDTO)
    emailIntent.data = Uri.parse("mailto:$email")
    activity.startActivity(emailIntent)
}

fun openViberApp(activity: Activity, phoneNo : String) {
//    val packageName = "com.viber.voip"
//
//    val isViberInstalled = isPackageInstalled(activity, packageName)
//    if (isViberInstalled) {
        val viberIntent = Intent(Intent.ACTION_VIEW, Uri.parse("viber://chat?number=%2B$phoneNo"))
        viberIntent.setPackage("com.viber.voip")
        activity.startActivity(viberIntent)
//    } else {
//        // If Viber is not installed, you can redirect the user to the Play Store
//        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
//        activity.startActivity(playStoreIntent)
//    }
}

fun sendMessageToViber(activity: Activity, message : String) {

    val packageName = "com.viber.voip"
    val isViberInstalled = isPackageInstalled(activity, packageName)
    if (isViberInstalled) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setPackage(packageName)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        activity.startActivity(intent)
    } else {
        // If Viber is not installed, you can redirect the user to the Play Store
        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        activity.startActivity(playStoreIntent)
    }

}

fun isPackageInstalled(activity: Activity, packageName: String): Boolean {
    val packageManager: PackageManager = activity.packageManager
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun replacePrefix(input: String): String {
    var modifiedInput = input
    if (input.startsWith("09")){
        modifiedInput = input.replaceFirst("^09".toRegex(), "959")
    }
    else if (input.startsWith("+959")) {
        modifiedInput = input.replaceFirst("^\\+959".toRegex(), "959")
    }

    return modifiedInput
}

fun openFacebookApp(activity: Activity, facebookPageURL : String) {

    try {
        val facebookIntent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookPageURL))
        activity.startActivity(facebookIntent)
    } catch (e: ActivityNotFoundException) {
        // If the Facebook app is not installed, open the Facebook page in a web browser
        val facebookWebIntent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookPageURL))
        activity.startActivity(facebookWebIntent)
    }
}

fun setUnderLineForLinks(textView : TextView){
    textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}