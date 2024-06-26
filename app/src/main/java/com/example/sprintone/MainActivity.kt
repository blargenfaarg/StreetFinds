package com.example.sprintone

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.sprintone.ui.theme.SprintOneTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : ComponentActivity()
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SprintOneTheme {
                LoadGreeting()
            }
        }
    }
}

object UserType {
    var BUYER = false
    var VENDOR = false
    var GUEST = false
}

@Preview
@Composable
fun LoadGreeting()
{
    val context = LocalContext.current

    if (isUserLoggedIn(context))
    {
        val userType = getUserType(context)
        if (userType != null) {
            when (userType)
            {
                "buyer" -> {
                    UserType.BUYER = true
                    UserType.VENDOR = false
                    UserType.GUEST = false
                }
                "vendor" -> {
                    UserType.VENDOR = true
                    UserType.BUYER = false
                    UserType.GUEST = false
                }
            }
        }
        context.startActivity(Intent(context, ListActivity::class.java))
    }
    else
    {
        LogInScreen()
    }
}


@Composable
fun LogInScreen()
{
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.truckpin),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(150.dp, 150.dp)
            )
            Text(
                text = "StreetFinds",
                textAlign = TextAlign.Center,
                fontSize = 62.sp,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(
                modifier = Modifier.padding(10.dp)
            )
            Row {

                Button(
                    onClick = {
                        UserType.BUYER = true
                        UserType.VENDOR = false
                        UserType.GUEST = false
                        context.startActivity(Intent(context, UserSignUp::class.java))
                    },
                    modifier = Modifier.width(130.dp)
                )
                {
                    Text(
                        text = "Users",
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                }
                Spacer(
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = {
                        UserType.GUEST = false
                        UserType.BUYER = false
                        UserType.VENDOR = true
                        context.startActivity(Intent(context, VendorSignUp::class.java))
                    },
                    modifier = Modifier.width(130.dp)
                )
                {
                    Text(
                        text = "Vendors",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(20.dp)
            )

            OutlinedButton(
                onClick = {
                    UserType.GUEST = true
                    UserType.BUYER = false
                    UserType.VENDOR = false
                    context.startActivity(Intent(context, ListActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(Color.LightGray)
            )
            {
                Text(
                    text = "Continue as a guest",
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun PickImageFromGallery()
{
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val storage = Firebase.storage
    val storageRef = storage.reference
    val scope = rememberCoroutineScope()
    var uploadProgress by remember { mutableStateOf(0f) }
    var uploadStatus by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false)}


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ uri: Uri? ->
        imageUri = uri
    }

        Button(onClick = { launcher.launch("image/*")
        showDialog = true})
        {
            Text(text="Select Image")
        }
        if (imageUri != null && showDialog)
        {
            AlertDialog(onDismissRequest = { showDialog = false }, confirmButton = { Button(onClick = { uploadImageToFirebaseStorage(imageUri!!, storageRef, scope, { progress ->
                uploadProgress = progress
            }, { status ->
                uploadStatus = status
            }, context)
                Timer().schedule(2000)
                {
                    showDialog = false
                }
            }) { Text("Upload Image") } }, text = {
                Column()
                {
                    imageUri?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap.value =
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }
                        bitmap.value?.let { btm ->
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(400.dp)
                                    .padding(20.dp)
                            )
                        }
                    }
                }
            }, title = {Text("Everything look good?", fontWeight = FontWeight.ExtraBold)},
                dismissButton = {Button(onClick = {showDialog=false}){Text("Cancel")} })
        }
}

fun saveImageUrlToFirestore(vendorId: String, imageUrl: String)
{
    Log.e("Vendor ID", "Vendor ID: $vendorId")
    val db = FirebaseFirestore.getInstance()
    val vendorRef = db.collection("vendors").document(vendorId)
    db.runTransaction { transaction ->
        val snapshot = transaction.get(vendorRef)
        val imageUris = snapshot.get("imageUrl") as? MutableList<String> ?: mutableListOf()
        imageUris.add(imageUrl)
        transaction.update(vendorRef, "imageUrl", imageUris)
        null
    }.addOnSuccessListener {
        println("Image URI added to vendor profile successfully!")
    }.addOnFailureListener { e ->
        println("Transaction failure: $e")
    }
}

fun uploadImageToFirebaseStorage(fileUri: Uri, storageRef: StorageReference,
                                 scope: CoroutineScope,
                                 onProgress: (Float) -> Unit,
                                 onStatus: (String) -> Unit, context: Context
) {
    val vendorId = getVendorId(context)
    Log.e("Upload Image To Firebase Storage: ", "vendorID: $vendorId")
    val fileRef = storageRef.child("uploads/$vendorId/${fileUri.lastPathSegment}")
    val uploadTask = fileRef.putFile(fileUri)

    uploadTask.addOnProgressListener { taskSnapshot ->
        val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
        scope.launch {
            onProgress(progress.toFloat() / 100.0f)
        }
    }.addOnFailureListener {
        scope.launch {
            onStatus("Upload failed: ${it.message}")
        }
    }.addOnSuccessListener { taskSnapshot ->
        fileRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            saveImageUrlToFirestore(vendorId.toString(), imageUrl)
        }
        scope.launch {
            onStatus("Upload successful!")
        }
    }
}

@Composable
fun LoadImageFromUrls(imageUrls: List<String>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(imageUrls) { imageUrl ->
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = imageUrl)
                    .apply {
                        crossfade(true)
                    }
                    .build()
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .width(200.dp)
                .height(200.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Loaded Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}



fun saveUserLoggedInState(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("isLoggedIn", isLoggedIn)
    editor.apply()
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}

fun saveVendorId(context: Context, userId: String)
{
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userId", userId)
    editor.apply()
}

fun getVendorId(context: Context): String?
{
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userId", null)
}


fun saveUserType(context: Context, userType: String) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userType", userType)
    editor.apply()
}
fun getUserType(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userType", null)
}

fun saveUserEmail(context: Context, email: String)
{
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("email", email)
    editor.apply()
}

fun getUserEmail(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("email", null)
}

fun saveUserFavorites(context: Context, favorites: List<String>) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putStringSet("favorites", favorites.toSet())
    editor.apply()
}

fun getUserFavorites(context: Context): Set<String>
{
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val favorites = sharedPreferences.getStringSet("favorites", emptySet())
    return favorites ?: emptySet()
}